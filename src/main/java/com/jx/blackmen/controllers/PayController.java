package com.jx.blackmen.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import com.jx.argo.ActionResult;
import com.jx.argo.annotations.Path;
import com.jx.blackface.gaea.sell.entity.LvzPackageSellEntity;
import com.jx.blackface.gaea.sell.entity.LvzSellProductEntity;
import com.jx.blackface.gaea.usercenter.entity.BFAreasEntity;
import com.jx.blackface.gaea.usercenter.entity.BFLoginEntity;
import com.jx.blackface.orderplug.buzs.PayBuz;
import com.jx.blackface.orderplug.common.CommonTools;
import com.jx.blackface.orderplug.common.MessageHandler;
import com.jx.blackface.orderplug.common.PayContents;
import com.jx.blackface.orderplug.common.Sign;
import com.jx.blackface.orderplug.frame.RSBLL;
import com.jx.blackface.orderplug.vo.PayMessageVo;
import com.jx.blackface.servicecoreclient.entity.OrderBFGEntity;
import com.jx.blackface.servicecoreclient.entity.PayOrderBFGEntity;
import com.jx.blackface.tools.blackTrack.entity.WebLogs;
import com.jx.blackmen.buz.WeiXinBuz;
import com.jx.blackmen.service.PackageService;
import com.jx.blackmen.utils.WXUtils;
import com.jx.blackmen.vo.PaySuccVo;
import com.jx.service.messagecenter.entity.PayMessageEntity;

public class PayController extends BaseController {
	
	

	@Path("/pay/detail")
	public ActionResult detailpay(){
		Object tokenid=beat().getRequest().getSession().getAttribute("tokenid");
		return null;
	}
	//@WXCheckLogin
	@Path("/wxpay/readypay")
	public ActionResult test(){
		WebLogs logs = WebLogs.getIntanse(PayController.class, "/wxpay/readypay");
		
		logs.putParam("testParam", "dqx");
		logs.printErrorLog();
		
		
		ActionResult buildWXUrl = buildWXUrl();
		if(buildWXUrl != null){
			return buildWXUrl;
		}
		String openId = model().get("openId").toString();
		logs.putParam("openid", openId);
		if(StringUtils.isBlank(openId)){
			logs.printInfoLog();
			return view("404");
		}
		try {
			BFLoginEntity bfLoginE = WeiXinBuz.wb.getBFLoginEByopenId(openId);
			if(null != bfLoginE){
				long userid= bfLoginE.getUserid();
				String payid_s = beat().getRequest().getParameter("payid");
				logs.putParam("payid_s", payid_s);
				
				//查询列表信息start......
				
				long payorderid = 0;
				if(null != payid_s && !"".equals(payid_s)){
					payorderid = Long.parseLong(payid_s);
				}
				
				PayOrderBFGEntity peg2 = null;
				if(payorderid > 0){
					
				}
				
				model().add("orderid", payorderid);
				
				long payid = StringUtils.isNotBlank(payid_s)?Long.valueOf(payid_s):0;
				
				if(payid > 0){
					
					try {
						peg2 = RSBLL.rb.getPayOrderService().getPayOrderByid(payorderid);
						if(null != peg2){
							model().add("payorder", peg2);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
					List<OrderBFGEntity> orderlist = null;
					try {
						orderlist = RSBLL.rb.getOrderBFGService().getOrderListBycondition("payid="+payorderid, 1, 99, "orderid");
					} catch (Exception e) {
						logs.printErrorLog(e);
					}
					if(null != orderlist && orderlist.size() > 0){
						
						model().add("productCount", orderlist.size());  //共计购买多少个商品
						
						//为商品包时进行特殊处理start===========================
						Set<Long> Set_packageSellE = new HashSet<Long>(); 
						Iterator<OrderBFGEntity> iterator = orderlist.iterator();
						while(iterator.hasNext()){
							OrderBFGEntity order = iterator.next();
							if(order.getPackagesellid() == 0){
								continue;
							}
							iterator.remove();  //排除掉包含了商品包的订单信息
							Set_packageSellE.add(order.getPackagesellid());
						}
						
						//如果存在商品包
						if(!Set_packageSellE.isEmpty()){
							List<Map<String,Object>> Set_packageSellEList = new ArrayList<Map<String,Object>>();
							Map<String,Object> packageSellList = new HashMap<String,Object>();
							for(Long packageSellid : Set_packageSellE){
								try {
									LvzPackageSellEntity lvzPackageSellEntity = com.jx.blackmen.frame.RSBLL.getstance().getPackageSellService().getLvzPackageSellEntity(packageSellid);
									Map<String,Object> temp_packageSell = BeanUtils.describe(lvzPackageSellEntity);
									temp_packageSell.put(String.valueOf(packageSellid), PackageService.getInstance().getPackageSellEntityMap(lvzPackageSellEntity));
									Set_packageSellEList.add(temp_packageSell);
								} catch (Exception e) {
									e.printStackTrace();
									logs.printErrorLog(e);
								}
							}
							if(!Set_packageSellEList.isEmpty()){
								beat().getModel().add("Set_packageSellEList", Set_packageSellEList);
							}
						}
						//为商品包时进行特殊处理end=====================================
						
						
						List<PaySuccVo> plist = new ArrayList<PaySuccVo>();
						for(OrderBFGEntity order : orderlist){
							PaySuccVo pv = new PaySuccVo();
							try {
								BFAreasEntity bfr = RSBLL.rb.getAreaService().getAeasEntityById(order.getLocalid());
								if(bfr != null){
									pv.setLocalstr(bfr.getName());
								}
							} catch (Exception e) {
								e.printStackTrace();
								logs.printErrorLog(e);
							}
							LvzSellProductEntity sell = null;
							try {
								sell = RSBLL.rb.getLvzSellProductService().getSellProductEntityById(order.getSellerid());
							} catch (Exception e) {
								e.printStackTrace();
								logs.printInfoLog(e);
							}
							if(sell != null){
								pv.setServername(sell.getSell_product_name());
							}
							pv.setPayid(payorderid);
							pv.setLocalid(order.getLocalid());
							pv.setOrderid(order.getOrderid());
							pv.setPaycount(String.valueOf(order.getPaycount()));
							plist.add(pv);
						}
						if(plist != null && plist.size() > 0){
							model().add("orderlist", plist);
						}
					}
					
					//查询列表信息end......
					
					String ret = "";
					
					PayOrderBFGEntity peg = RSBLL.rb.getPayOrderService().getPayOrderByid(payid);
					
					try {
						ret = PayBuz.pb.startPayByrecorde(payid, (long)(peg.getPaycount()*100), openId, 1, 99);//.wb.startPay(payorderid, (long)(peg.getPaycount()*100), openid,1);
						logs.putParam("startpayret", ret);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						logs.printErrorLog(e1);
					}
					StringBuffer url = beat().getRequest().getRequestURL();
					String queryString = beat().getRequest().getQueryString();
					if(!StringUtils.isEmpty(queryString))
							url.append("?" + queryString);
					String ticket = "";
					try {
						ticket = PayBuz.pb.getWeixinJSToken(PayContents.weixin_app_id, PayContents.weixin_app_secret_id);
						Map<String,Object> map = Sign.tranceTokentojst(ticket, url.toString());
						beat().getModel().addAll(map);
					} catch (Exception e) {
						e.printStackTrace();
						logs.printErrorLog(e);
						
					}
					
					if(!ret.equals("")){
						String tstamp = new Date().getTime()+"";
						PayMessageEntity pme = MessageHandler.newstance().trancePayResultToentity(ret);
						logs.putParam("ENTITY_PREPAYID", pme.getPrepay_id());
						PayMessageVo payvo = PayBuz.pb.trancePayResultToentity(ret);
						logs.putParam("PAYVO_PREPAYID", payvo.getPrepay_id());
						
						String signkey = "appId="+PayContents.weixin_app_id+"&nonceStr=ibuaiVcKdpRxkhJA&package=prepay_id="
						+pme.getPrepay_id()+"&signType=MD5&timeStamp="+tstamp+"&key=EKGt478kaAWygmU9AcfkK2vanc8ss8Xj";
						
						logs.putParam("BEFORE_signkey", signkey);
						signkey = CommonTools.MD5(signkey).toUpperCase();
						logs.putParam("AFTER_signkey", signkey);
						beat().getModel().add("signkey", signkey);
						beat().getModel().add("timeStamp", tstamp);
						beat().getModel().add("nonstr", "ibuaiVcKdpRxkhJA");
						beat().getModel().add("pmestr", ret);
						beat().getModel().add("pme", pme);
						model().add("orderid", payid);
					}
					
					logs.printErrorLog();
					beat().getRequest().getSession().removeAttribute("tokenid");
					return view("/wx/order/ordersubmit");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view("404");
		
	}
	
	
	//@Path("/wxpay/readypay")
	public ActionResult reqpay(){
		String payid = beat().getRequest().getParameter("orderid");
		System.out.println("test payid is "+payid);
		
		String openid = WXUtils.getOpenId(beat().getRequest());
		
		System.out.println("black men get openid is "+openid);
		
		long payorderid = 0;
		if(null != payid && !"".equals(payid)){
			payorderid = Long.parseLong(payid);
		}
		model().add("productName", "测试商品");
		
		PayOrderBFGEntity peg = null;
		if(payorderid > 0){
			try {
				peg = RSBLL.rb.getPayOrderService().getPayOrderByid(payorderid);
				if(null != peg){
					model().add("payorder", peg);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			List<OrderBFGEntity> orderlist = null;
			try {
				orderlist = RSBLL.rb.getOrderBFGService().getOrderListBycondition("payid="+payid, 1, 99, "orderid");
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(null != orderlist && orderlist.size() > 0){
				List<PaySuccVo> plist = new ArrayList<PaySuccVo>();
				for(OrderBFGEntity order : orderlist){
					PaySuccVo pv = new PaySuccVo();
					try {
						BFAreasEntity bfr = RSBLL.rb.getAreaService().getAeasEntityById(order.getLocalid());
						if(bfr != null){
							pv.setLocalstr(bfr.getName());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					LvzSellProductEntity sell = null;
					try {
						sell = RSBLL.rb.getLvzSellProductService().getSellProductEntityById(order.getSellerid());
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(sell != null){
						pv.setServername(sell.getSell_product_name());
					}
					pv.setPayid(payorderid);
					pv.setLocalid(order.getLocalid());
					pv.setOrderid(order.getOrderid());
					pv.setPaycount(String.valueOf(order.getPaycount()));
					plist.add(pv);
				}
				if(plist != null && plist.size() > 0){
					model().add("orderlist", plist);
				}
			}
			
		}
		
		model().add("orderid", payorderid);
		
		//if(peg != null){
			String ret = "";
			try {
				ret = PayBuz.pb.startPayByrecorde(payorderid, (long)(peg.getPaycount()*100), openid, 1, 1);//.wb.startPay(payorderid, (long)(peg.getPaycount()*100), openid,1);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			StringBuffer url = beat().getRequest().getRequestURL();
			String queryString = beat().getRequest().getQueryString();
			if(!StringUtils.isEmpty(queryString))
					url.append("?" + queryString);
			String ticket = "";
			try {
				ticket = PayBuz.pb.getWeixinJSToken(PayContents.weixin_app_id, PayContents.weixin_app_secret_id);
				Map<String,Object> map = Sign.tranceTokentojst(ticket, url.toString());
				beat().getModel().addAll(map);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(!ret.equals("")){
				System.out.println("black men readpay res is ======="+ret);
				String tstamp = new Date().getTime()+"";
				PayMessageEntity pme = MessageHandler.newstance().trancePayResultToentity(ret);
				String signkey = "appId="+PayContents.weixin_app_id+"&nonceStr=ibuaiVcKdpRxkhJA&package=prepay_id="
				+pme.getPrepay_id()+"&signType=MD5&timeStamp="+tstamp+"&key=EKGt478kaAWygmU9AcfkK2vanc8ss8Xj";
				
				
				System.out.println("black BEFORE signkey is ------->"+signkey);
				signkey = CommonTools.MD5(signkey).toUpperCase();
				System.out.println("black signkey is ------->"+signkey);
				//JSONObject jj = new JSONObject();
				beat().getModel().add("signkey", signkey);
				beat().getModel().add("timeStamp", tstamp);
				beat().getModel().add("nonstr", "ibuaiVcKdpRxkhJA");
				beat().getModel().add("pmestr", ret);
				beat().getModel().add("pme", pme);
				//beat().getModel().add("msg", jj.toString());
			}
		//}
		
			return view("/wx/order/ordersubmit");
	}
}
