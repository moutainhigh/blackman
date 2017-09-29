package com.jx.blackmen.weixin.event;

import com.jx.blackface.gaea.sell.entity.LvzProviderServiceEntity;
import com.jx.blackface.gaea.usercenter.entity.BFEmployersEntity;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.utils.MContents;
import com.jx.blackmen.weixin.handler.TextHandler;


public class ScanAction {

	private static ScanAction sa = null;
	public static ScanAction getstance(){
		if(sa == null)
			sa = new ScanAction();
		return sa;
	}
	public void dealScan(String openid,int scenid){
		System.out.println("come into dealscan");
		if(MContents.scend_id_map.containsKey(scenid)){//服务商绑定处理
			long uid = MContents.scend_id_map.get(scenid);
			System.out.println("get uid "+uid);
			if(uid > 0){
				try {//先判断uid是否是服务商
					LvzProviderServiceEntity vendor = RSBLL.getstance().getLvzProviderService().getEntityById(uid);
					if(vendor!=null){
						vendor.setOpenid(openid);
						RSBLL.getstance().getLvzProviderService().updateProviderServiceEntity(vendor);
						new TextHandler().sendTextMessage(openid, "微信绑定成功！");
					}else{//判断是否是员工id
						BFEmployersEntity ee = RSBLL.getstance().getEmployerService().getEmployersEntityById(uid);
						if(ee != null){
							ee.setOpenid(openid);
							RSBLL.getstance().getEmployerService().updateEmployersEntity(ee);
							new TextHandler().sendTextMessage(openid, "微信绑定成功！");
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
}
