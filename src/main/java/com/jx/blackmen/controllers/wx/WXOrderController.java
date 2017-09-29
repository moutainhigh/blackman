package com.jx.blackmen.controllers.wx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.jx.argo.ActionResult;
import com.jx.argo.Model;
import com.jx.argo.annotations.Path;
import com.jx.blackface.gaea.sell.entity.LvzSellProductEntity;
import com.jx.blackface.gaea.usercenter.entity.BFAreasEntity;
import com.jx.blackface.gaea.usercenter.entity.BFLoginEntity;
import com.jx.blackface.orderplug.buzs.OrderBuzForHunter;
import com.jx.blackmen.annotaion.WXCheckLogin;
import com.jx.blackmen.buz.WeiXinBuz;
import com.jx.blackmen.controllers.BaseController;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.service.MyOrderService;
import com.jx.blackmen.service.PackageService;
import com.jx.blackmen.utils.ActionResultUtils;
import com.jx.blackmen.utils.OrderPlaceUtils;
import com.jx.blackmen.vo.PaySuccVo;
import com.jx.service.preferential.plug.buz.PreferentialMatchBuz;
import com.jx.service.preferential.plug.utils.PreferentialUtils;
import com.jx.service.preferential.plug.vo.UnitVO;

/***
 * 微信端我的订单
 * @author duxiaofei
 * @date   2016年4月12日
 */
@Path("/wx/myorder")
public class WXOrderController extends BaseController{
	
	
	@Path("ordersubmit1")
	public ActionResult orderSubmit_v1(){
		JSONObject json = new JSONObject();
		json.put("code", -1);
		String sellids=beat().getRequest().getParameter("sellid");
		
		String packageSellid = beat().getRequest().getParameter("packageSellid"); //商品包定价条目id
		
		//特殊处理商品包拼接
		if(StringUtils.isNotBlank(packageSellid)){
			if(StringUtils.isBlank(sellids)){
				sellids += PackageService.getInstance().getPackageIdAndSellidStr(packageSellid);
			}else{
				sellids +=","+ PackageService.getInstance().getPackageIdAndSellidStr(packageSellid);
			}
		}
		if(StringUtils.isBlank(sellids)){
			json.put("code", -404);
			return ActionResultUtils.renderText(json.toJSONString());
		}
		
		String openId = beat().getRequest().getParameter("openid");
		BFLoginEntity bfLoginE = null;
		try {
			bfLoginE = WeiXinBuz.wb.getBFLoginEByopenId(openId);
			if(null == bfLoginE){
				return ActionResultUtils.renderText(json.toJSONString());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
			
		String sellcoupons = beat().getRequest().getParameter("sellcoupons");
		Map<Long,Long> sellCouponsMap = PreferentialUtils.getSellCouponsMap(sellcoupons);
		
		
		Map<String,Object> result = OrderBuzForHunter.obfhunter.orderPlace(sellids, bfLoginE.getUserid(), sellCouponsMap, 2);
		long payid = 0;
		long zero = 0;
		if (result != null) {
			payid = result.get("after") != null ? (Long) result.get("after") : 0;
			zero = result.get("zero") != null ? 1 : 0;
		}
		
		if(payid > 0){//跳转到确认订单页面
			json.put("code", payid);
			json.put("zero", zero);
			return ActionResultUtils.renderText(json.toJSONString());
		}
		return ActionResultUtils.renderText(json.toJSONString());
	}
	
	@WXCheckLogin
	@Path("buyNow")
	public ActionResult buyNow() {
		String sellid = beat().getRequest().getParameter("sellid");
		if (null == sellid || ("").equals(sellid.trim())) {
			return view("404");
		}
		String openId = model().get("openId").toString();
		BFLoginEntity bfLoginE = null;
		try {
			bfLoginE = WeiXinBuz.wb.getBFLoginEByopenId(openId);
			if(null == bfLoginE){
				return view("404");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		List<PaySuccVo> plist = new ArrayList<PaySuccVo>();
		String[] sellids = sellid.split(",");
		for (String sid : sellids) {
			if (StringUtils.isBlank(sid))
				continue;

			PaySuccVo vo = new PaySuccVo();

			LvzSellProductEntity sell = null;
			try {
				sell = RSBLL.getstance().getLvzSellProductService().getSellProductEntityById(Long.parseLong(sid));
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (sell == null) {
				continue;
			}

			try {
				BFAreasEntity bfr = RSBLL.getstance().getAreaService().getAeasEntityById(sell.getArea_id());
				if (bfr != null) {
					vo.setLocalstr(bfr.getName());
					vo.setLocalid(bfr.getAreaid());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			vo.setOrderid(Long.parseLong(sid));
			vo.setServername(sell.getSell_product_name());
			vo.setPaycount(String.valueOf(sell.getSell_overprice()));
			
			
			try {
				List<UnitVO> suitCoupons = PreferentialMatchBuz.getInstance().matchPacketFromAccount(bfLoginE.getUserid(),Long.parseLong(sid),null);
				vo.setCouponList(suitCoupons);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			plist.add(vo);
		}

		model().add("orderlist", plist);
		
		return view("/wx/order/orderconfirm_v1");
	}
	
	
	
	/**
	 * 直接下单购买
	 * @return
	 */
	@WXCheckLogin
	@Path("/ordersubmit/{sellid:\\S+}")
	public ActionResult orderSubmit(String sellid){
		Object tokenid=beat().getRequest().getSession().getAttribute("tokenid");
		if(tokenid == null){
			return redirect("/wx/index");
		}
//		ActionResult buildWXUrl = buildWXUrl();
//		if(buildWXUrl != null){
//			return buildWXUrl;
//		}
		String openId = model().get("openId").toString();
		if(StringUtils.isBlank(openId)){
			return view("404");
		}
		if( null == sellid || ("").equals(sellid.trim())){
			return view("404");
		}
		try {
			BFLoginEntity bfLoginE = WeiXinBuz.wb.getBFLoginEByopenId(openId);
			if(null != bfLoginE){
				long userid= bfLoginE.getUserid();
//			    long userid= Long.valueOf("38346088915969");
				Long payid = OrderPlaceUtils.orderSubmit(model(), sellid, userid);
				if(payid > 0){
					beat().getRequest().getSession().removeAttribute("tokenid");
					model().add("payid", payid);
					return view("/wx/order/orderconfirm");
				}
				
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view("404");
	}
	
	
	/**
	 * 从我的订单待支付进来
	 * @return
	 */
	@WXCheckLogin
	@Path("/nowPay/{sellid:\\S+}")
	public ActionResult nowPay(String sellid){
		Object tokenid=beat().getRequest().getSession().getAttribute("tokenid");
		
		String _payid = beat().getRequest().getParameter("payid");   
		long payid = 0;
		if(tokenid == null){
			return redirect("/wx/index");
		}
		ActionResult buildWXUrl = buildWXUrl();
		if(buildWXUrl != null){
			return buildWXUrl;
		}
		String openId = model().get("openId").toString();
		if(StringUtils.isBlank(openId)){
			return view("404");
		}
		if( null == sellid || ("").equals(sellid.trim())){
			return view("404");
		}
		
		if(StringUtils.isNotEmpty(_payid)){
			payid = Long.valueOf(_payid);
		}
		try {
			BFLoginEntity bfLoginE = WeiXinBuz.wb.getBFLoginEByopenId(openId);
			if(null != bfLoginE){
				long userid= bfLoginE.getUserid();
				     payid = OrderPlaceUtils.nowPay(model(), payid, userid);
				if(payid > 0){
					beat().getRequest().getSession().removeAttribute("tokenid");
					model().add("sellid", sellid);
					return view("/wx/order/orderconfirm");
				}
				
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view("404");
	}
	
	/*列表页入口**/
	@SuppressWarnings("unchecked")
	@WXCheckLogin
	@Path("/index.html")
	public ActionResult gotoIndex(){
		
		
		//tab
		model().add("seltab", "myorder");
	
		String openId = model().get("openId").toString();
		
		Model model = beat().getModel();
		try {
			BFLoginEntity userLoginEntity = WeiXinBuz.wb.getBFLoginEByopenId(openId);
			if(null != userLoginEntity){
				//初始化列表页面
				if(MyOrderService.myorder.orderInit(model, userLoginEntity.getUserid())){
					model.add("userLoginEntity", userLoginEntity);
					return view("/wx/order/myOrder");
				}
			}
			//热门
			List<Map<String, String>> hotlist = getHostList("wx");
			
			model().add("hotlist", hotlist);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view("/wx/order/noOrder");
	}
	
	/***
	 * 取消订单
	 * @param payid
	 */
	@Path("/cancelpayOrder/{payid:\\S+}")
	public ActionResult cancelpayOrder(String payid){
		ActionResult buildWXUrl = buildWXUrl();
		if(buildWXUrl != null){
			return buildWXUrl;
		}
		return MyOrderService.myorder.cancelpayOrder(beat(),payid);
	}
	
	/***
	 * 重新下单
	 * @param userid
	 * @param payid
	 * @return
	 */
	@Path("/againOrder/{userid:\\S+}/{payid:\\S+}")
	public ActionResult againOrder(String userid,String payid){
		ActionResult buildWXUrl = buildWXUrl();
		if(buildWXUrl != null){
			return buildWXUrl;
		}
		if(StringUtils.isBlank(userid) || StringUtils.isBlank(payid)){
			return view("/404");
		}
		MyOrderService.myorder.againOrder(userid, payid);
		//重新下单后跳转到订单页面
		return redirect("/wx/myorder/index.html");
	}
}
