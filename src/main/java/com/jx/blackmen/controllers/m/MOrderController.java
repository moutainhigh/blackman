package com.jx.blackmen.controllers.m;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.jx.argo.ActionResult;
import com.jx.argo.Model;
import com.jx.argo.annotations.Path;
import com.jx.blackface.gaea.sell.entity.LvzPackageSellEntity;
import com.jx.blackface.gaea.sell.entity.LvzSellProductEntity;
import com.jx.blackface.gaea.usercenter.entity.BFAreasEntity;
import com.jx.blackface.gaea.usercenter.entity.BFLoginEntity;
import com.jx.blackface.orderplug.buzs.OrderBuzForHunter;
import com.jx.blackface.servicecoreclient.entity.OrderBFGEntity;
import com.jx.blackface.servicecoreclient.entity.PayOrderBFGEntity;
import com.jx.blackmen.annotaion.MCheckLogin;
import com.jx.blackmen.common.CommonUtils;
import com.jx.blackmen.common.PayCommon;
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
 * M端我的订单
 * @author duxiaofei
 * @date   2016年4月12日
 */
@Path("/m/myorder")
public class MOrderController extends BaseController{
	
	/**
	 * 立即购买
	 * @return
	 */
	@MCheckLogin
	@Path("ordersubmit")
	public ActionResult orderSubmit(){
		String sellid=beat().getRequest().getParameter("sellid");
		if( null == sellid || ("").equals(sellid.trim())){
			return view("404");
		}
		long userid=CommonUtils.getLoginuserid(beat());
//		long userid= Long.valueOf("38346088915969");
		if(userid > 0){
			Long payid = OrderPlaceUtils.orderSubmit(model(), sellid, userid);
			if(payid > 0){//跳转到确认订单页面
				
				model().add("payid", payid);
				return view("/m/order/orderconfirm");
			}
		}
		return view("404");
	}
	
	
	@MCheckLogin
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
		
		long userid=CommonUtils.getLoginuserid(beat());
		if(userid > 0){
			
			String sellcoupons = beat().getRequest().getParameter("sellcoupons");
			Map<Long,Long> sellCouponsMap = PreferentialUtils.getSellCouponsMap(sellcoupons);
			
			
			Map<String,Object> result = OrderBuzForHunter.obfhunter.orderPlace(sellids, userid, sellCouponsMap, 2);
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
		}
		return ActionResultUtils.renderText(json.toJSONString());
	}
	
	@MCheckLogin
	@Path("buyNow")
	public ActionResult buyNow() {
		String sellid = beat().getRequest().getParameter("sellid");
		if (null == sellid || ("").equals(sellid.trim())) {
			return view("404");
		}
		long userid = CommonUtils.getLoginuserid(beat());
		if (userid <= 0) {
			return view("404");
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
				List<UnitVO> suitCoupons = PreferentialMatchBuz.getInstance().matchPacketFromAccount(userid,Long.parseLong(sid),null);
				vo.setCouponList(suitCoupons);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			plist.add(vo);
		}

		model().add("orderlist", plist);
		
		return view("/m/order/orderconfirm_v1");
	}
	
	
	
	/***
	 * m站立即购买后跳转页面
	 * @param payid
	 * @return
	 */
	@Path("/goToZhiFu/{payid:\\S+}")
	public ActionResult goToZhiFu(String payid){
		if(StringUtils.isBlank(payid)){
			return view("404");
		}

		Model model = beat().getModel();
		PayOrderBFGEntity payorder = null;
		try {
			payorder = RSBLL.getstance().getPayOrderService().getPayOrderByid(Long.valueOf(payid));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.add("payorder", payorder);
		List<OrderBFGEntity> orderlist = null;
		try {
			orderlist = RSBLL.getstance().getOrderBFGService().getOrderListBycondition("payid="+payid, 1, 99, "orderid");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
						LvzPackageSellEntity lvzPackageSellEntity = RSBLL.getstance().getPackageSellService().getLvzPackageSellEntity(packageSellid);
						Map<String,Object> temp_packageSell = BeanUtils.describe(lvzPackageSellEntity);
						temp_packageSell.put(String.valueOf(packageSellid), PackageService.getInstance().getPackageSellEntityMap(lvzPackageSellEntity));
						Set_packageSellEList.add(temp_packageSell);
					} catch (Exception e) {
						e.printStackTrace();
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
					BFAreasEntity bfr = RSBLL.getstance().getAreaService().getAeasEntityById(order.getLocalid());
					if(bfr != null){
						pv.setLocalstr(bfr.getName());
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				LvzSellProductEntity sell = null;
				try {
					sell = RSBLL.getstance().getLvzSellProductService().getSellProductEntityById(order.getSellerid());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(sell != null){
					pv.setServername(sell.getSell_product_name());
				}
				pv.setPayid(payorder.getPayid());
				pv.setLocalid(order.getLocalid());
				pv.setOrderid(order.getOrderid());
				pv.setPaycount(String.valueOf(order.getPaycount()));
				plist.add(pv);
			}
			if(plist != null && plist.size() > 0){
				model.add("orderlist", plist);
			}
		}
		Long ppid = Long.parseLong(payid);
		PayCommon.pbc.dealWexixinpay(ppid, beat());
		
		return view("m/order/ordersubmit");
	}
	
	/*列表页入口**/
	@SuppressWarnings("unchecked")
	@MCheckLogin
	@Path("/index.html")
	public ActionResult gotoIndex(){
		
		//tab
		model().add("seltab", "myorder");
	
		Model model = beat().getModel();
		model.add("CommonUtils",com.jx.blackmen.utils.CommonUtils.class);
		try {
			BFLoginEntity userLoginEntity = CommonUtils.getLoginEntity(beat());
			if(null != userLoginEntity){
				//初始化列表页面
//				long userid= Long.valueOf("38346088915969");
				long userid = userLoginEntity.getUserid();
				userLoginEntity = RSBLL.getstance().getLoginService().getLoginEntityById(userid);
//				if(MyOrderService.myorder.orderInit(model, userLoginEntity.getUserid())){
				if(MyOrderService.myorder.orderInit(model,userid)){
					model.add("userLoginEntity", userLoginEntity);
					return view("/m/order/myOrder");
				}
			}
			//热门
			List<Map<String, String>> hotlist = getHostList("m");
			
			model().add("hotlist", hotlist);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view("/m/order/noOrder");
	}
	
	/***
	 * 取消订单
	 * @param payid
	 */
	@Path("/cancelpayOrder/{payid:\\S+}")
	public ActionResult cancelpayOrder(String payid){
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
		if(StringUtils.isBlank(userid) || StringUtils.isBlank(payid)){
			return view("/404");
		}
		MyOrderService.myorder.againOrder(userid, payid);
		//重新下单后跳转到订单页面
		return redirect("/m/myorder/index.html");
	}
	
}
