package com.jx.blackmen.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jx.argo.Model;
import com.jx.blackface.gaea.sell.entity.LvzSellProductEntity;
import com.jx.blackface.gaea.usercenter.entity.BFAreasEntity;
import com.jx.blackface.orderplug.buzs.OrderBuzForHunter;
import com.jx.blackface.servicecoreclient.entity.OrderBFGEntity;
import com.jx.blackface.servicecoreclient.entity.PayOrderBFGEntity;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.vo.PaySuccVo;
import com.jx.service.preferential.entity.PreferentialAccountEntity;

public class OrderPlaceUtils {
	
	public static long orderSubmit(Model model,String sell_ids,long uid){
		
		
		Map<String,Object> rmap = OrderBuzForHunter.obfhunter.orderPlace(sell_ids, uid, 2);
		long payid = 0;
		if(null != rmap && rmap.containsKey("after")){
			payid = (Long) rmap.get("after");
		}
		//payid = Long.valueOf("39253636219137");
		if(payid > 0){
			PayOrderBFGEntity payorder = null;
			try {
				payorder = RSBLL.getstance().getPayOrderService().getPayOrderByid(payid);
				if(payorder == null)
					return 0L;
			} catch (Exception e) {
				e.printStackTrace();
			}
			model.add("payorder", payorder);
			List<OrderBFGEntity> orderlist = null;
			try {
				orderlist = RSBLL.getstance().getOrderBFGService().getOrderListBycondition("payid="+payid, 1, 99, "orderid");
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(null != orderlist && orderlist.size() > 0){
				List<PaySuccVo> plist = new ArrayList<PaySuccVo>();
				for(OrderBFGEntity order : orderlist){
					PaySuccVo pv = new PaySuccVo();
					try {
						BFAreasEntity bfr = RSBLL.getstance().getAreaService().getAeasEntityById(order.getLocalid());
						if(bfr != null){
							pv.setLocalstr(bfr.getName());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					LvzSellProductEntity sell = null;
					try {
						sell = RSBLL.getstance().getLvzSellProductService().getSellProductEntityById(order.getSellerid());
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(sell != null){
						pv.setServername(sell.getSell_product_name());
						
						try {
							//查询选中商品类目可用优惠券
							//List<PreferentialAccountEntity> cList = com.jx.blackmen.frame.RSBLL.getstance().getPreferentialAccountService().getApplicable(uid, order.getSellerid());
							
							//转化
							//List<PreferentialClientEntity> couponList = PreferentialBuz.getInstance().calculatePreferential(sell, cList);
							
							//pv.setCouponList(couponList);
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}

					
					}
					pv.setPayid(payorder.getPayid());
					pv.setLocalid(order.getLocalid());
					pv.setOrderid(order.getOrderid());
					pv.setPaycount(String.valueOf(order.getPaycount()));
					
					
					plist.add(pv);
				}
				if(plist != null && plist.size() > 0){
					
						
					model.add("orderlist", plist);
					return payid;
				}
			}
		}
		return 0L;
	}
	
	
	//不提交订单，只获取支付订单信息|从我的订单点击支付进来
	public static long nowPay(Model model,long payid,long uid){
		
		if(payid > 0){
			PayOrderBFGEntity payorder = null;
			try {
				payorder = RSBLL.getstance().getPayOrderService().getPayOrderByid(payid);
				if(payorder == null)
					return 0L;
			} catch (Exception e) {
				e.printStackTrace();
			}
			model.add("payorder", payorder);
			List<OrderBFGEntity> orderlist = null;
			try {
				orderlist = RSBLL.getstance().getOrderBFGService().getOrderListBycondition("payid="+payid, 1, 99, "orderid");
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(null != orderlist && orderlist.size() > 0){
				List<PaySuccVo> plist = new ArrayList<PaySuccVo>();
				for(OrderBFGEntity order : orderlist){
					PaySuccVo pv = new PaySuccVo();
					try {
						BFAreasEntity bfr = RSBLL.getstance().getAreaService().getAeasEntityById(order.getLocalid());
						if(bfr != null){
							pv.setLocalstr(bfr.getName());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					LvzSellProductEntity sell = null;
					try {
						sell = RSBLL.getstance().getLvzSellProductService().getSellProductEntityById(order.getSellerid());
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(sell != null){
						pv.setServername(sell.getSell_product_name());
						
						try {
							//查询选中商品类目可用优惠券
							//List<PreferentialAccountEntity> cList = com.jx.blackmen.frame.RSBLL.getstance().getPreferentialAccountService().getApplicable(uid, order.getSellerid());
							
							//转化
							//List<PreferentialClientEntity> couponList = PreferentialBuz.getInstance().calculatePreferential(sell, cList);
							
							//pv.setCouponList(couponList);
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}

					
					}
					pv.setPayid(payorder.getPayid());
					pv.setLocalid(order.getLocalid());
					pv.setOrderid(order.getOrderid());
					pv.setPaycount(String.valueOf(order.getPaycount()));
					
					
					plist.add(pv);
				}
				if(plist != null && plist.size() > 0){
					
						
					model.add("orderlist", plist);
					return payid;
				}
			}
		}
		return 0L;
	}

}
