package com.my.saas.common.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.my.saas.common.myannotation.TableHistory;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class BaseEntity extends StringIDEntity {
	
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    @TableHistory(exist = true)
    private String refId;//关联ID
	
    private String createUserId;//创建人ID

    private String createUserName;//创建人

    private Date createTime;//创建时间

    private String modifyUserId;//修改人ID
	
    private String modifyUserName;//修改人

    private Date modifyTime;//修改时间

    private int operation;//操作状态（0：新增，1：删除，2：修改）

    private String tenantId;//租户ID
    
}

