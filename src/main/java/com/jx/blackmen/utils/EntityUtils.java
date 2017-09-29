package com.jx.blackmen.utils;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyFilter;



/**
 * 操作实体的工具类
 *
 * @author chuxuebao 2015年6月25日
 * @see
 * @since 1.0
 */

public class EntityUtils {

	static{
		ConvertUtils.register(new DateConverter(null), Date.class); 
	}
	
	
	/** 
	* 按bean的属性值对list集合进行排序 
	* 
	* @author zhangyang
	* 
	* @param list 
	*            要排序的集合 
	* @param propertyName 
	*            集合元素的属性名 
	* @param isAsc 
	*            排序方向,true--正向排序,false--逆向排序 
	* @return 排序后的集合 
	*/ 
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List sortList(List list, String propertyName, boolean isAsc) { 
	        //借助commons-collections包的ComparatorUtils    
	        //BeanComparator，ComparableComparator和ComparatorChain都是实现了Comparator这个接口    
	        Comparator mycmp = ComparableComparator.getInstance();       
	        mycmp = ComparatorUtils.nullLowComparator(mycmp);  //允许null 
	        if(!isAsc){ 
	        mycmp = ComparatorUtils.reversedComparator(mycmp); //逆序       
	        } 
	        Comparator cmp = new BeanComparator(propertyName, mycmp);    
	        Collections.sort(list, cmp);   
	        return list; 
	} 
	
	
	/**
	 * 把对象转换为 类型为 T 的实体
	 * @param entity 输入对象
	 * @param T 转换后的实体类型
	 * @return
	 */
	public static <T> T transferEntity(Object entity, Class<T> T) {
		return transferEntity(entity, T, null);
	}
	
	/**
	 * 把对象转换为 类型为 T 的实体，序列化过滤
	 * @param entity 输入对象
	 * @param T 转换后的实体类型
	 * @param serializeFilter 序列化过滤
	 * @return
	 */
	public static <T> T transferEntity(Object entity, Class<T> T, PropertyFilter propertyFilter) {
		if(entity == null){
			return null;
		}
		if(entity instanceof String){
			String string = entity.toString();
			if(JSONUtils.mayBeJSON(string)){
				entity = JSON.parseObject(string);
			}
		}
		T t = null;
		try {
			t = T.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if(propertyFilter != null){
			PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(entity);
			for(PropertyDescriptor propertyDescriptor : propertyDescriptors){
				String name = propertyDescriptor.getName();
				if(StringUtils.equalsIgnoreCase(name, "class")){
					continue;
				}
				Object value = null;
				try {
					value = PropertyUtils.getSimpleProperty(entity, name);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
				if(propertyFilter.apply(entity, name, value)){
					try{
						BeanUtils.copyProperty(t, name, value);
					}catch(Exception e){
						e.printStackTrace();
						return null;
					}
				}
			}
		}else{
			try {
				BeanUtils.copyProperties(t, entity);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return t;
	}
	/**
	 * 合并对象，输入对象与输出对象不同
	 * @param entity 原始对象
	 * @param extJsonStr 扩展json串
	 * @param T 返回对象类型
	 * @return
	 */
	public static <T> T combineEntity(Object entity, String extJsonStr, Class<T> T){
		T t = transferEntity(entity, T);
		combineEntity(t, extJsonStr);
		return t;
	}
	
	/**
	 * 合并对象，输入对象与输出对象相同
	 * @param t
	 * @param extMap
	 * @return
	 */
	public static <T> T combineEntity(T t, Map<String, Object> extMap){
		String jsonString = JSONUtils.toJsonString(extMap);
		return combineEntity(t, jsonString);
	}
	
	
	/**
	 * 合并对象，输入对象与输出对象相同
	 * @param t 
	 * @param extJsonStr
	 * @return
	 */
	public static <T> T combineEntity(T t, String extJsonStr){
		if(JSONUtils.mayBeJSON(extJsonStr)){
			JSONObject extJsonObj = JSON.parseObject(extJsonStr);
			try {
				BeanUtils.copyProperties(t, extJsonObj);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return t;
	}
	
	
	
}
