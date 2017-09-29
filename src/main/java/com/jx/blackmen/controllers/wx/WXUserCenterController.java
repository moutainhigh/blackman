package com.jx.blackmen.controllers.wx;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.jx.argo.ActionResult;
import com.jx.argo.Model;
import com.jx.argo.annotations.Path;
import com.jx.blackface.gaea.usercenter.entity.BFLoginEntity;
import com.jx.blackmen.annotaion.WXCheckLogin;
import com.jx.blackmen.buz.WeiXinBuz;
import com.jx.blackmen.controllers.BaseController;
import com.jx.blackmen.frame.RSBLL;
import com.jx.service.preferential.entity.PreferentialAccountEntity;
import com.jx.service.preferential.plug.buz.PreferentialAccountBuz;
import com.jx.service.preferential.plug.vo.AccountVO;

/***
 * 微信的用户中心
 * @author duxiaofei
 * @date   2016年4月11日
 */
@Path("/wx/usercenter")
public class WXUserCenterController extends BaseController{
	
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	
	/**
	 * 微信端访问用户中心
	 * @return
	 */
	@Path("/index.html")
	public ActionResult myCenter(){
		//tab
		model().add("seltab", "center");
		ActionResult buildWXUrl = buildWXUrl();
		if(buildWXUrl != null){
			return buildWXUrl;
		}
		
		Model model = beat().getModel();
		try {
			String openId = model().get("openId").toString();
			model.add("openId", openId);
			BFLoginEntity bfloginE = WeiXinBuz.wb.getBFLoginEByopenId(openId);
			if(null != bfloginE){
				model.add("loginE", bfloginE);
				
				//查询优惠券数目
				//String wheresql = " userid= "+"38390039572225"+"  and status=0 ";
				String wheresql = " user_id= "+bfloginE.getUserid()+"  and status=0 ";
				long nums = PreferentialAccountBuz.getInstance().getUserCouponsCountByStatus(bfloginE.getUserid(), 0);
				model.add("nums", nums);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view("/wx/center");
	}
	
	
	/**
	 * wx端优惠券列表
	 * @return
	 */
	@WXCheckLogin
	@Path("/coupon.html")
	public ActionResult coupon(){
		
	
		Model model = beat().getModel();
		try {
			String openId = model().get("openId").toString();
				if(StringUtils.isNotBlank(openId)){
					model.add("openId", openId);
					BFLoginEntity bfloginE = WeiXinBuz.wb.getBFLoginEByopenId(openId);
					if(null != bfloginE){
						model.add("loginE", bfloginE);
						
						//查询优惠券
//						long userid= Long.valueOf("38346088915969") ;
						long userid= bfloginE.getUserid();
						
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
			
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view("wx/coupon");
	}
	
	
}
