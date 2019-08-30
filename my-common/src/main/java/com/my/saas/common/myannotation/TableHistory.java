package com.my.saas.common.myannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 历史表的注解
 * @Author ni.hua
 * @Commnet
 * @Date 2019年8月30日
 * @modify-note
 */
@Target({ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableHistory {
	/**table name **/
    String value() default "";
    /**the field of history table is exist*/
    boolean exist() default true;
}
