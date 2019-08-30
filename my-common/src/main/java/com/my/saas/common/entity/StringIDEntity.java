package com.my.saas.common.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class StringIDEntity  implements Serializable {
	
    private static final long serialVersionUID = 1L;

    private String id;
    
}

