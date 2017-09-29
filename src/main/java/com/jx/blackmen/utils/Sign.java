package com.jx.blackmen.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class Sign {
   
	
	public static Map<String,Object> tranceTokentojst(String token,String url)throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String ticket = token;
		Map<String,String> signmap = sign(ticket,url);
		map.put("timestamp", signmap.get("timestamp"));
		System.out.println("timestamp is "+signmap.get("timestamp"));
		map.put("nonceStr", signmap.get("nonceStr"));
		map.put("signature", signmap.get("signature"));
		return map;
	}
    public static Map<String, String> sign(String jsapi_ticket,String url) {
        Map<String, String> ret = new HashMap<String, String>();
        String nonce_str = create_nonce_str();
        String timestamp = create_timestamp();
        String string1;
        String signature = "";

        //注意这里参数名必须全部小写，且必须有序
        string1 = "jsapi_ticket=" + jsapi_ticket +
                  "&noncestr=" + nonce_str +
                  "&timestamp=" + timestamp +
                  "&url=" + url;
        System.out.println(string1);

        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        ret.put("url", url);
        ret.put("jsapi_ticket", jsapi_ticket);
        ret.put("nonceStr", nonce_str);
        ret.put("timestamp", timestamp);
        ret.put("signature", signature);
        System.out.println("signature is "+signature);
        return ret;
    }

    public static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public static String create_nonce_str() {
        return UUID.randomUUID().toString();
    }

    public static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
    
    public static void main(String[] args) {
        String jsapi_ticket = "sM4AOVdWfPE4DxkXGEs8VCV_S16qzXMqD_bRx_nMUL2IOjusyD3Jst7ArW3WkGWWgHAbfGx4LrLjBOnBWH49cQ";
        String url = "http://w.5858.com/vquan/get/24168405320967/28727221640965";
        Map<String, String> ret = sign(jsapi_ticket, url);
        for (Map.Entry entry : ret.entrySet()) {
            System.out.println(entry.getKey() + ", " + entry.getValue());
        }
    };
}
