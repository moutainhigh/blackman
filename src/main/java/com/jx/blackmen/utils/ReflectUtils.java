package com.jx.blackmen.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.comparators.ComparableComparator;

/**
 * @author bruce
 * @date 2016年04月12日
 * @email zhangyang226@gmail.com
 * @site http://blog.northpark.cn | http://northpark.cn | orginazation https://github.com/jellyband
 * 
 */
public class ReflectUtils {
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
	
	
	/*
	 * 获取给定类的父类的指定位置的实际泛型参数
	 */

	public static Class<?> getGenericTypeSuperclass(Class<?> clazz, int index) {

		Type type = clazz.getGenericSuperclass();

		/*
		 * 如果当前类没有自定义的父类，就放回Object类型
		 */

		if (!(type instanceof ParameterizedType)) {
			return Object.class;
		}
		/*
		 * 否则就返回自定义的父类的信息
		 */

		Type[] parameter = ((ParameterizedType) type).getActualTypeArguments();

		if (index >= parameter.length || index < 0) {
			throw new RuntimeException("你指定的泛型参数索引"
					+ (index >= parameter.length ? "已造成越界访问" : "为负数"));
		}

		if (!(parameter[index] instanceof Class)) {
			return Object.class;
		}

		return (Class<?>) parameter[index];
	}

	/*
	 * 获取父类的第一个泛型参数
	 */
	public static Class<?> getFirstGenericTypeSuperclass(Class<?> clazz) {
		return getGenericTypeSuperclass(clazz, 0);
	}
}
