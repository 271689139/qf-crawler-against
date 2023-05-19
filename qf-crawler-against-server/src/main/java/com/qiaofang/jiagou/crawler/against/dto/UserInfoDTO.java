package com.qiaofang.jiagou.crawler.against.dto;

import lombok.Data;

import java.io.Serializable;


@Data
public class UserInfoDTO implements Serializable {

    /**
     * 用户Id
     */
    private String userId;
    /**
     * 姓名
     */
    private String name;
    /**
     * 岗位
     */
    private String position;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 头像
     */
    private String avatar;
    /**
     * ldap帐号
     */
    private String ldap;
    /**
     * 部门ID
     */
    private String departId;
    /**
     * 部门名称
     */
    private String departName;
    /**
     * 完全部门名称
     */
    private String fullDepartName;

}
