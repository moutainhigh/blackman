package com.jx.blackmen.controllers.wx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.jx.argo.ActionResult;
import com.jx.argo.annotations.Path;
import com.jx.blackface.gaea.usercenter.entity.LvzPrizeEntity;
import com.jx.blackface.gaea.usercenter.entity.LvzPrizeRecordEntity;
import com.jx.blackmen.controllers.BaseController;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.utils.ActionResultUtils;
import com.jx.blackmen.utils.TimeUtils;
import com.jx.blackmen.utils.WXUtils;

/***
 * 抽奖类控制层
 * @author bruce
 * @date   2016-06-20
 * 
 */
@Path("/prize")
public class WXPrizeController extends BaseController{
	
//实现思路：
//假设有10000个元素，那么有【10000*0.02 = 200个（1）】【10000*0.25*0.01=25个（2）】【10000*0=0个（3）】【10000*0.01=100个（4）】
//	                   【10000*0.4=4000个（5）】【10000*0.5*0.01=50个（6）】【10000*0.34=3400个（7）】【10000*0.2225=2225个（8）】
//	中奖概率2%	    1   手机充值卡5元
//	中奖该概率0.25%	2   手机充值卡50元
//	中奖概率0%	    3   谢谢参与
//	中奖概率1%	    4	手机充值卡10元
//	中奖概率40%	    5   泰笛鲜花5元代金券    28%【10000*0.28=2800】
//	中奖概率0.5%	    6   手机充值卡20元
//	中奖概率34%	    7	回家吃饭50元优惠券   70% 【10000*0.7=7000】
//	中奖概率22.25%	8	再来一次			  2% 【10000*0.02=200】	

	//=============================以下为业务方法======================================
	
	/**
	 * 跳转文章页面
	 * @return
	 */
	@Path("/article.html")
	public ActionResult article(){
		String rs = "/wx/prize/article2";
			
		ActionResult buildWXUrl = buildWXUrl();
		if(buildWXUrl != null){
			return buildWXUrl;
		}
			    
		
		return view(rs);
	}
	
	/**
	 * 跳转抽奖页面
	 * @return
	 */
	@Path("/cj")
	public ActionResult cj(){
		String rs = "/wx/prize/cj2";
		
		String openId = beat().getRequest().getParameter("openId");
		
		if(StringUtils.isNotEmpty(openId)){
			model().add("openId", openId);
		}
		
		return view(rs);
	}
	
	/**
	 * 查看奖品页面
	 * @return
	 */
	@Path("/view")
	public ActionResult view(){
		String rs = "/wx/prize/prize";
		
		ActionResult buildWXUrl = buildWXUrl();
		if(buildWXUrl != null){
			return buildWXUrl;
		}
		
		try{
			
			String openId = (String) model().get("openId");
			
			String prizeid = beat().getRequest().getParameter("prizeid");
			
			String condition = " openid='"+openId+"' ";
			if(StringUtils.isNotBlank(prizeid)){
				condition += " and prizeid="+prizeid;
			}
			List<LvzPrizeRecordEntity> list = RSBLL.getstance().getPrizeRecordService().getLvzPrizeRecordEntity(condition, 1, 99, "adddate desc");
			if(list!=null){
				if(list.size()>0){
					LvzPrizeRecordEntity pr = list.get(0);
				    long prizeid_ = pr.getPrizeid();
				    LvzPrizeEntity model = RSBLL.getstance().getPrizeService().getLvzPrizeEntityById(prizeid_);
				    
				    model().add("model", model);
				    if(model.getType()==1){//充值卡
				    	rs = "/wx/prize/prize";
				    }else if(model.getType()==2){//回家吃饭
				    	rs="/wx/prize/prize2";
				    }else if(model.getType()==3){//泰迪鲜花
				    	rs="/wx/prize/prize1";
				    }
					
				}
			}
			//根据条件来查
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return view(rs);
	}
	
	/**
	 * 异步获取抽奖返回值
	 * @name addItem
	 * @param nodeid
	 * @return
	 */
	@Path("/getNo")
	public ActionResult getNo(){
		String rs = "0";
		Map map = new HashMap<String, Object>();
		try {
			map.put("todayed", "no");
			map.put("again", "no");
			map.put("fans", "no");
			String openId = beat().getRequest().getParameter("openId");
			
			//========获取用户是否关注===================================
			
			Map<String, Object> wxUser = WXUtils.getWXUserBy(openId);
			if(wxUser.isEmpty()){
				map.put("nouser", "yes"); //非法访问(防止伪造openId访问)
			}
			if("0".equals(wxUser.get("subscribe"))){
				map.put("fans", "no");  //未关注登录
			}else{
				map.put("fans", "yes");  //未关注登录
			}
			
			
			
			
			//========获取编号，并且抽奖===================================
			
			
			int remain = 0;
			
			do {
				ArrayList<Integer> list = initData();
				Integer index = getRandomOne(list);
				
				int prizeno = list.get(index);
				
				rs = prizeno+""; 
				
				remain = RSBLL.getstance().getPrizeService().getCount(" prizeno="+rs+" and status=0 ");
				
			} while (remain==0);
			
			System.out.println("remain--"+remain);
			
			map.put("no", rs);
			map.put("prizeid", "0");
			
			
			if(!rs.equals("8")){//不是再来一次，就插入一条记录
				List<LvzPrizeEntity> prizeList = RSBLL.getstance().getPrizeService().getLvzPrizeEntity(" prizeno="+rs+" and status=0 ", 1, 99, "");
				if(prizeList!=null){
					if(prizeList.size()>0){
						LvzPrizeEntity prizeModel = prizeList.get(0);
						//判断插入条件
						int count = RSBLL.getstance().getPrizeRecordService().getCount(" openid='"+openId+"' and adddate='"+TimeUtils.nowdate()+"' ");
						if(count<=0){
							//插入抽奖记录
							long prizeid = prizeModel.getPrizeid();
							map.put("prizeid", prizeid);
							LvzPrizeRecordEntity pr = new LvzPrizeRecordEntity();
							pr.setOpenid(openId);
							pr.setPrizeid(prizeid);
							pr.setAdddate(TimeUtils.nowdate());
							RSBLL.getstance().getPrizeRecordService().addLvzPrizeRecordEntity(pr);
							
							//更新奖品的状态
							if(!rs.equals("5") && !rs.equals("7")){//回家吃饭和鲜花不限量
								prizeModel.setStatus(1);
								RSBLL.getstance().getPrizeService().updateLvzPrizeEntity(prizeModel);
							}
						}else{
							map.put("todayed", "yes");//今日已抽奖
						}
						
					}
				}
			}else{
				map.put("again", "yes");//再抽一次
			}
			
			    
			    
			    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			rs = "0";
			map.put("ex", "yes");
			e.printStackTrace();
		}
		
		
		
		return ActionResultUtils.renderJson(JSON.toJSONString(map));
	}
	
	
	/**
	 * 异步获取今天是否抽过奖
	 * @name addItem
	 * @param nodeid
	 * @return
	 */
	@Path("/todayflag")
	public ActionResult todayflag(){
		Map map = new HashMap<String, Object>();
		try {
			map.put("todayed", "no");
			String openId = beat().getRequest().getParameter("openId");
			
			//判断插入条件
			int count = RSBLL.getstance().getPrizeRecordService().getCount(" openid='"+openId+"' and adddate='"+TimeUtils.nowdate()+"' ");
			if(count<=0){
				map.put("todayed", "no");
			}else{
				map.put("todayed", "yes");//今日已抽奖
			}
			
				    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
		
		
		
		return ActionResultUtils.renderJson(JSON.toJSONString(map));
	}
	
	
	

	
	//=============================以下为工具方法======================================
	
	/**
	 * @desc 随机取出一个数【size 为  10 ，取得类似0-9的区间数】
	 * @return
	 */
	public static Integer getRandomOne(List<?> list){
		
		
		Random ramdom =  new Random();
		int number = -1;
		int max = list.size();
		
		//size 为  10 ，取得类似0-9的区间数
		number = Math.abs(ramdom.nextInt() % max  );
		
		return number;
    
	}
	
	/**
	 * 初始化数据
	 * @return 
	 */
	public static ArrayList<Integer> initData() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		
//		for (int i = 0; i < 200; i++) {
//			list.add(1);
//		}
//		for (int i = 0; i < 25; i++) {
//			list.add(2);
//		}
//		for (int i = 0; i < 100; i++) {
//			list.add(4);
//		}
		for (int i = 0; i < 2800; i++) {
			list.add(5);
		}
//		for (int i = 0; i < 50; i++) {
//			list.add(6);
//		}
		for (int i = 0; i < 7000; i++) {
			list.add(7);
		}
		for (int i = 0; i < 200; i++) {
			list.add(8);
		}
		
		return list;
	}
	
	
	
	public static void main(String[] args) {
		
		ArrayList<Integer> list = initData();
		Integer index = getRandomOne(list);
		
		int prizeno = list.get(index);
		
		System.out.println(prizeno);
		
		
	}
}

