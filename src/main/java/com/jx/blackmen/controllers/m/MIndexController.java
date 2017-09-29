package com.jx.blackmen.controllers.m;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jx.argo.ActionResult;
import com.jx.argo.annotations.Path;
import com.jx.blackface.gaea.sell.entity.LvzProductCateEntity;
import com.jx.blackface.gaea.sell.entity.LvzProductEntity;
import com.jx.blackface.gaea.sell.entity.LvzProductInfoEntity;
import com.jx.blackface.gaea.sell.entity.LvzSellProductEntity;
import com.jx.blackmen.buz.IndexBuz;
import com.jx.blackmen.controllers.BaseController;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.utils.ActionResultUtils;
import com.jx.blackmen.utils.CommonUtils;
import com.jx.blackmen.utils.DingdingUtils;
import com.jx.blackmen.utils.PropertiesUtils;
import com.jx.blackmen.utils.Timers;
import com.jx.blackmen.utils.WorkDayUtil;
import com.jx.blackmen.vo.IndexHandVo;

public class MIndexController extends BaseController{
	
	/**
	 * 初始化页面
	 * @return
	 */
	@Path("/")
	public ActionResult myInit(){
		try {
			return myIndex();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view("/404");
	}
	
	/**
	 * 首页
	 * @return
	 * @throws Exception
	 */
	@Path("/m/index")
	public ActionResult myIndex() throws Exception{
			//tab
			model().add("seltab", "home");
		/**
			//热门单品
			List<Map<String, String>> hotlist = new ArrayList<Map<String,String>>();
			for (int i = 1; i <= 4; i++) {
				if(i!=2){
					Map hotMap = new HashMap<String, String>();
					String name  = PropertiesUtils.getProp("hot"+i+".name");
					String tip   = PropertiesUtils.getProp("hot"+i+".tip");
					String price = PropertiesUtils.getProp("hot"+i+".price");
					String path  = PropertiesUtils.getProp("hot"+i+".path");
					int num = CommonUtils.getProductBaseNumberBySellid("233333");
					hotMap.put("num", num);
					hotMap.put("name",name );
					hotMap.put("tip",tip  ); 
					hotMap.put("price",price); 
					hotMap.put("path",path.replace("duan", "m") ); 
					hotlist.add(hotMap);
				}else{
					Map hotMap = new HashMap<String, String>();
					String name  = PropertiesUtils.getProp("hot"+i+".m.name");
					String tip   = PropertiesUtils.getProp("hot"+i+".m.tip");
					String price = PropertiesUtils.getProp("hot"+i+".m.price");
					String path  = PropertiesUtils.getProp("hot"+i+".m.path");
					int num = CommonUtils.getProductBaseNumberBySellid("233333");
					hotMap.put("num", num);
					hotMap.put("name",name );
					hotMap.put("tip",tip  ); 
					hotMap.put("price",price); 
					hotMap.put("path",path.replace("duan", "m") ); 
					hotlist.add(hotMap);
				}
			}
			
			model().add("hotlist", hotlist);
			
			//设立公司
			
			List<Map<String, Object>> setlist = loadConf("setcp");
			model().add("setlist", setlist);
			
			//经营公司
			List<Map<String, Object>> oplist =  loadConf("operatecp");
			model().add("oplist", oplist);
			
			*/
			//------new index-----begin
			//banner
			List<IndexHandVo> bannerlist = IndexBuz.commbuz.getMbannerlist();
			if(bannerlist != null){
				model().add("bannerlist", bannerlist);
			}
			
			List<IndexHandVo> hotlist = IndexBuz.commbuz.getConfigVolist("hotlist");
			if(hotlist != null){
				model().add("productlist",hotlist);
			}
			
		//活动弹窗
		CommonUtils.activeFlag(beat());
		//活动右侧的小图标
		CommonUtils.activeFloatFlag(beat());
			//------new index-----end
		return view("/m/index");
	}

	/**
	 * @param key
	 * @return List集合信息
	 * @throws Exception
	 * @throws NumberFormatException
	 * @从配置文件获取List集合信息
	 */
	public List<Map<String, Object>> loadConf(String key) {
		List<Map<String, Object>> setlist = new ArrayList<Map<String,Object>>();
		try{
			String val = PropertiesUtils.getProp(key);
			String[] str = val.split(";");
			
			for (int i = 0; i < str.length; i++) {
				Map<String, Object> setmap = new HashMap<String, Object>();
				String id = str[i];
				LvzProductEntity m = RSBLL.getstance().getLvzProductService().getProductEntityById(Long.valueOf(id));
				LvzProductInfoEntity info = RSBLL.getstance().getInfoService().getProductInfoEntityList(" info_id="+id, 1, 1, "").size()>0?RSBLL.getstance().getInfoService().getProductInfoEntityList(" info_id="+id, 1, 1, "").get(0):null;
				
				//获取最低的商品价格
				List<LvzSellProductEntity> list = RSBLL.getstance().getLvzSellProductService().getSellProductEntityList(" sell_upderdesc =  0 and product_id="+id, 1, 99, " sell_overprice asc");
				
				if(list!=null){
					if(list.size()>0){
						float price = list.get(0).getSell_overprice();
						setmap.put("pri", price);
					}
				}
				
				
				
				//商品实体
				if(m!=null){
					setmap.put("pModel", m);
				}
				
				//详情实体
				if(info!=null){
					setmap.put("info", info);
				}
				
				
				//查询该商品对应的一级+2级菜单
				String p_code = "0";
				String c_code = "0";
						LvzProductEntity  pm=		RSBLL.getstance().getLvzProductService().getProductEntityById(Long.parseLong(id));
				
				if(pm!=null){
					p_code = pm.getParent_cate_code();
					c_code = pm.getChild_cate_code();
				}
				
				LvzProductCateEntity catModel = RSBLL.getstance().getLvzProductCateService().getProductCateEntityByCode(p_code);
				
				if(catModel!=null){
					setmap.put("catModel", catModel);
				}
				
				LvzProductCateEntity catModel2 = RSBLL.getstance().getLvzProductCateService().getProductCateEntityByCode(c_code);
				
				if(catModel2!=null){
					setmap.put("catModel2", catModel2);
				}
				
				//获取购买人数nums
				int num = CommonUtils.getProductBaseNumberBySellid(id);
				setmap.put("num", num);
				
				setlist.add(setmap);
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		return setlist;
	}
	

	/**
	 * 专题页
	 * @return
	 */
	@Path("/sem/m/suit.html")
	public ActionResult suit(){

		return view("/m/suitm");
	}
	
	/**
	 * 年报专题页
	 */
	@Path("/m/report.html")
	public ActionResult report(){
		CommonUtils.getTimer(beat());
		return view("/m/reportm");
	}
	
	/**
	 * 1元法律咨询专题页
	 */
	@Path("/m/law.html")
	public ActionResult law(){
		CommonUtils.getTimer(beat());
		return view("/m/lawm");
	}
	
	//公司注册落地页
	@Path("/m/ldy/companyRegLdy.html")
	public ActionResult companyRegLdy(){
		//转到的页面路径
		return view("/m/companyRegLdy");
	}
	
	//海淀专题页
		@Path("/m/hdtc.html")
		public ActionResult hdtc(){
			//pid设置
			model().add("productid", "38229817543169");
			//日期推算
			try {
				
				Date date = WorkDayUtil.getDeadLineDate(new Date(), 25);
				String endday = Timers.parseDate2Day(date);
				model().add("endday", endday);
			} catch (Exception e) {
				// TODO: handle exception
			}
			//转到的页面路径
			return view("/m/mhdtc");
		}
		
		
		//海淀专题页
		@Path("/mhdtc.html")
		public ActionResult mhdtc(){
			//pid设置
			model().add("productid", "38229817543169");
			//日期推算
			try {
				
				Date date = WorkDayUtil.getDeadLineDate(new Date(), 25);
				String endday = Timers.parseDate2Day(date);
				model().add("endday", endday);
			} catch (Exception e) {
				// TODO: handle exception
			}
			//转到的页面路径
			return view("/m/mhdtc");
		}
		
		@Path("/m/hdtc2.html")
		public ActionResult mhdtc2(){
			//pid设置
			model().add("productid", "38229817543169");
			//日期推算
			try {
				
				Date date = WorkDayUtil.getDeadLineDate(new Date(), 25);
				String endday = Timers.parseDate2Day(date);
				model().add("endday", endday);
			} catch (Exception e) {
				// TODO: handle exception
			}
			//转到的页面路径
			return view("/m/mhdtc2");
		}
				

		//公司注册落地页 电话咨询 - 钉钉消息
		@Path("/m/ldy/sendDingDingMsg")
		public ActionResult sendDingDingMsg(){
			String phone = request().getParameter("phone");
			int type = request().getParameter("type") == null ? 0 : Integer.parseInt(request().getParameter("type"));
			
			String content = "";
			String dingdingCode = "";
			if (type == 1) {
				dingdingCode = "chatba99ba1ac9052105cc357fe4295be32b";
				content = "您有一个公司注册＋地址套餐新客户，电话号码为："+phone+",请尽快催单！";
			} else if (type == 2) {
				String msg = request().getParameter("msg");

				dingdingCode = "chat9ed9ffae5639b9c8d3b596418baaf97a";
				if (StringUtils.isNotBlank(msg)) {
					content = "您有一个商标查询新客户，电话号码为："+ phone + ",查询关键字为：“" + msg + "”。请尽快催单！";
				} else {
					content = "您有一个商标注册新客户，电话号码为："+phone+"，请尽快催单！";
				}
				
				
			} else {
				dingdingCode = "chatba99ba1ac9052105cc357fe4295be32b";
				content = "您有一个公司注册新客户，电话号码为："+ phone + ",请尽快催单！";
			} 
					
			boolean sendFlag = DingdingUtils.sendDingding(dingdingCode, "01555218071881", content);
			
			return ActionResultUtils.renderText("{\"ret\":\""+sendFlag+"\"}");
		}
				
				
				
		//扶持计划
	    //@date 2016-08-08
		@Path("/msupportpj.html")
		public ActionResult support(){
		 

			return view("/m/supportpj/msupportpj");
		}
				
		//套餐落地页
	    //@date 2016-08-08
		@Path("/m/ldy/tc.html")
		public ActionResult tcActive(){
		 

			return view("/m/tc/mland");
		}
		
		
		//套餐  ----  ck
	    //@date 2016-08-08
		@Path("/m/ldy/cktc.html")
		public ActionResult cktcActive(){
		 

			return view("/m/tc/mland_ck");
		}
		
		
		@Path("/m/ldy/shangbiao.html")
		public ActionResult sbActive(){
			
			//转到的页面路径
			return view("/m/ldy/sb_land");
		}
		
		@Path("/m/ldy/biangeng.html")
		public ActionResult bgActive(){
			
			//转到的页面路径
			return view("/m/ldy/bg_land");
		}
		
		
		//三证合一
		@Path("/m/merge.html")
		public ActionResult merge(){
			
			//转到的页面路径
			return view("/m/merge/merge");
		}
	
		@Path("/m/ldy/book.html")
		public ActionResult bookActive(){
			
			//转到的页面路径
			return view("/m/ldy/book_land");
		}
		
		@Path("/m/ldy/book_ck.html")
		public ActionResult bookCkActive(){
			
			//转到的页面路径
			return view("/m/ldy/book_land_ck");
		}
		
		@Path("/m/ldy/kezhang.html")
		public ActionResult kzActive(){
			
			//转到的页面路径
			return view("/m/ldy/kz_land");
		}
		@Path("/m/ldy/midautumn.html")
		public ActionResult midautumn(){
			//转到的页面路径
			return view("/m/ldy/mid_land");
		}
}
