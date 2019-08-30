package com.my.saas.common.utils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.cglib.beans.BeanMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.my.saas.common.entity.StringIDEntity;
import com.my.saas.common.model.PagerModel;


public class CommonUtil {
	
    /**
     * 创建唯一ID
     *
     * @return
     */
    public static String createId() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    /**
     * 删除数组中重复元素
     */
    public static String[] toDiffArray(String[] s) {
        Set<String> set = new HashSet<String>();
        for (String sa : s) {
            set.add(sa);
        }
        return set.toArray(new String[]{});
    }

    /**
     * 去除map中空的内容
     * @Author ni.hua
     * @Commnet
     * @Date 2019年8月30日
     * @param map
     */
    public static void mapClean(final Map<String, Object> map) {
        for (Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Object> item = it.next();
            Object val = item.getValue();
            if (val instanceof String && val.toString().length() == 0) {
                it.remove();
            }
        }
    }

    /**
     * 获取对象中的value值
     * @param obj 对象
     * @param filed get的具体字段名
     * @return
     */
    public static Object getMethod(Object obj, String filed) {
        Object o = null;
        try {
            Class clazz = obj.getClass();
            PropertyDescriptor pd = new PropertyDescriptor(filed, clazz);
            Method getMethod = pd.getReadMethod();//获得get方法
            if (pd != null) {
                o = getMethod.invoke(obj);//执行get方法返回一个Object
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return o;
    }
    
    /**
     * 将class2中的属性值赋值给class1，如果class1属性有值，则不覆盖 
    *              ，前提条件是有相同的属性名
     * @param class1 基准类,被赋值对象
     * @param class2 提供数据的对象
     */
    public static void copyJavaBean(Object class1, Object class2) {  
        try {  
            Class<?> clazz1 = class1.getClass();  
            Class<?> clazz2 = class2.getClass();  
            // 得到method方法  
            Method[] method1 = clazz1.getMethods();  
            Method[] method2 = clazz2.getMethods();  
   
            int length1 = method1.length;  
            int length2 = method2.length;  
            if (length1 != 0 && length2 != 0) {  
                // 创建一个get方法数组，专门存放class2的get方法。  
                Method[] get = new Method[length2];  
                for (int i = 0, j = 0; i < length2; i++) {  
                    if (method2[i].getName().indexOf("get") == 0) {  
                        get[j] = method2[i];  
                        ++j;  
                    }  
                }  
   
                for (int i = 0; i < get.length; i++) {  
                    if (get[i] == null)// 数组初始化的长度多于get方法，所以数组后面的部分是null  
                        continue;  
                    // 得到get方法的值，判断时候为null，如果为null则进行下一个循环  
                    Object value = get[i].invoke(class2, new Object[] {});  
                    if (null == value)  
                        continue;  
                    // 得到get方法的名称 例如：getXxxx  
                    String getName = get[i].getName();  
                    // 得到set方法的时候传入的参数类型，就是get方法的返回类型  
                    Class<?> paramType = get[i].getReturnType();  
                    Method getMethod = null;  
                    try {  
                        // 判断在class1中时候有class2中的get方法，如果没有则抛异常继续循环  
                        getMethod = clazz1.getMethod(getName, new Class[] {});  
                    } catch (NoSuchMethodException e) {  
                        continue;  
                    }  
                    // class1的get方法不为空并且class1中get方法得到的值为空，进行赋值，如果class1属性原来有值，则跳过  
                    if (null == getMethod || null != getMethod.invoke(class1, new Object[] {}))  
                        continue;  
                    // 通过getName 例如getXxxx 截取后得到Xxxx，然后在前面加上set，就组装成set的方法名  
                    String setName = "set" + getName.substring(3);  
                    // 得到class1的set方法，并调用  
                    Method setMethod = clazz1.getMethod(setName, paramType);  
                    setMethod.invoke(class1, value);  
                }  
            }  
        } catch(Exception e) {  
        	System.out.println(e);
        	e.printStackTrace();
        }  
    }  
    
    /**
     * 循环遍历list中的map，将map中key都是“key”的值，转成一个list返回
     * @Author ni.hua
     * @Commnet
     * @Date 2019年8月30日
     * @param mapList
     * @param key 需要取value的map中的key
     * @return
     */
    public static List<Object> convertListValue(List<Map<String, Object>> mapList, String key) {
        List<Object> idlist = new ArrayList<Object>();
        for (Map<String, Object> m : mapList) {
            if (m.get(key) != null) {
                idlist.add(m.get(key).toString());
            }
        }
        return idlist;
    }

    /**
     * 把LIST转换成PagerModel返回
     */
    public static PagerModel listToPagerModel(Map<String, Object> map, List l) {
        int pageSize = Integer.parseInt((String) map.get("P_pagesize"));
        int total = 0;
        if (l.size() % pageSize == 0) {
            total = l.size() / pageSize;
        } else {
            total = l.size() / pageSize + 1;
        }
        int pageNumber = Integer.parseInt((String) map.get("P_pageNumber"));
        PagerModel pm = new PagerModel();
        pm.setPageNumber(pageNumber);
        pm.setTotal(l.size());
        pm.setTotalPages(total);
        if (l.size() > pageSize * pageNumber) {
            pm.setData(l.subList(pageSize * (pageNumber - 1), pageSize * pageNumber));
        } else {
            pm.setData(l.subList(pageSize * (pageNumber - 1), l.size()));
        }
        return pm;
    }


    /**
     * 将List<T>转换为List<Map<String, Object>>
     *
     * @param objList
     * @return
     */
    public static <T> List<Map<String, Object>> listBeanToListMap(List<T> objList) {
        List<Map<String, Object>> list = Lists.newArrayList();
        if (objList != null && objList.size() > 0) {
            Map<String, Object> map = null;
            T bean = null;
            for (int i = 0, size = objList.size(); i < size; i++) {
                bean = objList.get(i);
                map = beanToMap(bean);
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 将对象装换为map
     *
     * @param bean
     * @return
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = Maps.newHashMap();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(key + "", beanMap.get(key));
            }
        }
        return map;
    }

    /**
     * 将map的key 、value 转成list
     * @param map
     * @param isValue true：将value转成list；false：将key转list
     * @param lists 如果是有选择性的假如，则lists不为空
     * @return
     */
    public static List MapToList (Map map,boolean isValue,List<String> lists) {
    	List list = Lists.newArrayList();
    	Iterator it = map.keySet().iterator();
    	while (it.hasNext()) {
    		String key = it.next().toString();
    		if(lists.size()>0) {
    			for(String val:lists) {
    				if(val.equals(key)) {
    					if (isValue) {  
    		                list.add(map.get(key));  
    		            } else {  
    		                list.add(key);  
    		            }  
    				}
    			}
    		}else {
    			if (isValue) {  
                    list.add(map.get(key));  
                } else {  
                    list.add(key);  
                }  
    		}
    		
    	}
    	return list;
    }
    
    /**
     * 将List<entity>中的所有entity的id的value 转成list
     * @param List<entity>
     * @return
     */
    public static List<String> EntityToList (List<? extends StringIDEntity> listEntity) {
    	List<String> list = new ArrayList<String>();
    	for(StringIDEntity e:listEntity) {
    		list.add(e.getId());
    	}
    	return list;
    }
}
