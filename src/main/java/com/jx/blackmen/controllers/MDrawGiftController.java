package com.jx.blackmen.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.jx.argo.ActionResult;
import com.jx.argo.annotations.Path;
import com.jx.blackface.gaea.sell.entity.LvzSellProductEntity;
import com.jx.blackface.gaea.usercenter.contract.ILoginService;
import com.jx.blackface.gaea.usercenter.entity.BFLoginEntity;
import com.jx.blackface.gaea.usercenter.utils.ActionResultUtils;
import com.jx.blackmen.common.CommonUtils;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.utils.CookieUtils;
import com.jx.blackmen.utils.MContents;
import com.jx.blackmen.utils.WAQUtils;
import com.jx.service.messagecenter.entity.MobileSmsResultExt;
import com.jx.service.preferential.plug.buz.PreferentialFetchBuz;

import net.sf.json.JSONObject;
/**
 * @author bruce
 * @date 2016-07-19
 * 新用户享优惠
 *
 */
@Path("/m")
public class MDrawGiftController extends BaseController{
	
	public static final Long ppid = 40841276016129L;
	public static final List<Long> ppid_onlie = new ArrayList<Long>();
	static{
		ppid_onlie.add(40867018845953L);
		ppid_onlie.add(40866986493953L);
		ppid_onlie.add(40866867851009L);
		ppid_onlie.add(40866777148161L);
		ppid_onlie.add(40866676281089L);
	}
	
	/**
	 * 跳转手机和验证码弹窗内容
	 * @return
	 */
	@Path("/drawgift.html")
	public ActionResult detail(){
		
		try {
			//获取token
			CommonUtils.geneCode(beat());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return view("/m/gift/getcou");
	}
	/**
	 * 展示发送的红包
	 * @return
	 */
	@Path("/gift/showgift")
	public ActionResult showgift(){
		
//		String uid = beat().getRequest().getParameter("uid");
//		try {
//			if(StringUtils.isNotEmpty(uid)){
//				
//				List<AccountVO> volist = PreferentialAccountBuz.getInstance().getUserCouponsByStatus(Long.valueOf(uid), 0,1,99,"quota desc");
//				
//				if(CollectionUtils.isNotEmpty(volist)){
//					model().add("volist", volist);
//				}
//			}
//		
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
		//转到的页面路径
		
		return view("/m/gift/mcoupon");
	}
	
	
	/**
	 * 发送手机验证码||没有图像验证码的
	 * @return
	 */
	@Path("/gift/sendPhoneCode")
	public ActionResult sendPhoneCode(){
			
		
		String phoneNum = beat().getRequest().getParameter("phoneNum")==null?"":WAQUtils.HTMLEncode(beat().getRequest().getParameter("phoneNum"));
		
		String tokenstr = beat().getRequest().getParameter("tokenstr");
		
		String sessiontoken = (String) beat().getRequest().getSession().getAttribute("token");
		
		if(!sessiontoken.equals(tokenstr)){
			return ActionResultUtils.renderJson("{\"tokenerror\":\"1\"}");
		}else{
			//刷新token
			CommonUtils.geneCode(beat());
		}
		
		
		if(StringUtils.isBlank(phoneNum)){
			return ActionResultUtils.renderJson("{\"error\":}");
		}
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
		
		return ActionResultUtils.renderJson("{\"flag\":\"-1\"}");
	
	}
	/**
	 * 判断手机号是否存在
	 * @return
	 */
	@Path("/gift/phoneFlag")
	public ActionResult phoneFlag(){
		
		String rs = "0";
		try {
			
			String phoneNum = beat().getRequest().getParameter("phoneNum");
			List<BFLoginEntity> list = RSBLL.getstance().getLoginService().getLoginEntity(" userphone = '"+phoneNum+"' ", 1, 1, "");
			if(CollectionUtils.isNotEmpty(list)){
				if(list.size()>0){
					rs = "1";
				}
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return ActionResultUtils.renderText(rs);
	}
	
	/**
	 * 领取优惠券
	 * @return
	 */
	@Path("/gift/receive")
	public ActionResult receive(){
		
		ILoginService ls = RSBLL.getstance().getLoginService();
		String userphone = beat().getRequest().getParameter("phoneNum");
		String phonecode = beat().getRequest().getParameter("usercode");
		
		JSONObject jr = new JSONObject();
		if(userphone != null && !"".equals(userphone)){
			String condition = "userphone='"+userphone+"'";
			List<BFLoginEntity> list = null;
			try {
				list = ls.getLoginEntity(condition, 1, 1, "userid");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(null != list && list.size() > 0){
				jr.put("ret", "fail");
				jr.put("msg", "电话已经存在！");
				return ActionResultUtils.renderJson(jr.toString());
			}
		}
		
		try {
			if(phonecode == null || "".equals(phonecode)){
				jr.put("ret", "fail");
				jr.put("msg", "手机验证码错误");
				return ActionResultUtils.renderJson(jr.toString());
			}else if(!RSBLL.getstance().getMoblieSmsService().checkVerifyCode(userphone, phonecode)){
				jr.put("ret", "fail");
				jr.put("msg", "手机验证码错误");
				return ActionResultUtils.renderJson(jr.toString());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		//添加新用户
		
		long userid = 0;
		
		BFLoginEntity bf = new BFLoginEntity();
		bf.setUserphone(userphone);
		bf.setUsername(userphone);
		bf.setAddtime(new Date().getTime());
		bf.setChannel(MContents.Channel.GIFT_M.key);
		
		try {
			userid = ls.addLoginEntity(bf);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(userid > 0){
			int cookieTime = 60 * 60 * 24;
			// 如果选中了记住我则cookie保存7天
			try {
				CookieUtils.saveCookie(userid, response(), cookieTime);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//分配礼包
			try {
//				long fetchResult = PreferentialFetchBuz.getInstance().fetchPacket(userid, ppid);
//				if(fetchResult<0){
//					jr.put("ret", "ok");
//					jr.put("msg", "领取红包异常!");
//				}else if(fetchResult==1){
//					jr.put("ret", "ok");
//					jr.put("msg", "已发送红包!");
//				}else if(fetchResult==2){
//					jr.put("ret", "ok");
//					jr.put("msg", "您已领取过该红包!");
//				}
//				
				//多个发送
				for (int i = 0; i < ppid_onlie.size(); i++) {
					long fetchResult = PreferentialFetchBuz.getInstance().fetchPacket(userid, ppid_onlie.get(i));
					if(fetchResult<0){
						jr.put("ret", "ok");
						jr.put("msg", "领取红包异常!");
					}else if(fetchResult==1){
						jr.put("ret", "ok");
						jr.put("msg", "已发送红包!");
					}else if(fetchResult==2){
						jr.put("ret", "ok");
						jr.put("msg", "您已领取过该红包!");
					}
					
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			jr.put("uid", userid);
			jr.put("ret", "ok");
			
			
		}else{
			jr.put("ret", "fail");
			jr.put("msg", "添加用户失败!");
		}
		
		
		
		return ActionResultUtils.renderJson(jr.toString());
		
		
		
	}

	
	
	
	/**
	 * 根据商品id、城市id、区域id查询价格并且异步返回
	 * @param pid|cityid|areaid
	 * @return
	 */
	@Path("/detail/queryPrice")
	public ActionResult queryPrice(){
		
			String pid = beat().getRequest().getParameter("pid");
			String cityid = beat().getRequest().getParameter("cityid");
			String areaid = beat().getRequest().getParameter("areaid");
			float sellprice = 0 ;
			float marketprice = 0 ;
			long  sellid = 0 ;
			Map map = new HashMap<String, Object>();
		
		try {
			List<LvzSellProductEntity> slist = RSBLL.getstance().getLvzSellProductService().getSellProductEntityList(" sell_upderdesc =  0 and product_id="+pid+" and city_id="+cityid+" and area_id="+areaid, 1, 99, "");
			
			if(slist!=null){
				if(slist.size()>0){
					 sellprice  = slist.get(0).getSell_overprice();
					 marketprice =  slist.get(0).getSell_markprice();
					 sellid = slist.get(0).getSell_id();
					 map.put("sellprice", sellprice);
					 map.put("marketprice", marketprice);
					 map.put("sellid", sellid);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ActionResultUtils.renderJson(JSON.toJSONString(map));
	}
}

