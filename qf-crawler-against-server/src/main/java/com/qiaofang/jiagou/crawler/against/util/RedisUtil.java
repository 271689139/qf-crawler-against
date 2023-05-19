package com.qiaofang.jiagou.crawler.against.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.google.common.collect.Lists;
import com.qiaofang.core.cacheclient.CoredisTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redis工具类
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2018/8/13 上午10:47
 */
@Component
@Slf4j
public class RedisUtil {

    @Autowired
    private CoredisTemplate<String, Object> coredisTemplate;

    /**
     * isExistsKey
     *
     * @param redisKey
     * @return
     */
    public boolean isExistsKey(String redisKey) {
        return coredisTemplate.hasKey(redisKey);
    }

    /**
     * set
     *
     * @param key
     * @param value
     * @param time
     * @param timeUnit
     */
    public void set(String key, Object value, int time, TimeUnit timeUnit) {
        coredisTemplate.opsForValue().set(key, value, time, timeUnit);
    }


    /**
     * 从value类型获取value
     *
     * @param key
     * @param clazz
     * @return
     */
    public <T> T get(String key, Class<T> clazz) {
        return clazz.cast(coredisTemplate.opsForValue().get(key));
    }

    /**
     * 获取redis的list缓存
     *
     * @param key
     * @param clazz
     * @return
     * @author jijun
     * @date 2014年7月10日
     */
    public <T> List<T> getList(String key, Class<T> clazz) {
        List<Object> value = (List<Object>) coredisTemplate.opsForValue().get(key);
        List<T> result = Lists.newArrayList();
        if (value == null || value.isEmpty()) {
            return result;
        }
        for (Object o : value) {
            result.add(clazz.cast(o));
        }
        return result;
    }

    /**
     * 获取redis hash中某个key的list缓存
     *
     * @param key
     * @param hashKey
     * @param clazz
     * @return
     * @author jijun
     * @date 2014年7月10日
     */
    public <T> List<T> hGetList(String key, String hashKey, Class<T> clazz) {
        String objectJson = (String) coredisTemplate.opsForHash().get(key, hashKey);
        if (!StringUtils.isBlank(objectJson)) {
            return JSON.parseArray(objectJson, clazz);
        }
        return new ArrayList<>();
    }

    /**
     * hSet
     *
     * @param key
     * @param hashKey
     * @param value
     */
    public void hSet(String key, String hashKey, Object value) {
        this.hSet(key, hashKey, value, 24, TimeUnit.HOURS);
    }

    /**
     * hSet
     *
     * @param key
     * @param hashKey
     * @param value
     * @param time
     * @param timeUnit
     */
    public void hSet(String key, String hashKey, Object value, int time, TimeUnit timeUnit) {
        if (!Objects.isNull(value)) {
            coredisTemplate.opsForHash().put(key, hashKey, JSON.toJSONString(value));
            coredisTemplate.expire(key, time, timeUnit);
        }
    }


    /**
     * hGet
     *
     * @param key
     * @param hashKey
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T hGet(String key, String hashKey, Class<T> clazz) {
        String objectJson = (String) coredisTemplate.opsForHash().get(key, hashKey);
        if (StringUtils.isBlank(objectJson)) {
            return null;
        }
        return JSON.parseObject(objectJson, clazz);
    }

    /**
     * 获取哈希表中所有值
     *
     * @param key
     * @return
     */
    public List<Object> hValues(String key) {
        return coredisTemplate.opsForHash().values(key);
    }

    /**
     * 获取所有给定字段的值
     *
     * @param key
     * @param fields
     * @return
     */
    public List<Object> hMultiGet(String key, Collection<Object> fields) {
        return coredisTemplate.opsForHash().multiGet(key, fields);
    }

    /**
     * hPutAll
     *
     * @param key
     * @param maps
     */
    public void hPutAll(String key, Map<String, String> maps, long timeout, TimeUnit unit) {
        coredisTemplate.opsForHash().putAll(key, maps);
        coredisTemplate.expire(key, timeout, unit);
    }

    /**
     * 将所有指定的值插入到存于 key 的列表的头部。
     * 如果 key 不存在，那么在进行 push 操作前会创建一个空列表。
     * 如果 key 对应的值不是一个 list 的话，那么会返回一个错误。
     * @param key
     * @param values
     * @return
     */
    public Long leftPush(String key, Object... values) {
        return coredisTemplate.opsForList().leftPushAll(key, values);
    }

    /**
     * 设置过期时间
     *
     * @param key
     * @param timeout
     * @param unit
     * @return
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return coredisTemplate.expire(key, timeout, unit);
    }


    /**
     * 分布式锁
     *
     * @param key
     * @param expireTime
     * @param timeUnit
     * @param process
     * @return
     */
    public void lock(final String key, final int expireTime, TimeUnit timeUnit, IProcess process) {
        long now = System.currentTimeMillis();
        boolean hasLock = false;
        try {
            try {
                if (!BooleanUtils.toBoolean(this.setNx(key, now))) {
                    Long value = this.get(key, Long.class);
                    if (value != null) {
                        if ((now - value) > timeUnit.toMillis(value)) {
                            log.info("Occupy key long time,key:{}, use time:{}s，force release key!", key, timeUnit.toSeconds(value));
                            this.delete(key);
                        }
                        throw new RuntimeException("Operation is too frequent. Please wait for a second try.");
                    }
                } else {
                    hasLock = true;
                    this.expire(key, expireTime, timeUnit);
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                log.error("get redis lock error,key:{}", key, e);
            }
            if (hasLock) {
                process.process();
                return;
            }
            throw new RuntimeException("Occupy key failed");
        } finally {
            if (hasLock) {
                Long value = this.get(key, Long.class);
                if (value != null && value != now) {
                    log.error("The value of the lock is not consistent with the setting time. now lock value:{},Previously set value:{}", value, now);
                }
                this.delete(key);
            }
        }
    }

    /**
     * expire
     *
     * @param key
     * @param expireTime
     * @param timeUnit
     */
    public void expire(String key, int expireTime, TimeUnit timeUnit) {
        coredisTemplate.expire(key, expireTime, timeUnit);
    }


    /**
     * delete
     *
     * @param key
     */
    public void delete(String key) {
        coredisTemplate.delete(key);
    }

    /**
     * 删除redis某个key的缓存
     *
     * @param key
     * @author jijun
     * @date 2014年7月10日
     */
    public void hDelete(String key, Object... hashKey) {
        coredisTemplate.opsForHash().delete(key, hashKey);
    }

    public Boolean setNx(final String key, final Object value) {
        return coredisTemplate.execute((RedisCallback<Boolean>) connection -> {
            SerializeWriter out = new SerializeWriter();
            JSONSerializer serializer = new JSONSerializer(out);
            serializer.write(value);
            return connection.setNX(key.getBytes(), out.toBytes("utf-8"));
        });
    }

    /**
     * 添加redis的map缓存
     *
     * @param redisKey
     * @param num
     */
    public Long increment(String redisKey, long num) {
        return coredisTemplate.opsForValue().increment(redisKey, num);
    }


    /**
     * 频率控制
     *
     * @param limitKey
     * @param limitTime
     * @param timeUnit
     * @return 返回当前key是否在限定频率内
     */
    public boolean frequencyControl(String limitKey, int limitTime, TimeUnit timeUnit) {
        Long value = this.get(limitKey, Long.class);
        if (value != null) {
            if (System.currentTimeMillis() - value > timeUnit.toMillis(limitTime)) {
                //防止锁一直没释放
                this.delete(limitKey);
                return true;
            }
            return false;
        }
        this.set(limitKey, System.currentTimeMillis(), limitTime, timeUnit);
        return true;
    }


}
