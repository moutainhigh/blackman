package com.jx.blackmen.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.jx.argo.ActionResult;
import com.jx.argo.annotations.Path;
import com.jx.argo.controller.AbstractController;
import com.jx.blackmen.actionresult.CustomActionResult;
import com.jx.blackmen.actionresult.MessageActionResult;
import com.jx.blackmen.actionresult.PlainActionResult;
import com.jx.blackmen.actionresult.TuwenActionResult;
import com.jx.blackmen.annotaion.TracePoint;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.utils.ActionResultUtils;
import com.jx.blackmen.utils.CommonUtils;
import com.jx.blackmen.utils.MContents;
import com.jx.blackmen.utils.PropertiesUtils;
import com.jx.blackmen.utils.Sign;
import com.jx.blackmen.utils.UtilsHelper;
import com.jx.blackmen.utils.WAQUtils;
import com.jx.blackmen.utils.WXUtils;
import com.jx.blackmen.weixin.event.ScanAction;
import com.jx.blackmen.weixin.event.TextAction;
import com.jx.blackmen.weixin.handler.MessageHandler;
import com.jx.service.messagecenter.entity.MessageEntity;
import com.jx.service.messagecenter.entity.MobileSmsResultExt;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/***
 * 基础的controller
 * @author duxf
 */
@TracePoint
public class BaseController extends AbstractController{
	
	public String getOpenid(){
		
		String openId = beat().getRequest().getParameter("openId");
		if(StringUtils.isBlank(openId)){
			openId = WXUtils.getOpenId(beat().getRequest());
		}
		System.out.println("get weixin openid is =======>"+openId);
		return openId;
	}
	/**
	 * 构建微信请求头
	 * @return
	 */
	public ActionResult buildWXUrl(){
		String openId = beat().getRequest().getParameter("openId");
		if(StringUtils.isBlank(openId)){
			openId = WXUtils.getOpenId(beat().getRequest());
		}
		if(StringUtils.isBlank(openId)){
			return redirect(WXUtils.getBaseUrl(request()));
		}
    	model().add("openId", openId);
    	System.out.println("buildurl--openid"+openId);
    	StringBuffer url = beat().getRequest().getRequestURL();
		String queryString = beat().getRequest().getQueryString();
		if(!StringUtils.isEmpty(queryString)){
			url.append("?" + queryString);
		}
    	String ticket = null;
		try {
			ticket = RSBLL.getstance().getWeixinService().getWeixinJSToken(MContents.weixin_app_id, MContents.weixin_app_secret_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("get js token ticke is "+ticket);
		try {
			Map<String,Object> map = Sign.tranceTokentojst(ticket, url.toString());
			map.put("appid", MContents.weixin_app_id);
			model().addAll(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//model().add("openId", "oxizHs97Dv0Kb7L29D782LkdMzE4");
		return null;
	}
	
	/**
	 * 公共的404页面
	 * @return
	 */
	@Path("/404")
	public ActionResult asf(){
		return view("404");
	}
	
	
	
	@Path("/wxmessage")
	public ActionResult weixinMessage(){
		HttpServletRequest request = beat().getRequest();
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		beat().getResponse().setCharacterEncoding("UTF-8");
		if (request.getMethod().equalsIgnoreCase("get")) {
			return new PlainActionResult(rr(request));
		}
		MessageHandler mh = new MessageHandler();
		MessageEntity me = mh.getMessageEntity(beat().getRequest());
		beat().getRequest().getSession().putValue("openid", me.getFromuser());
		beat().getRequest().getSession().setAttribute("openid", me.getFromuser());
		
		String res = "小微律政服务热线：010-57426695（客服工作时间为每天8:00-20:00），若有问题欢迎致电小微咨询";
		if(me != null){
			String co = me.getUniquecontent();
			JSONObject obj = JSONObject.fromObject(co);
			if(me.getMessagetype().equalsIgnoreCase("event")){//事件
				String eventtype = obj.getString("eventtype");
				System.out.println("eventtype is "+eventtype);
				try {
					if(eventtype != null ){//关注事件
						if(eventtype.equalsIgnoreCase("subscribe")){
							StringBuffer sb = new StringBuffer();
							sb.append("据说99%的创业者都关注了我~\n\n以后，你主外，我主内\n\n我能做到的，定会做得让你无可挑剔\n\n");
							sb.append("<a href='"+MContents.WX_url_base_shouquan_page.replace("APP_ID", MContents.weixin_app_id).replace("REDIRECT_URI", URLEncoder.encode(MContents.weixin_hosts+"/weixin/mywf/index", "utf-8"))+"'>1.【免费注册个公司】</a>\n\n");
							sb.append("<a href='"+MContents.WX_url_base_shouquan_page.replace("APP_ID", MContents.weixin_app_id).replace("REDIRECT_URI", URLEncoder.encode(MContents.weixin_hosts+"/wxdetail/38229817543169.html", "utf-8"))+"'>2.【不想免费怎么办】</a>\n\n");
							sb.append("<a href='"+MContents.WX_url_base_shouquan_page.replace("APP_ID", MContents.weixin_app_id).replace("REDIRECT_URI", URLEncoder.encode(MContents.weixin_hosts+"/wx/addresslist.html", "utf-8"))+"'>3.【没有地址怎么办】</a>\n\n");
							sb.append("<a href='"+MContents.WX_url_base_shouquan_page.replace("APP_ID", MContents.weixin_app_id).replace("REDIRECT_URI", URLEncoder.encode(MContents.weixin_hosts+"/wxdetail/38230152382977.html", "utf-8"))+"'>4.【法人、股权变更】</a>\n\n");
							sb.append("<a href='"+MContents.WX_url_base_shouquan_page.replace("APP_ID", MContents.weixin_app_id).replace("REDIRECT_URI", URLEncoder.encode(MContents.weixin_hosts+"/wxdetail/38253214893569.html", "utf-8"))+"'>5.【代理记账】</a>\n\n");
							sb.append("<a href='"+MContents.WX_url_base_shouquan_page.replace("APP_ID", MContents.weixin_app_id).replace("REDIRECT_URI", URLEncoder.encode(MContents.weixin_hosts+"/wx/report.html", "utf-8"))+"'>6.【小微帮您做年报，点此购买】</a>\n\n");
							return new MessageActionResult(sb.toString(), me.getFromuser());
						}else if(eventtype.equalsIgnoreCase("unsubscribe")){  //取消关注
							System.out.println(eventtype+" is "+me.getFromuser());
							//b.deltefans(me.getFromuser());
						}else if(eventtype.equalsIgnoreCase("SCAN")){ //已关注推送
							//已关注用户获取eventkey进行事件推送
							int scene_id = obj.getInt("Event");
							if(60 == scene_id || 61 == scene_id|| 62 == scene_id || 63 == scene_id){
//								JSONObject jo = new JSONObject();
//								jo.put("title", "hi朋友，怎么能一分钱不花很快拿到执照？戳进来小微告诉你");
//								jo.put("desc", "免费公司注册就来小微律政");
//								jo.put("picurl", "http://static.lvzheng.com/wx/images/zizhu.jpg");
//								jo.put("url", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx76ec5c98f567d0e1&redirect_uri=http%3A%2F%2Fm.lvzheng.com%2Fweixin%2Fzizhubanner&response_type=code&scope=snsapi_base&state=123&connect_redirect=1#wechat_redirect");
//								JSONArray ja = new JSONArray();
//								ja.add(jo);
//								JSONObject jr = new JSONObject();
//								jr.put("content", ja);
							}else{
								int scenid = obj.getInt("Event");
								System.out.println("eventi is "+scenid);
								ScanAction.getstance().dealScan(me.getFromuser(), scenid);
							}
						}else if(eventtype.equalsIgnoreCase("click") && obj.get("Event").equals("YJ_fankui")){
							System.out.println("come into hot line");
							String msg = "感谢亲爱的你提出意见，在公众号回复“反馈：＋您想要说的话”小微就能听到了哦 ";
							return new MessageActionResult(msg,me.getFromuser());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if(me.getMessagetype().equalsIgnoreCase("text")){//文字
				System.out.println("come into text deat action");
				String content = me.getContent();
				Pattern pattern = Pattern.compile("[0-9a-zA-Z]{0,}年报[^0-9]{0,1}1[3|5|7|8]{1}[0-9]{9}[A-Za-z]{0,}");
				Matcher matcher = pattern.matcher(content);
				if(matcher.matches()){
					System.out.println("now we come into 年报 + dianhua 。。。。");
					JSONObject jo = new JSONObject();
					jo.put("title", "小微律政普及｜年报相关政策");
					jo.put("desc", "小微教你如何做年报！");
					jo.put("picurl", "http://static.lvzheng.com/zt/nbphone.png");
					jo.put("url", "http://mp.weixin.qq.com/s?__biz=MzAxNjE3NDc5MA==&mid=212585792&idx=1&sn=a69163fdbea3734e29ede841342dbcea#rd");
					JSONArray ja = new JSONArray();
					ja.add(jo);
					JSONObject jr = new JSONObject();
					jr.put("content", ja);
					return new TuwenActionResult(jr.toString(),me.getFromuser());
				}
//				else if(content.contains("年报") || content.contains("nianbao") || content.contains("nb") || content.contains("NB")){
//					String nb = "只需99元,小微帮您做年报,<a href='https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx76ec5c98f567d0e1&redirect_uri=http%3A%2F%2Fm.lvzheng.com%2Fwx%2Freport.html&response_type=code&scope=snsapi_base&state=123&connect_redirect=1#wechat_redirect'>点此购买</a>";
//					return new MessageActionResult(nb, me.getFromuser());
//				}else if(content.contains("听课") || content.contains("tingke")){  //输入听课活动地址
//					String tingK = "爱学习的孩子都是好孩子，<a href='http://xiaoweilvzheng.mikecrm.com/f.php?t=mTPNNS'>点此报名</a>开始学习！";
//					return new MessageActionResult(tingK,me.getFromuser());
//				}else if(content.contains("查看奖品")){  //输入查看奖品
//					String tingK = "<a href='http://m.lvzheng.com/prize/view'>查看奖品</a>";
//					return new MessageActionResult(tingK,me.getFromuser());
//				}
				else if(content.contains("财务")){  //输入财务'
					JSONObject jo = new JSONObject();
					jo.put("title", "初创企业一个亿的小目标：要避免财务易犯的几个错误");
					jo.put("desc", "小微律政，专注为中小微企业提供一站式法律服务！");
					jo.put("picurl", "http://static.lvzheng.com/wx/images/caiwu.png");
					jo.put("url", "http://mp.weixin.qq.com/s?__biz=MzAxNjE3NDc5MA==&mid=505565688&idx=1&sn=e21d90dfce80ebb8f56537f3b4b85e04&scene=0#wechat_redirect");
					JSONArray ja = new JSONArray();
					ja.add(jo);
					JSONObject jr = new JSONObject();
					jr.put("content", ja);
					return new TuwenActionResult(jr.toString(),me.getFromuser());
				}else if(content.contains("公章")){  //输入公章'
					JSONObject jo = new JSONObject();
					jo.put("title", "印章的管理及内控防范的疑难问题");
					jo.put("desc", "小微律政，专注为中小微企业提供一站式法律服务！");
					jo.put("picurl", "http://static.lvzheng.com/wx/images/kezhang.png");
					jo.put("url", "http://mp.weixin.qq.com/s?__biz=MzAxNjE3NDc5MA==&mid=505565697&idx=1&sn=dc80aebc7477139e49d76d56aeff7f00&scene=0#wechat_redirect");
					JSONArray ja = new JSONArray();
					ja.add(jo);
					JSONObject jr = new JSONObject();
					jr.put("content", ja);
					return new TuwenActionResult(jr.toString(),me.getFromuser());
				}else if(content.contains("离岸")){  //输入离岸'
					JSONObject jo = new JSONObject();
					jo.put("title", "那些不用交税的公司");
					jo.put("desc", "小微律政，专注为中小微企业提供一站式法律服务！");
					jo.put("picurl", "http://static.lvzheng.com/wx/images/lian.png");
					jo.put("url", "http://mp.weixin.qq.com/s?__biz=MzAxNjE3NDc5MA==&mid=505565699&idx=1&sn=e14d7df6717c130825cc9354f0c681ea&scene=0#wechat_redirect");
					JSONArray ja = new JSONArray();
					ja.add(jo);
					JSONObject jr = new JSONObject();
					jr.put("content", ja);
					return new TuwenActionResult(jr.toString(),me.getFromuser());
				}else if(content.contains("人事")){  //输入人事'
					JSONObject jo = new JSONObject();
					jo.put("title", "招贤纳士有讲究");
					jo.put("desc", "小微律政，专注为中小微企业提供一站式法律服务！");
					jo.put("picurl", "http://static.lvzheng.com/wx/images/renshi.png");
					jo.put("url", "http://mp.weixin.qq.com/s?__biz=MzAxNjE3NDc5MA==&mid=505565701&idx=1&sn=2bef43693d626403aef2aa5b89505542&scene=0#wechat_redirect");
					JSONArray ja = new JSONArray();
					ja.add(jo);
					JSONObject jr = new JSONObject();
					jr.put("content", ja);
					return new TuwenActionResult(jr.toString(),me.getFromuser());
				}
				String txt = TextAction.getstance().dealMessage(me);
				if(!"".equals(txt)){
					res = txt;
					return new MessageActionResult(res,me.getFromuser());
				}
				System.out.println("the bind result is "+res);
			}
		}
		return new CustomActionResult(res,me.getFromuser());
	}
	
	public String rr(HttpServletRequest request) {
		String signature = request.getParameter("signature");// 微信加密签名
		String timestamp = request.getParameter("timestamp");// 时间戳
		String nonce = request.getParameter("nonce");// 随机数
		String echostr = request.getParameter("echostr");// 随机字符串

		return UtilsHelper.testkey( signature, timestamp, nonce,  echostr);

	}

	
	/***
	 * 发送手机验证码
	 * @return 1发送成功 2发送的语音验证码 -1发送失败 error 获取手机号失败
	 */
	@Path("/common/sendPhoneCode")
	public ActionResult sendPhoneCode(){
		String tokenstr = beat().getRequest().getParameter("tokenstr");
		String valicdoe = beat().getRequest().getParameter("validatecode");
		
		String phoneNum = beat().getRequest().getParameter("phoneNum")==null?"":WAQUtils.HTMLEncode(beat().getRequest().getParameter("phoneNum"));
		
		if(StringUtils.isBlank(phoneNum)){
			return ActionResultUtils.renderJson("{\"error\":}");
		}
		if(valicdoe == null || "".equals(valicdoe) || null == tokenstr || "".equals(tokenstr)){
			return ActionResultUtils.renderJson("{\"flag\":\"8\"}");
		}else if(!valicdoe.toUpperCase().equalsIgnoreCase((String) beat().getRequest().getSession().getAttribute("valicode"+tokenstr))){
			String sttt = (String) beat().getRequest().getSession().getAttribute("valicode"+tokenstr);
			
			System.out.println(valicdoe.toUpperCase()+"---"+sttt);
			return ActionResultUtils.renderJson("{\"flag\":\"9\"}");
		}else{
			try {
				MobileSmsResultExt  result = RSBLL.getstance().getMoblieSmsService().sendVerifyCode(phoneNum);
				System.out.println("**********"+phoneNum+"**********"+result.getCode()+"==="+result.getMsg()+"==="+result.isResult());
				if(result.isResult()){
					if(result.getCode() == 2){
						return ActionResultUtils.renderJson("{\"flag\":\"2\"}");
					}
					return ActionResultUtils.renderJson("{\"flag\":\"1\"}");
		        }
			} catch (Exception e) {
				e.printStackTrace();
				return ActionResultUtils.renderJson("{\"flag\":\"-1\"}");
			}
		}
		return ActionResultUtils.renderJson("{\"flag\":\"-1\"}");
	}
	
//	/***
//	 * 校验手机发送的验证码是否正确
//	 * @return true正确 false 错误 error 手机或验证码为空
//	 */
//	@Path("/common/checkPhoneAndCode")
//	public ActionResult checkPhoneAndCode(){
//		String phoneNum = beat().getRequest().getParameter("phoneNum")==null?"":WAQUtils.HTMLEncode(beat().getRequest().getParameter("phoneNum"));
//		String code = beat().getRequest().getParameter("code")==null?"":WAQUtils.HTMLEncode(beat().getRequest().getParameter("code"));
//		
//		if(StringUtils.isBlank(phoneNum) || StringUtils.isBlank(code)){
//			return ActionResultUtils.renderJson("{\"error\":}");
//		}
//		try {
//			Boolean  result = RSBLL.getstance().getMoblieSmsService().checkVerifyCode(phoneNum, code);
//			if(result){
//				return ActionResultUtils.renderJson("{\"success\":\"true\"}");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("调用校验手机和验证码方法：checkVerifyCode失败手机号:" + phoneNum +"验证码:"+code);
//		}
//		return ActionResultUtils.renderJson("{\"success\":\"false\"}");
//	}
	
	
	/**
	 * 获取热门单品
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, String>>  getHostList(String duan) throws Exception{
		
		
			//热门单品
			List<Map<String, String>> hotlist = new ArrayList<Map<String,String>>();
			for (int i = 1; i <= 4; i++) {
				if(i!=2){
					Map hotMap = new HashMap<String, String>();
					String name  = PropertiesUtils.getProp("hot"+i+".name");
					String tip   = PropertiesUtils.getProp("hot"+i+".tip");
					String price = PropertiesUtils.getProp("hot"+i+".price");
					String path  = PropertiesUtils.getProp("hot"+i+".path");
					int num = CommonUtils.getProductBaseNumberBySellid("233333");
					hotMap.put("num", num);
					hotMap.put("name",name );
					hotMap.put("tip",tip  ); 
					hotMap.put("price",price); 
					hotMap.put("path",path.replace("duan", duan) ); 
					hotlist.add(hotMap);
				}
			}
			
			
		
		return hotlist;
	}
}
