package com.jx.blackmen.buz;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.jx.argo.ArgoTool;
import com.jx.blackmen.vo.IndexHandVo;

public class IndexBuz {

	public static IndexBuz commbuz = new IndexBuz();
	
	public List<IndexHandVo> getbannerlist(){
		List<IndexHandVo> list = getConfigVolist("bannerkey");
		return list;
	}
	public List<IndexHandVo> getWXbannerlist(){
		List<IndexHandVo> list = getConfigVolist("wxbannerkey");
		return list;
	}
	public List<IndexHandVo> getMbannerlist(){
		List<IndexHandVo> list = getConfigVolist("mbannerkey");
		return list;
	}
	public static void main(String[] args){
		List<IndexHandVo> list = IndexBuz.commbuz.getbannerlist();
	}
	public List<IndexHandVo> getConfigVolist(String filename){
		List<IndexHandVo> list = new ArrayList<IndexHandVo>();
		
		Properties prop = getPropertiesfile();
		
		//String jsonarry = String.valueOf(prop.getProperty(filename));//"{str:[{\"sortstr\":\"1\"}]}";//
		String jsonarry = "";//prop.getProperty(filename);//prop.get
		Iterator it = prop.keySet().iterator();
		while(it.hasNext()){
			//System.out.println(it.next());
			String key = (String) it.next();
			//System.out.println(key.equals(filename));
			if(key.equals(filename) || key == filename){
				jsonarry = (String) prop.get(key);
			}
			//System.out.println("key is "+key+" and val is "+prop.get(key));
		}
		if(null != jsonarry && !"".equals(jsonarry)){
			JSONObject jo = JSONObject.fromObject(jsonarry);
			JSONArray ja = jo.getJSONArray("valuestr");
			for(int i=0,c=ja.size();i<c;i++){
				IndexHandVo hvo = (IndexHandVo) JSONObject.toBean(ja.getJSONObject(i), IndexHandVo.class);
				if(null != hvo){
					list.add(hvo.getSort()-1, hvo);
				}
			}
		}
		
		return list;
	}
	private Properties getPropertiesfile(){
		Properties p = new Properties(); 
		String fileurl = ArgoTool.getConfigFolder()+ArgoTool.getNamespace()+"/configpage.properties";
		InputStreamReader in = null;
		try {
			in = new InputStreamReader(new FileInputStream(fileurl),"UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		if(in != null){
			try {
				
				p.load(in);
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		return p;
	}
	
}
