package com.my.saas.system.sqlprovider;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Transient;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.util.Assert;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.my.saas.common.model.User;
import com.my.saas.common.myannotation.TableHistory;

/**
 * the base provider for dao CRUD and batch CRUD
 * this class doesn't consider the null pointer 
 * @author chen.dailiang
 */
public class AbstractBusinessSqlProvider {
	
	/**
	 * 获取实体类中的所有字段
	 * 2019年5月24日
	 * @param entity
	 * @return
	 * @Author chen.dailiang
	 */
	private static <T> List<String> getAllField(T entity,boolean isMainTable) {
		Assert.notNull(entity, "entity can not be null !");
		Class<?> cz = entity.getClass();
		List<Field> temp = Arrays.asList(cz.getDeclaredFields());
		List<Field> list = Lists.newCopyOnWriteArrayList(temp);
		cz = cz.getSuperclass();
		do{
			list.addAll(Arrays.asList(cz.getDeclaredFields()));
			cz = cz.getSuperclass();
		}while(cz != null);
		Assert.notEmpty(list, "there is no field at this entity !");
		return isMainTable ? list.stream().filter(t ->(!Objects.equal("serialVersionUID", 
				t.getName()) && (!t.isAnnotationPresent(Transient.class)) && !Objects.equal("id", t.getName()) 
				) && !(t.isAnnotationPresent(TableField.class) && !t.getAnnotation(TableField.class).exist())
				).map(Field::getName).collect(Collectors.toList())
		: list.stream().filter(t ->(!Objects.equal("serialVersionUID", 
				t.getName()) && (!t.isAnnotationPresent(Transient.class)) && !Objects.equal("id", t.getName()) 
				) && !((t.isAnnotationPresent(TableHistory.class) && !t.getAnnotation(TableHistory.class).exist()) )
				).map(Field::getName).collect(Collectors.toList());
	}
	
	/**
	 * join str with coma 
	 * 2019年5月27日
	 * @param pattern
	 * @param list
	 * @return  values of mybatis need
	 * @Author chen.dailiang
	 */
	private static <T> String formatIdList(String pattern,List<T> list){
		MessageFormat messageFormat = new MessageFormat(pattern);
		StringBuffer idsBuffer = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			idsBuffer.append(messageFormat.format(new Object[] { i }));
			if (i < list.size() - 1) {
				idsBuffer.append(",");
			}
		}
		return idsBuffer.toString();
	}
	
	/**
	 * true:main table
	 * false:history table
	 * 2019年6月4日
	 * @param entity
	 * @param isMainTable
	 * @return
	 * @Author chen.dailiang
	 */
	private static <T> String getInsertSql(T t,boolean isMainTable){
		SQL sql = new SQL();
		String tableName = isMainTable ? t.getClass().getAnnotation(TableName.class).value():t.getClass().getAnnotation(TableHistory.class).value();
		String idValue = isMainTable ? "} , #{entity.id}" :"} ,replace(uuid(),'-','')";
		sql.INSERT_INTO(tableName);
		List<String> allFieldName = getAllField(t,isMainTable);
		String [] allColum = allFieldName.stream().map(f->CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,f)).toArray(String[]::new);
		String [] id  = {"id"};
		String [] all = ArrayUtils.addAll(allColum, id);
		sql.INTO_COLUMNS(all);
		String values = allFieldName.stream().collect(Collectors.joining("},#{entity.","#{entity.",idValue));
		String newValues = values.replace("#{entity.refId}", "#{entity.id}"); 
		sql.INTO_VALUES(Arrays.asList(newValues.split(",")).stream().toArray(String[]::new) );
		return sql.toString();
	}
	/**
	 * true:main table
	 * false:history table
	 * 2019年6月4日
	 * @param entity
	 * @param isMainTable
	 * @return
	 * @Author chen.dailiang
	 */
	private static <T> String getInsertListSql(List<T> list ,boolean isMainTable){
		T t = list.get(0);
		String tableName = isMainTable ? t.getClass().getAnnotation(TableName.class).value():t.getClass().getAnnotation(TableHistory.class).value();
		String endValue = !isMainTable ? "},replace(uuid(),''-'','''') )" : "}, #'{'list[{0}].id })";
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO ").append(tableName);
		List<String> allFieldName = getAllField(t,isMainTable);
		String columns = allFieldName.stream().map(f->CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,f)).collect(Collectors.joining(","," (",",id ) VALUES "));
		sql.append(columns);
		String values =allFieldName.stream().collect(Collectors.joining("}, #'{'list[{0}].","( #'{'list[{0}].", endValue));
		sql.append(formatIdList(values, list));
		String finalStr = sql.toString().replace("].refId}", "].id}");
		return finalStr;
	}
	
	/**
	 * 新增修改记录
	 * 2019年5月22日
	 * @param entity
	 * @param user
	 * @param obj
	 * @return 
	 * @Author chen.dailiang
	 */
	public static <T> String insertModify(@Param("entity") T entity, @Param("user") User user){
		return getInsertSql(entity,false);
	}
	
	/**
	 * 新增主表数据
	 * 2019年6月4日
	 * @param entity
	 * @param user
	 * @return
	 * @Author chen.dailiang
	 */
	public static <T> String insertEntity(@Param("entity") T entity, @Param("user") User user){
		return getInsertSql(entity,true);
	}
	
	/**
	 * 批量增加修改记录
	 * 2019年5月22日
	 * @param list
	 * @param user
	 * @param t
	 * @return
	 * @Author chen.dailiang
	 */
	public static <T> String insertModifyList(@Param("list") List<T> list, @Param("user") User user){
		return getInsertListSql(list, false);
	}
	/**
	 * 批量增加主表数据
	 * 2019年5月22日
	 * @param list
	 * @param user
	 * @param t
	 * @return
	 * @Author chen.dailiang
	 */
	public static <T> String insertEntityList(@Param("list") List<T> list, @Param("user") User user){
		return getInsertListSql(list, true);
	}
	
	/**
	 * 通过idList获取List<Entity>
	 * 2019年5月22日
	 * @param <T>
	 * @param list
	 * @param user
	 * @return
	 * @Author chen.dailiang
	 */
	public static <T> String getEntityByIdList(@Param("list")List<String> list, @Param("user")User user, Class<T> t){
		SQL sql = new SQL();
		sql.SELECT(" * ").FROM(t.getAnnotation(TableName.class).value());
		String whereString = list.stream().collect(Collectors.joining("','", "'", "'"));
		sql.WHERE("ID IN ("+whereString+")");
		return sql.toString();
	}
	
	/**
	 * 通过business_id删除
	 * 2019年5月22日
	 * @param businessId
	 * @param obj
	 * @return
	 * @Author chen.dailiang
	 */
	public static <T>String deleteEntityByBusinessId(@Param("businessId")String businessId, @Param("user")User user, Class<T> t) {
		SQL sql = new SQL();
		sql.DELETE_FROM(t.getAnnotation(TableName.class).value());
		sql.WHERE("BUSINESS_ID = #{businessId} AND TENANT_ID=#{user.tenantId}");
		return sql.toString();
	}
	
	/**
	 * 通过business id批量删除
	 * 2019年5月27日
	 * @return
	 * @Author chen.dailiang
	 */
	public static <T> String deleteEntityByBusinessIdList(@Param("list")List<String> bidList, @Param("user")User user,  Class<T> t){
		SQL sql = new SQL();
		sql.DELETE_FROM(t.getAnnotation(TableName.class).value());
		String ids = "#'{'list[{0}]}";
		String idsBuffer = formatIdList(ids, bidList);
		sql.WHERE("BUSINESS_ID IN ("+idsBuffer+") AND TENANT_ID=#{user.tenantId} ");
		return sql.toString();
	}
	/**
	 * get entity by business id 
	 * 2019年5月27日
	 * @param businessId
	 * @param user
	 * @param t
	 * @Author chen.dailiang
	 */
	public static <T> String getEntityByBid(@Param("bid")String businessId, @Param("user")User user,@Param("t")Class<T> t){
		SQL sql = new SQL();
		sql.SELECT("*").FROM(t.getAnnotation(TableName.class).value());
		sql.WHERE("BUSINESS_ID = #{bid} AND TENANT_ID=#{user.tenantId} AND OPERATION <> 1");
		return sql.toString();
	}
	/**
	 * 
	 * 2019年5月28日
	 * @param list
	 * @param user
	 * @param t
	 * @return
	 * @Author chen.dailiang
	 */
	public static <T> String getEntityByBIdList(@Param("list")List<String> list, @Param("user")User user,Class<T> t){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM ").append(t.getAnnotation(TableName.class).value()).append(" WHERE BUSINESS_ID IN (");
		String ids = "#'{'list[{0}]}";
		String idValue = formatIdList(ids, list);
		sql.append(idValue).append(" ) AND TENANT_ID=#{user.tenantId} AND OPERATION <> 1");
		return sql.toString();
	}
	
	/**
	 * get modify records by ref id
	 * 2019年5月28日
	 * @param refId
	 * @param user
	 * @param t
	 * @return
	 * @Author chen.dailiang
	 */
	public static <T> String getModifyByRefId(@Param("refId")String refId, @Param("user") User user,Class<T> t){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM ").append(t.getAnnotation(TableHistory.class).value()).append(" WHERE 1=1 AND ");
		sql.append("ref_id = #{refId} AND TENANT_ID=#{user.tenantId}" );
		return sql.toString();
	}
	
	/**
	 * batch delete by id list
	 * 2019年5月28日
	 * @param list
	 * @param user
	 * @param t
	 * @Author chen.dailiang
	 */
	public static <T> String deleteEntityByIdList(@Param("list") List<String> list, @Param("user") User user,Class<T> t){
		SQL sql = new SQL();
		sql.DELETE_FROM(t.getAnnotation(TableName.class).value());
		String ids = "#'{'list[{0}]}";
		String idsBuffer = formatIdList(ids, list);
		sql.WHERE("ID IN ("+idsBuffer+") AND TENANT_ID=#{user.tenantId}");
		return sql.toString();
	}
	
	/**
	 * logic delete by ids or businessId
	 * 2019年7月10日
	 * @Author chen.dailiang
	 * @param isId true:id,false:businessId
	 * @param list
	 * @param user
	 * @param t
	 * @return
	 */
	public static <T> String logicDelByIds(@Param("list") List<String> list, @Param("user") User user,Class<T> t,boolean isId){
		String tableName = t.getAnnotation(TableName.class).value();
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE ").append(tableName).append(" SET OPERATION = 1 WHERE 1=1 AND ");
		String field = isId ? " ID " : " BUSINESS_ID ";
		sql.append(field).append("IN ( ");
		String ids = "#'{'list[{0}]}";
		sql.append(formatIdList(ids, list)).append(")").append(" AND TENANT_ID = #{user.tenantId}");	
		return sql.toString();
	}
	
	/**
	 * batch update
	 * 2019年7月9日
	 * @param list
	 * @param user
	 * @return
	 */
	public static <T> String batchUpdate(@Param("list")List<T> list, @Param("user") User user){
		T t = list.get(0);
		List<String> allField = getAllField(t,true);
		String tableName = t.getClass().getAnnotation(TableName.class).value();
		StringBuffer sql = new StringBuffer();
		StringBuffer pattern =null;
		MessageFormat messageFormat = null;
		for(int i =0,j=list.size();i<j;i++){
			pattern = new StringBuffer();
			sql.append("UPDATE ").append(tableName).append(" SET ");
			String p = "#'{'list[{0}].";
			messageFormat = new MessageFormat(p);
			String format = messageFormat.format(new Object[] { i });
			for(int k = 0;k<allField.size();k++){
				String singleField = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,allField.get(k));
				pattern.append(singleField).append("=").append(format).append(allField.get(k)).append("}");
				if(k<allField.size()-1){
					pattern.append(",");
				}
			}
			sql.append(pattern).append(" where id = ").append(format).append("id} and tenant_Id = #{user.tenantId} ;" );
		}
		return sql.toString();
	}
	
	
	
	
	
	
	
}
