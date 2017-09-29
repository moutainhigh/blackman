package com.jx.blackmen.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import com.jx.argo.ActionResult;
import com.jx.argo.BeatContext;
import com.jx.argo.Model;
import com.jx.blackface.gaea.sell.entity.LvzPackageSellEntity;
import com.jx.blackface.gaea.sell.entity.LvzSellProductEntity;
import com.jx.blackface.gaea.usercenter.entity.BFAreasEntity;
import com.jx.blackface.orderplug.buzs.OrderBuz;
import com.jx.blackface.orderplug.buzs.OrderBuzForHunter;
import com.jx.blackface.servicecoreclient.entity.OrderBFGEntity;
import com.jx.blackface.servicecoreclient.entity.PayOrderBFGEntity;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.utils.ActionResultUtils;
import com.jx.blackmen.utils.MContents;
import com.jx.blackmen.utils.TimeUtils;

/**
 * 我的订单服务
 * @author duxiaofei
 * @date   2016年4月13日
 */
public class MyOrderService {
	
	public static MyOrderService myorder = new MyOrderService();
	
	/***
	 * 订单公共的初始化页面
	 * @param model
	 * @param userid
	 * @throws Exception
	 */
	public boolean orderInit(Model model,Long userid){
		boolean flag = false;
		try {
			List<PayOrderBFGEntity> payorderElist = RSBLL.getstance().getPayOrderService().getPoelistBypage("(paystate='1' or paystate='0' or paystate='9') and userid='"+userid+"'", 1, 99, "addtime desc");
			if(null != payorderElist && !payorderElist.isEmpty()){
				flag = true;
				List<Map<String,Object>> payorderMapList = new ArrayList<Map<String,Object>>();
				for(PayOrderBFGEntity payorderbfg : payorderElist){
					Map<String,Object> payorderMap = new HashMap<String, Object>(); 
					payorderMap = BeanUtils.describe(payorderbfg);
					payorderMap.put("paystateValue",MContents.PayState.getPayStateValueByKey(payorderbfg.getPaystate())); //支付状态
					payorderMap.put("addtimeValue", TimeUtils.longToString(payorderbfg.getAddtime())); //下单时间;
					List<Map<String,Object>> orderMapList = new ArrayList<Map<String,Object>>();
					try {
						List<OrderBFGEntity> orderEList = RSBLL.getstance().getOrderBFGService().getOrderListBycondition("payid='"+payorderbfg.getPayid()+"'", 1, 99, "paycount desc");
						if(null != orderEList && !orderEList.isEmpty()){
							for(OrderBFGEntity orderE : orderEList){
								Map<String,Object> orderMap = new HashMap<String, Object>();
								orderMap = BeanUtils.describe(orderE);
								if(orderE.getPackagesellid() != 0){
									LvzPackageSellEntity lvzPackageSellEntity = RSBLL.getstance().getPackageSellService().getLvzPackageSellEntity(orderE.getPackagesellid());
									if(null != lvzPackageSellEntity){
										orderMap.put("packageFlag","true");
										orderMap.put("packageName", lvzPackageSellEntity.getPackage_name()); //商品包名称
										orderMap.put("packagePrice", lvzPackageSellEntity.getSell_overprice()); //商品包价格
									}
								}

								LvzSellProductEntity sellProductE = RSBLL.getstance().getLvzSellProductService().getSellProductEntityById(orderE.getSellerid());
								long areaid = 0l;
								if(null != sellProductE){
									orderMap.put("productName", sellProductE.getSell_product_name());
									orderMap.put("sell_overprice", sellProductE.getSell_overprice());
									areaid = sellProductE.getArea_id();
								}
								
								BFAreasEntity aeasEntity = RSBLL.getstance().getAreaService().getAeasEntityById(areaid);
								if(aeasEntity != null){
									orderMap.put("aeasname", aeasEntity.getName());
									orderMap.put("aeasid", aeasEntity.getAreaid());
									BFAreasEntity cityEntity = RSBLL.getstance().getAreaService().getAeasEntityById(Long.valueOf(aeasEntity.getParentid()));
									orderMap.put("cityname", cityEntity.getName());
									orderMap.put("cityid", cityEntity.getAreaid());
								}
								orderMapList.add(orderMap);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					payorderMap.put(String.valueOf(payorderbfg.getPayid()),orderMapList);
					payorderMapList.add(payorderMap);
				}
				model.add("payorderMapList", payorderMapList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	
	/***
	 * 取消订单服务
	 * @param beat
	 * @param payid
	 * @return
	 */
	public ActionResult cancelpayOrder(BeatContext beat,String payid){
		if(StringUtils.isBlank(payid)){
			return ActionResultUtils.renderJson("{\"error\":}");
		}
		String userid = beat.getRequest().getParameter("userid");
		if(StringUtils.isBlank(userid)){
			return ActionResultUtils.renderJson("{\"error\":}");
		}
		try {
			String jsonMSG = OrderBuz.ob.cancelOrderByPayid(Long.valueOf(userid), Long.valueOf(payid));
			return ActionResultUtils.renderJson(jsonMSG);
		} catch (Exception e) {
			e.printStackTrace();
			return ActionResultUtils.renderJson("{\"error\":}");
		}
	}
	
	private static final int orderchannel = 2; //渠道为微信
	/***
	 * 重新下单
	 * @param userid
	 * @param payid
	 */
	public void againOrder(String userid,String payid){
		List<OrderBFGEntity> orderList = null;
		try {
			orderList = RSBLL.getstance().getOrderBFGService().getOrderListBycondition("payid='"+payid+"'", 1, 99, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(null != orderList && !orderList.isEmpty()){
			StringBuffer sbSellids = new StringBuffer();
			for(OrderBFGEntity orderE : orderList){
				if(sbSellids.length() == 0 ){
					sbSellids.append(orderE.getSellerid());
				}else{
					sbSellids.append(",").append(orderE.getSellerid());
				}
			}
			//下单
			OrderBuzForHunter.obfhunter.orderPlace(sbSellids.toString(), Long.valueOf(userid), orderchannel);
		}
	}
}
