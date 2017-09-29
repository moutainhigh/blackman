package com.jx.blackmen.controllers.wx;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.helper.StringUtil;

import com.jx.argo.ActionResult;
import com.jx.argo.annotations.Path;
import com.jx.blackface.gaea.sell.entity.LvzProductCateEntity;
import com.jx.blackface.gaea.sell.entity.LvzProductEntity;
import com.jx.blackface.gaea.sell.entity.LvzProductInfoEntity;
import com.jx.blackface.gaea.sell.entity.LvzSellProductEntity;
import com.jx.blackmen.buz.IndexBuz;
import com.jx.blackmen.common.MemcachedUtil;
import com.jx.blackmen.controllers.BaseController;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.utils.CommonUtils;
import com.jx.blackmen.utils.PropertiesUtils;
import com.jx.blackmen.vo.IndexHandVo;

public class WXIndexController extends BaseController{
	
	
	
	/**
	 * 首页
	 * @return
	 * @throws Exception
	 */
	@Path("/wx/index")
	public ActionResult myIndex() throws Exception{
			
			//tab
			model().add("seltab", "home");
	
		
			ActionResult buildWXUrl = buildWXUrl();
			if(buildWXUrl != null){
				return buildWXUrl;
			}
		
			/**
			 * 
			 
			//热门单品
			List<Map<String, String>> hotlist = new ArrayList<Map<String,String>>();
			for (int i = 1; i <= 4; i++) {
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
				hotMap.put("path",path.replace("duan", "wx") ); 
				hotlist.add(hotMap);
			}
			
			model().add("hotlist", hotlist);
			
			//设立公司
			
			List<Map<String, Object>> setlist = loadConf("setcp");
			model().add("setlist", setlist);
			
			//经营公司
			List<Map<String, Object>> oplist =  loadConf("operatecp");
			model().add("oplist", oplist);
			
			*/
			
			//--------new begin-----
			//banner
			List<IndexHandVo> bannerlist = IndexBuz.commbuz.getWXbannerlist();
			if(bannerlist != null){
				model().add("bannerlist", bannerlist);
			}
			
			List<IndexHandVo> hotlist = IndexBuz.commbuz.getConfigVolist("hotlist");
			if(hotlist != null){
				model().add("productlist",hotlist);
			}
		//	model().add("openid", getOpenid());
			//--------new end-----
			//活动弹窗
			CommonUtils.activeFlag(beat());
			//活动右侧的小图标
			CommonUtils.activeFloatFlag(beat());
		return view("/wx/index");
	}

	/**
	 * @param key
	 * @return List集合信息
	 * @throws Exception
	 * @throws NumberFormatException
	 * @从配置文件获取List集合信息
	 */
	public List<Map<String, Object>> loadConf(String key)  {
		String mKey = "loadConf_wx_" + key;
		Object object = MemcachedUtil.get(mKey);
		List<Map<String, Object>> setlist = new ArrayList<Map<String,Object>>();
		if(object != null && object instanceof List){
//			System.out.println("get mKey = " + mKey);
			setlist = (List<Map<String, Object>>)object;
		}
		if(setlist == null || setlist.isEmpty()){
			System.out.println("setlist is null. mKey = " + mKey);
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
			if(setlist != null && !setlist.isEmpty()){
				System.out.println("put mKey = " + mKey);
				MemcachedUtil.set(mKey, setlist, new Date(24 * 60 * 60 * 1000));
			}
		}

		return setlist;
	}

	
	/**
	 * 专题页
	 * @return
	 */
	@Path("/wx/sem/suit.html")
	public ActionResult suit(){
		//获取传递的URL参数openid
		String openId = getOpenid();
		if(!StringUtil.isBlank(openId)){
			model().add("openId", openId);
		}
		return view("/wx/suitm");
	}
	
	/**
	 * 年报专题页
	 */
	@Path("/wx/report.html")
	public ActionResult report(){
		//获取传递的URL参数openid
		String openId = getOpenid();
		if(!StringUtil.isBlank(openId)){
			model().add("openId", openId);
		}
		CommonUtils.getTimer(beat());
		return view("/wx/reportm");
	}
	
	/**
	 * 1元法律咨询专题页
	 */
	@Path("/wx/law.html")
	public ActionResult law(){
		//获取传递的URL参数openid
		String openId = getOpenid();
		if(!StringUtil.isBlank(openId)){
			model().add("openId", openId);
		}
		CommonUtils.getTimer(beat());
		return view("/wx/lawm");
	}
	
	//公司注册落地页
	@Path("/wx/ldy/companyRegLdy.html")
	public ActionResult companyRegLdy(){
		String openId = getOpenid();
		if(!StringUtil.isBlank(openId)){
			model().add("openId", openId);
		}
		//转到的页面路径
		return view("/wx/companyRegLdy");
	}
	
	//扶持计划
    //@date 2016-08-08
	@Path("/wxsupportpj.html")
	public ActionResult support(){
		
		ActionResult buildWXUrl = buildWXUrl();
		if(buildWXUrl != null){
			return buildWXUrl;
		}

		return view("/wx/supportpj/msupportpj");
	}
	
	@Path("/wx/ldy/midautumn.html")
	public ActionResult midautumn(){
		//转到的页面路径
		return view("/wx/ldy/mid_land");
	}
	
}
