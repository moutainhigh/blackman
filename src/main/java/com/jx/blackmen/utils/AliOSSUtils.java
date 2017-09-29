/**
 * 
 */

package com.jx.blackmen.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyun.oss.model.ResponseHeaderOverrides;



/**
 * simple introduction
 *
 * <p>detailed comment</p>
 * @author chuxuebao 2015年8月11日
 * @see
 * @since 1.0
 */

public class AliOSSUtils {

	
	private static String accessKeyId = "";
	
	private static String accessKeySecret = "";

	private static String endpoint = "";

	public static String bucketName = "";
	
	static{
		Properties configFile = ConfigFileUtils.getConfigFile("AliOSS.properties");
		if(configFile == null || configFile.isEmpty()){
			System.out.println("******************** start failed.");
		}
		accessKeyId = configFile.getProperty("accessKeyId");
		accessKeySecret = configFile.getProperty("accessKeySecret");
		endpoint = configFile.getProperty("endpoint");
		bucketName = configFile.getProperty("bucketName");
	}
	
	private static Object lock = new Object();
	
	private static  OSSClient client;
	public static OSSClient getOSSClient(){
		if(client == null){
			synchronized (lock) {
				if(client == null){
					client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
				}
			}
		}
		return client;
	}
	
	/**
	 * 获取文件流
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public static InputStream getObject(String key) throws IOException{
		OSSObject object = getOSSClient().getObject(bucketName, key);
		return object.getObjectContent();
	}
	
	/**
	 * 获取文件下载的链接
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public static String getObjectUrl(String key, String fileName)throws IOException{
		// 有效期 2 个小时
		Date expiration = new Date(System.currentTimeMillis() + 2 * 60 * 60 *1000);
		GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key, HttpMethod.GET);
		//设置过期时间
		request.setExpiration(expiration);
		ResponseHeaderOverrides responseHeaders = new ResponseHeaderOverrides();
		responseHeaders.setContentDisposition("filename=" + fileName);
		request.setResponseHeaders(responseHeaders);
		// 生成URL签名(HTTP GET请求)
		URL signedUrl = getOSSClient().generatePresignedUrl(request);
		return signedUrl.toString();
	}
	/**
	 * 上传文件
	 * @param fileInputStream
	 * @return
	 * @throws IOException
	 */
	public static String pubObject(InputStream fileInputStream) throws IOException{
		byte[] data = IOUtils.toByteArray(fileInputStream);
		return pubObject(data);
	}
	
	/**
	 * 上传文件
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static String pubObject(byte[] data) throws IOException{
		String key = UUID.randomUUID().toString();
		ObjectMetadata metadata = new ObjectMetadata();
		PutObjectResult putResult = getOSSClient().putObject(bucketName, key, new ByteArrayInputStream(data), metadata);
		String eTag = putResult.getETag();
		String md5Hex = DigestUtils.md5Hex(data);
		if(StringUtils.equalsIgnoreCase(eTag, md5Hex)){
			return key;
		}else{
			// 上传失败
			return null;
		}
	}
	
	public static String putObjectUrl(String key){
		Date expiration = new Date(System.currentTimeMillis() + 2 * 60 * 60 *1000);
		GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key, HttpMethod.PUT);
		//设置过期时间
		request.setExpiration(expiration);
		//设置Content-Type
		request.setContentType("application/octet-stream");
		// 生成URL签名(HTTP PUT请求)
		URL signedUrl = getOSSClient().generatePresignedUrl(request);
		return signedUrl.toString();
	}
	
	
	public static void main(String[] args) throws IOException {
/*		// 文件上传
		File f = new File("D:\\111.jpg");
		InputStream fileInputStream = new FileInputStream(f); 
		String key = pubObject(fileInputStream);
		// 文件下载
		InputStream objectContent = getObject(key);
		File file = new File("D://123.jpg");
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		IOUtils.copy(objectContent, fileOutputStream);*/
	}
}