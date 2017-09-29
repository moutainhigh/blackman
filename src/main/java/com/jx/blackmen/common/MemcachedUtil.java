package com.jx.blackmen.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.jx.blackmen.utils.ConfigFileUtils;
import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;

public class MemcachedUtil {
	protected static MemCachedClient cachedClient = new MemCachedClient();
	private static String keyPrefix = "";
	static {
		Properties configFile = ConfigFileUtils.getConfigFile("memcache.properties");
		keyPrefix = configFile.getProperty("keyPrefix");
		 // server list and weights  
		String[] servers = {configFile.getProperty("servers")};  
        Integer[] weights = {1};  
        // grab an instance of our connection pool  
        SockIOPool pool = SockIOPool.getInstance();  
        // set the servers and the weights  
        pool.setServers(servers);  
        pool.setWeights(weights);  
        pool.setHashingAlg(SockIOPool.CONSISTENT_HASH);  
        // set some basic pool settings  
        // 5 initial, 5 min, and 250 max conns  
        // and set the max idle time for a conn  
        // to 6 hours  
        pool.setInitConn(5);  
        pool.setMinConn(5);  
        pool.setMaxConn(250);  
        pool.setMaxIdle(1000 * 60 * 60 * 6);  
  
        // set the sleep for the maint thread  
        // it will wake up every x seconds and  
        // maintain the pool size  
        pool.setMaintSleep(30);  
  
        // set some TCP settings  
        // disable nagle  
        // set the read timeout to 3 secs  
        // and don't set a connect timeout  
        pool.setNagle(false);  
        pool.setSocketTO(3000);  
        pool.setSocketConnectTO(0);  
  
        // initialize the connection pool  
        pool.initialize();  
	}
	
	/** 
     * 向缓存添加新的键值对。如果键已经存在，则之前的值将被替换。 
     *  
     * @param key 
     *            键 
     * @param value 
     *            值 
     * @return 
     */  
    public static boolean set(String key, Object value) {
        return cachedClient.set(keyPrefix + key, value);
    }  
  
    /** 
     * 向缓存添加新的键值对。如果键已经存在，则之前的值将被替换。 
     *  
     * @param key 
     *            键 
     * @param value 
     *            值 
     * @param expire 
     *            过期时间 New Date(1000*10)：十秒后过期 
     * @return 
     */  
    public static boolean set(String key, Object value, Date expire) {  
    	return cachedClient.set(keyPrefix + key, value,expire);  
    }
    /** 
     * get 命令用于检索与之前添加的键值对相关的值。 
     *  
     * @param key 
     *            键 
     * @return 
     */  
    public static Object get(String key) {
    	return cachedClient.get(keyPrefix + key);
    }  
  
    /** 
     * 删除 memcached 中的任何现有值。 
     *  
     * @param key 
     *            键 
     * @return 
     */  
    public static boolean delete(String key) {  
        return cachedClient.delete(keyPrefix + key);
    }  
  
    /** 
     * 清理缓存中的所有键/值对 
     *  
     * @return 
     */  
    public static boolean flashAll() { 
        return cachedClient.flushAll();
    }  
  
    public static void main(String[] args) {
/*		Date date = new Date(10 * 1000);
		boolean b = set("foo", "1111", date);
		System.out.println(b);
		String bar = String.valueOf(MemcachedUtil.get("foo"));
		System.out.println(bar);*/
    	
    	delKeysByPrefix("page");
	}
    
    /**
     * 跟key的前缀删除缓存
     * @param prefix
     */
    public static void delKeysByPrefix(String prefix){
    	List<String> keyListByText = getKeyListByText(prefix);
    	if(keyListByText != null && !keyListByText.isEmpty()){
    		for(int i=0 ; i<keyListByText.size(); i++){
    			cachedClient.delete(keyListByText.get(i));
    		}
    	}
    }

	/**
	 * 
	 */
	private static List<String> getKeyListByText(String text) {
		List<String> reList = new ArrayList<String>();
		Map<String, Map<String, String>> statsItems = cachedClient.statsItems();
    	Set<Entry<String, Map<String, String>>> entrySet = statsItems.entrySet();
    	Iterator<Entry<String, Map<String, String>>> statsItemsIterator = entrySet.iterator();
    	while(statsItemsIterator.hasNext()){
    		Entry<String, Map<String, String>> next = statsItemsIterator.next();
    		Map<String, String> itemsMap = next.getValue();
    		Iterator<String> itemsKeyIterator = itemsMap.keySet().iterator();
    		while(itemsKeyIterator.hasNext()){
    			String itemKey = itemsKeyIterator.next();
    			String itemVal = itemsMap.get(itemKey);
    			if(StringUtils.equalsIgnoreCase(itemVal, "0")){
    				continue;
    			}
    			String regEx = "items:(\\d+):number";
    			Pattern pat = Pattern.compile(regEx);
    			Matcher mat = pat.matcher(itemKey);
    			if(!mat.find()){
    				continue;
    			}
    			String group = mat.group(1);
    			Map<String, Map<String, String>> statsCacheDump = cachedClient.statsCacheDump(Integer.valueOf(group), 0);
    			if(statsCacheDump == null || statsCacheDump.isEmpty()){
    				continue;
    			}
    			Iterator<Entry<String, Map<String, String>>> iterator = statsCacheDump.entrySet().iterator();
    			while(iterator.hasNext()){
    				Entry<String, Map<String, String>> statsCache = iterator.next();
    				Map<String, String> statsCacheVal = statsCache.getValue();
    				Iterator<String> iterator2 = statsCacheVal.keySet().iterator();
    				while(iterator2.hasNext()){
    					String next2 = iterator2.next();
    					if(StringUtils.startsWith(next2, text)){
    						reList.add(next2);
    					}
    				}
    			}
    		}
    	}
    	return reList;
	}

} 
