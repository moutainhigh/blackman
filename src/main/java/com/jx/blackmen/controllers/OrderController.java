package com.jx.blackmen.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jx.argo.ActionResult;
import com.jx.argo.annotations.Path;
import com.jx.blackface.gaea.sell.entity.LvzSellProductEntity;
import com.jx.blackface.gaea.usercenter.entity.BFAreasEntity;
import com.jx.blackface.orderplug.buzs.OrderBuzForHunter;
import com.jx.blackface.servicecoreclient.entity.OrderBFGEntity;
import com.jx.blackface.servicecoreclient.entity.PayOrderBFGEntity;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.vo.PaySuccVo;

public class OrderController extends BaseController{

	@Path("/ordersubmit")
	
	public ActionResult tobuysell(){
		String sell_ids = beat().getRequest().getParameter("sell_ids");
		if(null == sell_ids || "".equals(sell_ids)){
			return view("pay/");
		}
		String uid = beat().getRequest().getParameter("uid");
		
		
		Map<String,Object> rmap = OrderBuzForHunter.obfhunter.orderPlace(sell_ids, Long.parseLong(uid), 2);
		long payid = 0;
		if(null != rmap && rmap.containsKey("after")){
			payid = (Long) rmap.get("after");
		}
		if(payid > 0){
			PayOrderBFGEntity payorder = null;
			try {
				payorder = RSBLL.getstance().getPayOrderService().getPayOrderByid(payid);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			model().add("payorder", payorder);
			List<OrderBFGEntity> orderlist = null;
			try {
				orderlist = RSBLL.getstance().getOrderBFGService().getOrderListBycondition("", 1, 99, "orderid");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(null != orderlist && orderlist.size() > 0){
				List<PaySuccVo> plist = new ArrayList<PaySuccVo>();
				for(OrderBFGEntity en : orderlist){
					PaySuccVo pv = new PaySuccVo();
					try {
						BFAreasEntity bfr = RSBLL.getstance().getAreaService().getAeasEntityById(en.getLocalid());
						if(bfr != null){
							pv.setLocalstr(bfr.getName());
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					LvzSellProductEntity server = null;
					try {
						server = RSBLL.getstance().getLvzSellProductService().getSellProductEntityById(en.getSellerid());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(server != null){
						pv.setServername(server.getSell_product_name());
					}
					pv.setPayid(payorder.getPayid());
					pv.setLocalid(en.getLocalid());
					plist.add(pv);
				}
				if(plist != null && plist.size() > 0){
					model().add("orderlist", plist);
				}
			}
		}
		return view("pay/ordersubmit");
	}
	
}
