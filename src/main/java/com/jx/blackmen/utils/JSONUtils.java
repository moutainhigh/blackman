/**
 * 
 */

package com.jx.blackmen.utils;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * simple introduction
 *
 * <p>
 * detailed comment
 * </p>
 * 
 * @author chuxuebao 2015年7月28日
 * @see
 * @since 1.0
 */

public class JSONUtils {

	public static boolean mayBeJSON(String string) {
		return ((string != null) && (!StringUtils.equals(string, "{}")) && ((("null".equals(string))
				|| ((string.startsWith("[")) && (string.endsWith("]"))) || ((string.startsWith("{")) && (string
				.endsWith("}"))))));
	}
	
	public static String toJsonString(Object object){
		return JSON.toJSONString(object, SerializerFeature.WriteNullStringAsEmpty);
	}
	
}
