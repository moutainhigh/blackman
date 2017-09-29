package com.jx.blackmen.common;

import java.util.HashMap;
import java.util.Map;

public class PhoneCodeMap {
	
	//私有的默认构造子  
	private PhoneCodeMap() {
		map =new HashMap<String, Map<String,Object>>();
	}  
	//注意，这里没有final      
	private static PhoneCodeMap codemap=null;  
	public Map<String, Map<String,Object>> map=null;
	//静态工厂方法   
	public static PhoneCodeMap getInstance() {  
	      if (codemap == null) {    
	    	  codemap = new PhoneCodeMap();  
	       }    
	      return codemap;  
	} 

	
	
	
	
	public void setMap(String phone,Map<String,Object> mapCode){
		map.put(phone, mapCode);
	}
	public Map getMap(String phone){
		return map.get(phone);
	}
	
	public void clearMap(String phone){
		map.remove(phone);
	}
	
}
