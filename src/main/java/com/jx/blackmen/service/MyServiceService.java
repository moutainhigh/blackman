package com.jx.blackmen.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.jx.argo.Model;
import com.jx.blackface.gaea.sell.entity.LvzEnterpriseEntity;
import com.jx.blackface.gaea.sell.entity.LvzPersonEntity;
import com.jx.blackface.gaea.sell.entity.LvzProviderServiceEntity;
import com.jx.blackface.gaea.sell.entity.LvzSellProductEntity;
import com.jx.blackface.gaea.usercenter.entity.BFAreasEntity;
import com.jx.blackface.gaea.vendor.entity.VendorServeEntity;
import com.jx.blackface.servicecoreclient.entity.OrderBFGEntity;
import com.jx.blackface.servicecoreclient.entity.OrderFollowupBFGEntity;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.utils.TimeUtils;

/***
 * 我的服务，服务
 * @author duxiaofei
 * @date   2016年4月13日
 */
public class MyServiceService {
	public static MyServiceService myservice = new MyServiceService();
	
	

	
	private static final int serviceState_9 = 9;  //服务中
	private static final int serviceState_10 = 10;  //服务中
	private static final int providerFlag_1 = 1;  //企业服务商
	private static final int providerFlag_2 = 2;  //个人服务商
	/**
	 * 查看服务初始化页面
	 */
	public void showServiceInit(Model model,Long orderid){

		OrderBFGEntity orderBFGEntity = null;
		try {
			orderBFGEntity = RSBLL.getstance().getOrderBFGService().loadOrderBFGEntityByid(orderid);
			if(null != orderBFGEntity){
				Map<String,Object> orderMap = new HashMap<String, Object>();
				orderMap = BeanUtils.describe(orderBFGEntity);

				LvzSellProductEntity sellProductE = RSBLL.getstance().getLvzSellProductService().getSellProductEntityById(orderBFGEntity.getSellerid());

				orderMap.put("booktimeValue", TimeUtils.longToString(orderBFGEntity.getBooktime())); //购买时间;
				long areaid = 0l;
				if(null != sellProductE){
					orderMap.put("productName", sellProductE.getSell_product_name());  //商品名称
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
				
				//获取服务商服务信息根据用户id和订单id 只查询服务中与服务完成
				List<VendorServeEntity> vendorServeList = RSBLL.getstance().getVendorServeService().getVendorServeListBycondition("(status='9' or status='10') and userid='"+orderBFGEntity.getUserid()+"' and orderid='"+orderBFGEntity.getOrderid()+"'", 1, 1, null);
				long vendorid=0L;
				if(null != vendorServeList && !vendorServeList.isEmpty()){
					VendorServeEntity vendorServeEntity = vendorServeList.get(0);  //获取服务商服务记录
					if(null != vendorServeEntity){
						vendorid=vendorServeEntity.getVendorid();
						//获取服务商电话
						LvzProviderServiceEntity providerEntity = RSBLL.getstance().getLvzProviderService().getEntityById(vendorServeEntity.getVendorid());
						if(null != providerEntity){
							//企业服务商查询企业服务商服务，个人服务商查询个人服务商服务
							if(providerEntity.getProvider_flag() == providerFlag_1){
								LvzEnterpriseEntity lvzEnterpriseEntity = RSBLL.getstance().getLvzEnterpriseService().getLvzEnterpriseEntityByPid(providerEntity.getProvider_id());
								if(null != lvzEnterpriseEntity){
									orderMap.put("phonenumber", lvzEnterpriseEntity.getContactPhone());
								}
							}else if(providerEntity.getProvider_flag() == providerFlag_2){
								LvzPersonEntity lvzPersonEntity = RSBLL.getstance().getLvzPersonService().getLvzPersonEntityByPid(providerEntity.getProvider_id());
								if(null != lvzPersonEntity){
									orderMap.put("phonenumber", lvzPersonEntity.getPersonPhone());
								}
							}
						}
						
						//获取跟进服务
						if(vendorServeEntity.getStatus() == serviceState_9){
							orderMap.put("statusValue", "服务中");
							orderMap.put("startBeiZhuTime", TimeUtils.DateToStr(vendorServeEntity.getStarttime()));  //服务备注开始服务时间
						}else if(vendorServeEntity.getStatus() == serviceState_10){
							orderMap.put("statusValue", "服务完成");
							orderMap.put("endBeiZhuTime", TimeUtils.DateToStr(vendorServeEntity.getEndtime()));  //服务备注结束服务时间
							orderMap.put("startBeiZhuTime", TimeUtils.DateToStr(vendorServeEntity.getStarttime()));  //服务备注开始服务时间
						}
					}
				}
				//获取跟进服务
				List<OrderFollowupBFGEntity> followList = RSBLL.getstance().getFollowService().getFollowListbypage("followtype='2' and order_id='"+orderBFGEntity.getOrderid()+"' and vendor_id='"+vendorid+"'", 1, 99, "followtime desc");
				if(null != followList && !followList.isEmpty()){
					List<Map<String,Object>> followMapList = new ArrayList<Map<String,Object>>();
					for(OrderFollowupBFGEntity follow : followList){
						Map<String,Object> followMap = BeanUtils.describe(follow);
						followMap.put("followtimeValue", TimeUtils.longToString(follow.getFollowtime()));  //备注时间
						followMapList.add(followMap);
					}
					if(null != followMapList && followMapList.size() > 0){
						model.add("followMapList", followMapList);  //备注记录
					}
				}
				model.add("orderMap", orderMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
