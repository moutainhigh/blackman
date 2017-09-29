package com.jx.blackmen.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.jx.blackface.gaea.usercenter.entity.BFLoginEntity;
import com.jx.blackmen.frame.RSBLL;

public class CookieUtils {
	public final static String cookicName = "lvuser";
	public final static String userOpenId = "userOpenId";
	//设置cookie有效期是一个星期
	public final static int cookieMaxAge = 60 * 60 * 24 * 7; 
	
	//cookie的有效期 
	public final static long validTime = System.currentTimeMillis() ; 
	//加密cookie时的网站自定码 
    public final static String webKey = "8888"; 
	
	/***
	 * 检查此cookie名称是否存在且值不为空 
	 * @param cookicName
	 * @param request
	 * @return true 不为空 false 为空
	 * @throws Exception
	 */
    public static boolean checkCookieName(String cookicName,HttpServletRequest request) throws Exception{ 
	     //根据cookieName取cookieValue 
		 Cookie cookies[] = request.getCookies();
         String cookieValue = null; 
         boolean userFlag = false;
         if(cookies!=null){ 
            for(int i=0;i<cookies.length; i++){
               if (cookicName.equals(cookies[i].getName())) { 
                  cookieValue = cookies[i].getValue(); 
                  break; 
               } 
            }
            if(StringUtils.isNotBlank(cookieValue)){
            	long userid =0l;
    			if(StringUtils.contains(cookieValue, ":")){
    				String[] splitCookieValues = StringUtils.split(cookieValue, ":");
    				if(splitCookieValues.length > 0 && StringUtils.isNotBlank(splitCookieValues[0])){
    					userid =Long.valueOf(splitCookieValues[0]) ;
    				}
    			}else{
    				userid = Long.valueOf(cookieValue);
    			}
    			if(userid > 0){
    				BFLoginEntity loginEntity = RSBLL.getstance().getLoginService().getLoginEntityById(userid);
    				if(null != loginEntity){
    					userFlag = true;
    				}
    			}
            }
         } 
         //如果cookieValue为空,返回, 
         if(cookieValue==null){ 
        	 return false;
         }
         if(!userFlag){
        	 return false;
         }
         return true;
     } 
    
    /***
     * 根据cookie名称取cookie的值  (注:使用值时需要看下值是否需要处理)
     * @param cookieName
     * @param request
     * @return 
     */
    public static String getCookieValueByName(String cookieName,HttpServletRequest request) throws Exception{
    	//根据cookieName取cookieValue 
		Cookie cookies[] = request.getCookies(); 
        String cookieValue = ""; 
        if(cookies!=null){ 
           for(int i=0;i<cookies.length; i++){
              if (cookieName.equals(cookies[i].getName())) {
                 cookieValue = cookies[i].getValue(); 
                 break; 
              } 
           }
        }
        return cookieValue;
    }
    
    /**
     * 删除指定名称的cookie
     * @param request
     * @param response
     */
    public static void deleteCookie(String cookieName,HttpServletRequest request, HttpServletResponse response) throws Exception{
    	 Cookie cookies[] = request.getCookies(); 
    	 if(cookies!=null){ 
             for(int i=0;i<cookies.length; i++){
                    if (cookieName.equals(cookies[i].getName())) { 
                    	Cookie cookie = new Cookie(cookieName,null);
                        cookie.setMaxAge(0);
                        cookie.setPath("/");
                        cookie.setDomain(".lvzheng.com");
                        response.addCookie(cookie);
                        break;
                    } 
             }
      } 
    }
    
    /***
     * 保存cookie
     * @param userId
     * @param response
     * @param cookieTime
     * @throws Exception
     */
    public static void saveCookie(long userId, HttpServletResponse response,int cookieTime) throws Exception { 
    	//cookie的有效期 
        long validTime = System.currentTimeMillis() ;  //+ (cookieTime * 5000); 暂时不判断cookie的失效时间 
        //MD5加密用户详细信息 
        String cookieValueWithMd5 =MD5.sign(userId + ":" + validTime + ":" + webKey,"UTF-8"); 
        //将要被保存的完整的Cookie值 
        String cookieValue = userId + ":" + validTime + ":" + cookieValueWithMd5; 
        //开始保存Cookie 
        Cookie cookie = new Cookie(cookicName, cookieValue); 
        //存两年(这个值应该大于或等于validTime) 
        cookie.setMaxAge(cookieTime); 
        //cookie有效路径是网站根目录 
        cookie.setPath("/"); 
        cookie.setHttpOnly(true);
        cookie.setDomain(".lvzheng.com");
        //向客户端写入 
        response.addCookie(cookie); 
    }
    
    
    /***
     * 保存指定名称的cookie
     * @param userId
     * @param response
     * @param cookieTime
     * @throws Exception
     */
    public static void saveCookie(String cookieName,String cookieValue, HttpServletResponse response) throws Exception { 
        //开始保存Cookie 
        Cookie cookie = new Cookie(cookieName, cookieValue); 
        //存两年(这个值应该大于或等于validTime) 
        cookie.setMaxAge(-1); 
        //cookie有效路径是网站根目录 
        cookie.setPath("/"); 
        cookie.setHttpOnly(true);
        cookie.setDomain(".lvzheng.com");
        //向客户端写入 
        response.addCookie(cookie); 
    }
    
    /**
     * 删除cookie
     * @param request
     * @param response
     */
	public static void deleteCookie(HttpServletRequest request,HttpServletResponse response) {
		Cookie cookies[] = request.getCookies(); 
   	 	if(cookies!=null){ 
            for(int i=0;i<cookies.length; i++){
                   if (cookicName.equals(cookies[i].getName())) {
                   	Cookie cookie = new Cookie(cookicName,null);
                       cookie.setMaxAge(0);
                       cookie.setPath("/");
                       cookie.setDomain(".lvzheng.com");
                       response.addCookie(cookie);
                       break;
                   } 
            }
   	 	} 
		
	}
	
	/***
	 * 根据cookie值获取userid
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
    public static String getUseridByCookie(HttpServletRequest request, HttpServletResponse response) throws Exception{ 
		     //根据cookieName取cookieValue 
    		 Cookie cookies[] = request.getCookies(); 
             String cookieValue = null; 
             if(cookies!=null){ 
                    for(int i=0;i<cookies.length; i++){
                           if (cookicName.equals(cookies[i].getName())) { 
                                  cookieValue = cookies[i].getValue(); 
                                  break; 
                           } 
                    }
             } 
             //如果cookieValue为空,返回, 
             if(cookieValue==null){ 
                    return null;
             } 
	          //如果cookieValue不为空,才执行下面的代码 
              //对解码后的值进行分拆,得到一个数组,如果数组长度不为3,就是非法登陆 
	          String cookieValues[] = cookieValue.split(":"); 
              if(cookieValues.length!=3){ 
	                return null;
	          } 
              //取出cookie中的用户名,并到数据库中检查这个用户名, 
              Long userid =Long.parseLong(cookieValues[0]) ; 
              String TimeInCookie = cookieValues[1];
              //根据用户名到数据库中检查用户是否存在 
              BFLoginEntity user = RSBLL.getstance().getLoginService().getLoginEntityById(userid);
              //如果user返回不为空,就取出密码,使用用户名+密码+有效时间+ webSiteKey进行MD5加密 
              if(null != user){ 
                     String md5ValueInCookie = cookieValues[2]; 
                     String md5ValueFromUser = MD5.sign(userid + ":" + TimeInCookie + ":" + webKey,"UTF-8"); 
                     //将结果与Cookie中的MD5码相比较,如果相同,写入Session,自动登陆成功,并继续用户请求 
                     if(md5ValueFromUser.equals(md5ValueInCookie)){ 
                    	 return userid.toString();
                     } 
              }else{ 
            	  return null;
             } 
             return null;
       } 
    
    /**
	 * 写入cookie
	 * 
	 * @param response
	 * @param cookieName
	 * @param cookieValue
	 * @param maxAge 生命周期（秒数）
	 */
	public static void write(HttpServletResponse response, String cookieName, String cookieValue, int maxAge, String domain) {
		if (response == null || StringUtils.isEmpty(cookieName))
			return;

		try {
			Cookie cookie = new Cookie(cookieName, cookieValue);
			cookie.setPath("/");
			if (!StringUtils.isEmpty(domain))
				cookie.setDomain(domain);
			cookie.setMaxAge(maxAge);
			response.addCookie(cookie);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void write(HttpServletResponse response, String cookieName, String cookieValue, int maxAge) {
		write(response, cookieName, cookieValue, maxAge, ".lvzheng.com");
	}
}
