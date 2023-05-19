package com.qiaofang.jiagou.crawler.against.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * @author shihao.liu
 * @version 1.0
 * @date 2019/8/30 3:34 下午
 */
public class BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
