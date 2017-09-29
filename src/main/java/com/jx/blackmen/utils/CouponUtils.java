package com.jx.blackmen.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jx.blackface.gaea.usercenter.entity.BFLoginEntity;
import com.jx.blackmen.frame.RSBLL;
import com.jx.service.newcore.entity.CouponsEntity;
import com.jx.service.newcore.entity.LoginEntity;
import com.jx.service.newcore.entity.UserCouponsEntity;

/**
 * 优惠券临时类
 * @author duxiaofei
 * @date   2016年3月2日
 */
public class CouponUtils {

	private static CouponUtils couponservice = null;
	
	public static CouponUtils getInstance(){
		if(null == couponservice){
			couponservice = new CouponUtils();
		}
		return couponservice;
	}

	/**
	 * 给用户添加一个优惠券[第二个参数传什么就添加什么优惠券]
	 * @param userE
	 * @param couponsid 优惠券的ID
	 * @param overTimer 到期时间
	 */
	public Long addUserCoupons(BFLoginEntity user,String couponsid,String overTimer){
		Long usercouponsId = 0l;
		if(null != user && StringUtils.isNotBlank(String.valueOf(user.getUserid()))){
			UserCouponsEntity usercoupon = new UserCouponsEntity();
			usercoupon.setUserid(user.getUserid());   								//用户id
			usercoupon.setCouponsid(Long.valueOf(couponsid));   			    	//优惠券ID
			usercoupon.setGettime(new Date().getTime());   							//得到优惠券的时间
			
			
			CouponsEntity couponsEntity;
			try {
				couponsEntity = RSBLL.getstance().getCouponsService().getCouponsById(Long.valueOf(couponsid));
				if(null != couponsEntity){
					usercoupon.setCtype(couponsEntity.getCtype());
					usercoupon.setClabel(couponsEntity.getClabel());
					usercoupon.setCmoney(couponsEntity.getMoney());
					usercoupon.setCname(couponsEntity.getCouponsname());
					usercoupon.setDqtime(Timers.getAddaferDate(new Date(new Date().getTime()), "month", 1).getTime()); //顺延一个月到期时间
					usercouponsId = RSBLL.getstance().getUserCouponsService().addUserCoupons(usercoupon);
				}
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return usercouponsId;
	}
	
	/**
	 * 发放优惠券
	 * @param user
	 */
	public static void sendCoupons(long userid) {
		if(userid == 0){
			System.out.println("未获取到用户id"+userid);
		}else{
			try {
				BFLoginEntity loginEntity = RSBLL.getstance().getLoginService().getLoginEntityById(userid);
				
				for(int i=1;i<=4;i++){
					CouponUtils.getInstance().addUserCoupons(loginEntity,"100011",null);
				}
				for(int i=1;i<=3;i++){
					CouponUtils.getInstance().addUserCoupons(loginEntity,"100012",null);
				}
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("webUserId", userid);
				RSBLL.getstance().getNoticeService().notice(30001L, map);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("发送优惠券失败!用户id:"+userid);
			}
		}
	}
}
