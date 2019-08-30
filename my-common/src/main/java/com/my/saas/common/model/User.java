package com.my.saas.common.model;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户对象
 *
 * @author 国栋
 *
 */
@Data
@TableName("t_user")
@Accessors(chain=true)
public class User  {

	private static final long serialVersionUID = 1L;

	private Long showOrder;

	/**  **/
	private String id;

	/** 名字 **/
	private String name;

	/** 登录名 **/
	private String loginName;

	/** 年龄 **/
	private Integer age;

	/** 地址 **/
	private String address;

	/** 性别 **/
	private Integer gender;

	/** 邮箱 **/
	private String email;

	/** 出生年月 **/
	private Date birthDay;

	/** 手机 **/
	private String mobile;

	/** 电话 **/
	private String telephone;

	/** 头像图片url **/
	private String picUrl;

	/** 说明注释 **/
	private String comments;

	/** 隔离ID **/
	private String tenantId;

	/** 数据状态 0有效，1无效 **/
	private Integer status;

	private String identityId;

	private Integer isSuperadmin;

	@TableField(exist = false)
	private String organizeId;
	
	@TableField(exist = false)
	private String organizeName;

}
