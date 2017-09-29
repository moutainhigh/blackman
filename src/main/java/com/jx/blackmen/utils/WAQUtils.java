/**
 * 
 */

package com.jx.blackmen.utils;

import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jx.tools.waq.WAQ;



/**
 * simple introduction
 *
 * <p>detailed comment</p>
 * @author chuxuebao 2016年3月11日
 * @see
 * @since 1.0
 */

public class WAQUtils {

	public static String HTMLEncode(String data){
		boolean jsonFlag = JSONUtils.mayBeJSON(data);
		if(jsonFlag){
			// JSON对象
			JSONObject parseObject = JSON.parseObject(data);
			encodeJsonObject(parseObject);
			return parseObject.toJSONString();
		}
		return WAQ.forXSS().HTMLEncode(data);
	}

	/**
	 * @param parseObject
	 */
	private static void encodeJsonObject(JSONObject parseObject) {
		if(parseObject == null){
			return;
		}
		Set<String> keySet = parseObject.keySet();
		for(String key:keySet){
			Object object = parseObject.get(key);
			if(object == null){
				continue;
			}
			if(object instanceof String){
				parseObject.put(key, WAQ.forXSS().HTMLEncode(object.toString()));
			}else if(object instanceof JSONObject){
				// json 对象
				encodeJsonObject((JSONObject)object);
			}else if(object instanceof JSONArray){
				// json 数组
				JSONArray jsonArray = (JSONArray)object;
				for(int i = 0; i < jsonArray.size(); i++ ){
					encodeJsonObject(jsonArray.getJSONObject(i));
				}
			}
		}
	}
}
