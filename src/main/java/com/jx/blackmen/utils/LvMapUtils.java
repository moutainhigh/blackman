/**
 * 
 */

package com.jx.blackmen.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;



/**
 * simple introduction
 *
 * <p>detailed comment</p>
 * @author chuxuebao 2016年1月22日
 * @see
 * @since 1.0
 */

public class LvMapUtils {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map combine(Map...mapArray){
		if(mapArray == null || mapArray.length <= 0){
			return null;
		}
		Map allMap = new HashMap();
		for(Map map:mapArray){
			if(MapUtils.isNotEmpty(map)){
				allMap.putAll(map);
			}
		}
		return allMap;
	}
}
