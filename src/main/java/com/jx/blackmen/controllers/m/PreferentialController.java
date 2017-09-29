package com.jx.blackmen.controllers.m;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jx.argo.ActionResult;
import com.jx.argo.annotations.Path;
import com.jx.blackface.gaea.sell.entity.LvzSellProductEntity;
import com.jx.blackface.gaea.usercenter.entity.BFLoginEntity;
import com.jx.blackface.tools.blackTrack.entity.WebLogs;
import com.jx.blackmen.common.CommonUtils;
import com.jx.blackmen.controllers.BaseController;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.utils.ActionResultUtils;
import com.jx.blackmen.utils.CookieUtils;
import com.jx.service.preferential.plug.buz.PreferentialAccountBuz;
import com.jx.service.preferential.plug.buz.PreferentialFetchBuz;
import com.jx.service.preferential.plug.buz.PreferentialMatchBuz;
import com.jx.service.preferential.plug.buz.PreferentialPacketBuz;
import com.jx.service.preferential.plug.buz.PreferentialServiceBuz;
import com.jx.service.preferential.plug.utils.PreferentialUtils;
import com.jx.service.preferential.plug.vo.PacketVO;

/**
 * 本次优惠券发放准备红包id为：
 * 满2000减50：39601420503297
 * 满4000减100：39601455436289
 * 满6000减200：39601499874049
 * 满50.01减50：39601541210369（此为系统自动发放，不计入展示）
 * @author eilir
 *
 */
@Path("/m")
public class PreferentialController extends BaseController{
	private static final Logger logger = LoggerFactory.getLogger(PreferentialController.class);

	
	@Path("preferential/fetch")
	public ActionResult fetchPreferential(){
		JSONObject json = new JSONObject();
		json.put("success", false);
		String ppid = beat().getRequest().getParameter("packetId");
		String userID = null;
		try {
			userID = CookieUtils.getUseridByCookie(beat().getRequest(),beat().getResponse());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if(null == userID){
			String path ="http://m.lvzheng.com/m/coupon/list";
			json.put("login", "http://m.lvzheng.com/m/login.html?flag=1&reach_url="+path+"?packetId="+ppid);
			json.put("code", -999);
			return ActionResultUtils.renderJson(json);
		}else{
			if(StringUtils.isEmpty(ppid)){
				json.put("msg", "参数错误：【packetid:"+ ppid +"】");
				return ActionResultUtils.renderJson(json);
			}
			try {
				long fetchResult = PreferentialFetchBuz.getInstance().fetchPacket(Long.parseLong(userID), Long.parseLong(ppid));
				System.out.println("------preferential fetch result:"+fetchResult);
				json.put("code", fetchResult);
				if(fetchResult == 0){
					json.put("msg", "您已经领取过该优惠券，不能重复领取！");
				}else if(fetchResult == 1){
					json.put("msg", "抱歉，优惠券已经领完！");
				}else{
					json.put("msg", "服务器错误，领取失败，请联系客服！");
				}
			} catch (NumberFormatException e) {
				json.put("msg", "网络错误！");
				e.printStackTrace();
			} catch (Exception e) {
				json.put("msg", "网络错误！");
				e.printStackTrace();
			}
			return ActionResultUtils.renderJson(json);
		}
	}
	
	
	
	
	
	private static List<String> PACKET_IDS;
	static {
		PACKET_IDS = new ArrayList<String>();
		PACKET_IDS.add("40027023366145");
		PACKET_IDS.add("40027029029633");
		PACKET_IDS.add("40027027760641");
		PACKET_IDS.add("40027025993729");
	}
	@Path("preferential/active")
	public ActionResult activePage(){
		boolean isTime =isTrueTime();
		int timeFlag = isTime ? 1 : 0;
		if (isTime) {
			
			long uid = CommonUtils.getLoginuserid(beat());
			String userId = uid > 0 ? uid + "":null;
			List<PacketVO> showPackets = new ArrayList<PacketVO>();
			
			for(String id : PACKET_IDS){
				PacketVO p = PreferentialPacketBuz.getInstance().getPacketVOCacheWithUser(Long.parseLong(id),uid);
				p.setName("通用优惠券");
				if(null != p){
					showPackets.add(p);
				}
				//System.out.println(p.getUnitName()+"---->"+p.getQuota());
			}
			model().add("packets", showPackets);
		}
		
		String packetId = beat().getRequest().getParameter("packetId");
		if(StringUtils.isNotEmpty(packetId)){
			model().add( "packetId",packetId);
		}
	
		model().add("timeFlag", timeFlag);
		model().add("auth",CommonUtils.getLoginuserid(beat()));
		return view("m/night/mnight");
		
	}
	
	@Path("preferential/day")
	public ActionResult activeDayPage(){
		return view("m/day/mday");
	}
	static List<Long> COUPON_PACKET_IDS = null;
	static {
		COUPON_PACKET_IDS = new ArrayList<Long>();
		COUPON_PACKET_IDS.add(40050910852865L);//内资20
		COUPON_PACKET_IDS.add(40050909798913L);//商标30
		COUPON_PACKET_IDS.add(40050908535297L);//套餐50
		COUPON_PACKET_IDS.add(40050906803201L);//地址50
	}
	
	
	@Path("preferential/coupon")
	public ActionResult couponPage(){
		
		try {
			long uid = CommonUtils.getLoginuserid(beat());
			String userId = uid > 0 ? uid + "":null;
			Map<Long,PacketVO> showPackets = new HashMap<Long,PacketVO>();
			
			for(Long id : COUPON_PACKET_IDS){
				PacketVO p = PreferentialPacketBuz.getInstance().getPacketVOCache(id);
				p.setName("通用优惠券");
				int last =(int)((p.getTotalCount() - p.getFetchCount())/p.getTotalCount() * 100);
				p.setLastCount(last);
				//p.setFetched(1);
				if(null != p){
					showPackets.put(id, p);
				}
				//System.out.println(p.getUnitName()+"---->"+p.getQuota());
			}
			//看用户是否存在，并且看一下用户是不是领过优惠券
			if(!StringUtils.isEmpty(userId)){
				
				for (Long key : showPackets.keySet()) {
					PacketVO p = showPackets.get(key);
					if (p == null)
						continue;
					
					if (PreferentialServiceBuz.getService().getPreferentialAccountService()
							.getCountOfPreferentialAccount(" packet_id = "+p.getId()+" and user_id = "+uid) > 0) {
						p.setUserFetch(1);
					}
					
				}
			}
			
			model().add("packets", showPackets);
			
			String packetId = beat().getRequest().getParameter("packetId");
			if(StringUtils.isNotEmpty(packetId)){
				model().add("packetId",packetId);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		return view("wx/coupon/coupon");
	}
	
	private boolean isTrueTime(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 20);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		
		long startTime = cal.getTime().getTime();
		long endTime = startTime + 1000*60*60*4;
		
		long nowTime = System.currentTimeMillis();
		
		if (nowTime > startTime && nowTime < endTime)
			return true;
		
		return false;
	}
	@Path("preferential/removeCache")
	public ActionResult removeCache(){
		try {
			PreferentialPacketBuz.getInstance().removePacketCache();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	@Path("preferential/getRecommend")
	public ActionResult getRecommendCoupons(){
		JSONObject result = new JSONObject();
		WebLogs logs = WebLogs.getIntanse(PreferentialController.class, "getRecommendCoupons");
		
		try {
			
			long uid = CommonUtils.getLoginuserid(beat());
			String userId = uid > 0 ? uid + "":null;
			String productCode = beat().getRequest().getParameter("productCode");
			String productId = beat().getRequest().getParameter("productId");
			String cityId = beat().getRequest().getParameter("cityId");
			String areaId = beat().getRequest().getParameter("areaId");

			//一些相关日志输出一下
			logs.putParam("uid", uid);
			logs.putParam("pid", productId);
			logs.putParam("productCode", productCode);
			logs.putParam("cityId", cityId);
			logs.putParam("areaId", areaId);
			
			double productPrice = 0;
			List<LvzSellProductEntity> slist = RSBLL.getstance().getLvzSellProductService().getSellProductEntityList(" sell_upderdesc =  0 and product_id="+productId+" and city_id="+cityId+" and area_id="+areaId, 1, 99, "");
			if (!CollectionUtils.isEmpty(slist)) {
				productPrice  = slist.get(0).getSell_overprice();
			}
			logs.putParam("productPrice", productPrice);
			List<PacketVO> packetList = PreferentialMatchBuz.getInstance().matchPacket(uid, productCode, areaId, productPrice, 2); 
			List<PacketVO> accountList = null;
			if (uid > 0) {
				accountList = PreferentialMatchBuz.getInstance().matchPacketFromUserid(uid,productCode,areaId,productPrice,2);
			}
				
			int psize = CollectionUtils.isEmpty(packetList) ? 0 : packetList.size();
			int asize = CollectionUtils.isEmpty(accountList) ? 0 : accountList.size();
			
			
			if (psize > 0 && asize > 0) {//已领，可领  都有的情况， 一人一个
				packetList = PreferentialUtils.subList(packetList, 1);
				result.put("avaList", JSON.toJSONString(packetList));

				accountList = PreferentialUtils.subList(accountList, 1);
				result.put("holdList", JSON.toJSONString(accountList));
			} else if (psize >0 && asize == 0) {//可领有，已领没有  那就是已领是2个
				result.put("avaList", JSON.toJSONString(packetList));
			}else if (psize == 0 && asize >0) {//已领有，可领没有  那就是已领是2个
				result.put("holdList", JSON.toJSONString(accountList));
			}

			logs.printErrorLog();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		
		return ActionResultUtils.renderJson(result);
	}
	
}
