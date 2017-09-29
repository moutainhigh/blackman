package com.jx.blackmen.controllers.m;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jx.argo.ActionResult;
import com.jx.argo.Model;
import com.jx.argo.annotations.Path;
import com.jx.blackface.gaea.usercenter.entity.BFLoginEntity;
import com.jx.blackmen.common.CommonUtils;
import com.jx.blackmen.controllers.BaseController;
import com.jx.blackmen.frame.RSBLL;
import com.jx.service.preferential.entity.PreferentialAccountEntity;
import com.jx.service.preferential.plug.buz.PreferentialAccountBuz;
import com.jx.service.preferential.plug.vo.AccountVO;

/***
 * m的用户中心
 * @author duxiaofei
 * @date   2016年4月11日
 */
@Path("/m/usercenter")
public class MUserCenterController extends BaseController{

	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	
	
	/**
	 * m端访问用户中心
	 * @return
	 */
	@Path("/index.html")
	public ActionResult myCenter(){
		
		//tab
		model().add("seltab", "center");
	
		Model model = beat().getModel();
		try {
			BFLoginEntity bfloginE = CommonUtils.getLoginEntity(beat());
			if(null != bfloginE){
				model.add("loginE", bfloginE);
				
				//查询优惠券数目
				long nums = PreferentialAccountBuz.getInstance().getUserCouponsCountByStatus(bfloginE.getUserid(), 0);
				model.add("nums", nums);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view("m/center");
	}
	
	/**
	 * m端优惠券列表
	 * @return
	 */
	@Path("/coupon.html")
	public ActionResult coupon(){
		
	
		Model model = beat().getModel();
		try {
			BFLoginEntity bfloginE = CommonUtils.getLoginEntity(beat());
			if(null != bfloginE){
				model.add("loginE", bfloginE);
				
				//查询优惠券
				long userid= bfloginE.getUserid();
//			    long userid= Long.valueOf("38346088915969") ;
			    List<AccountVO> unuselist = PreferentialAccountBuz.getInstance().getUserCouponsByStatus(userid, 0,1,99,"effect_date");
				if (!CollectionUtils.isEmpty(unuselist)) {
					model.add("unuselist", unuselist);
				}
				
				
				List<AccountVO> uselist = PreferentialAccountBuz.getInstance().getUserCouponsByStatus(userid, 2 , 1 , 99,"effect_date");				
				if (!CollectionUtils.isEmpty(uselist)) {
					model.add("uselist", uselist);
				}
				
				
				
				List<AccountVO> overlist = PreferentialAccountBuz.getInstance().getUserCouponsByStatus(userid, -1 , 1 , 99,"effect_date");
				if (!CollectionUtils.isEmpty(overlist)) {
					model.add("overlist", overlist);
				}
				
				model.add("simpleDateFormat", simpleDateFormat);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view("m/coupon");
	}
	
	
	
	
}
