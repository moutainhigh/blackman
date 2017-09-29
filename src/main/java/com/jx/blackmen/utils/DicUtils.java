/**
 * 
 */

package com.jx.blackmen.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.jx.blackmen.frame.RSBLL;
import com.jx.service.dic.entity.DicDataEntity;
import com.jx.service.enterprise.entity.LvEnterpriseDicDataEntity;



/**
 * simple introduction
 *
 * <p>detailed comment</p>
 * @author chuxuebao 2015年8月21日
 * @see
 * @since 1.0
 */

public class DicUtils {
	
	public static String getDicDataValue(String dicTypeKey, String dicDataKey, String defaultValue){
		String dicDataValue = null;
		try {
			dicDataValue = RSBLL.getstance().getDicService().getDicDataValueByDicTypeKeyAndDicDataKey(dicTypeKey, dicDataKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(StringUtils.isBlank(dicDataValue)){
			return defaultValue;
		}
		return dicDataValue;
	}

	public static String getDicDataValue(String dicTypeKey, String dicDataKey){
		return getDicDataValue(dicTypeKey, dicDataKey, "");
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> transferDicData(Map map){
		if(map == null || map.isEmpty()){
			return Collections.EMPTY_MAP;
		}
		Map<String, Object> reMap = new HashMap<String, Object>();
		Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, Object> next = iterator.next();
			String key = next.getKey();
			Object obj = next.getValue();
			if(obj != null && StringUtils.isNotBlank(obj.toString())){
				String value = obj.toString();
				String dicDataValue = DicUtils.getDicDataValue(key, value);
				if(StringUtils.isNotBlank(dicDataValue)){
					reMap.put(key, dicDataValue);
					reMap.put(key + "Key", value);
					continue;
				}
				
				// 企业库字典表
				String enterpriseDicValue = DicUtils.getEnterpriseDicValue(key, value);
				if(StringUtils.isNotBlank(enterpriseDicValue)){
					reMap.put(key, enterpriseDicValue);
					reMap.put(key + "Key", value);
					continue;
				}
				
				reMap.put(key, value);
			}else{
				map.put(key, "");
			}
		}
		return reMap;
	}
	
	public static List<DicDataEntity> getDicDataList(String dicTypeKey){
		List<DicDataEntity> lvDicDataList = null;
		try {
			lvDicDataList = RSBLL.getstance().getDicService().getListDicDataByDicTypeKey(dicTypeKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lvDicDataList;
	}
	
	public static String getEnterpriseDicValue(String dicTypeKey, String dicDataKey){
		String dicDataValue = null;
		try {
			dicDataValue = RSBLL.getstance().getEpEnterpriseDicDataService().getDicDataValueByTypeKeyAndDataKey(dicTypeKey, dicDataKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(StringUtils.isNotBlank(dicDataValue)){
			return dicDataValue;
		}
		return "";
	}
	
	public static List<LvEnterpriseDicDataEntity> getEnterpriseDicDataList(String dicTypeKey){
		List<LvEnterpriseDicDataEntity> dicDataList = new ArrayList<LvEnterpriseDicDataEntity>();
		try {
			dicDataList = RSBLL.getstance().getEpEnterpriseDicDataService().getDicDataListByType(dicTypeKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dicDataList;
	}
}
