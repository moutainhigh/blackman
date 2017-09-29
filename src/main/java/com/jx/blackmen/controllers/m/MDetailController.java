package com.jx.blackmen.controllers.m;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.jx.argo.ActionResult;
import com.jx.argo.annotations.Path;
import com.jx.blackface.gaea.sell.entity.LvzCusEvaluationEntity;
import com.jx.blackface.gaea.sell.entity.LvzProductCateEntity;
import com.jx.blackface.gaea.sell.entity.LvzProductEntity;
import com.jx.blackface.gaea.sell.entity.LvzProductInfoEntity;
import com.jx.blackface.gaea.sell.entity.LvzQAinfoEntity;
import com.jx.blackface.gaea.sell.entity.LvzSellProductEntity;
import com.jx.blackface.gaea.usercenter.entity.BFAreasEntity;
import com.jx.blackface.gaea.usercenter.utils.ActionResultUtils;
import com.jx.blackmen.common.DataFormat;
import com.jx.blackmen.common.MemcachedUtil;
import com.jx.blackmen.controllers.BaseController;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.service.PackageService;
import com.jx.blackmen.utils.AddressUtils;
import com.jx.blackmen.utils.CommonUtils;
import com.jx.blackmen.utils.EntityUtils;
import com.jx.blackmen.utils.PropertiesUtils;
import com.jx.blackmen.utils.ReflectUtils;
import com.jx.service.preferential.plug.buz.PreferentialMatchBuz;
import com.jx.service.preferential.plug.utils.PreferentialUtils;
import com.jx.service.preferential.plug.vo.PacketVO;

@Path("/mdetail")
public class MDetailController extends BaseController{
	private static SerializeConfig mapping = new SerializeConfig(); static { 
        mapping.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss")); 
	} 
	
	public static final String CACHED_PRE = "proids_";
	public static final String CACHED_CAT_PRE = "proids_cat_";
	public static final String CACHED_PLIST = "plist_";
	public static final String CACHED_CITYLIST = "citylist_";
	public static final String CACHED_AREALIST = "arealist_";
	public static final String CACHED_PACKAGE = "package_";
	public static final String CACHED_SELL_MODEL = "sell_model_";
	public static final String CACHED_INFO = "info_";
	//表类别1一级分类id2二级分类id3商品id4定价条目id
	
	/**
	 * 根据商品id查询详情
	 * @param productid
	 * @return
	 */
	@Path("/{productid:\\d+}.html")
	public ActionResult detail(long productid){
		
		
		try {
			
			//添加某专题页选中某区域的映射
			String sel_zt = beat().getRequest().getParameter("sel_zt");
			if(StringUtils.isNotEmpty(sel_zt)){
				model().add("sel_zt", sel_zt);
			}
			
			//查询该商品对应的一级+2级菜单
			String p_code = "0";
			String c_code = "0";
			LvzProductEntity pm = RSBLL.getstance().getLvzProductService().getProductEntityById(productid);
			
			if(pm!=null){
				p_code = pm.getParent_cate_code();
				c_code = pm.getChild_cate_code();
				
				

				//虚拟地址--转跳
				if("GS-001003".equals(c_code)){
					//beat().getResponse().sendRedirect("http://www.lvzheng.com/addresslist.html");
					return redirect("http://m.lvzheng.com/m/address/list");
					//return ActionResultUtils.renderJson("");
				}
				
				
			}
			LvzProductCateEntity catModel = RSBLL.getstance().getLvzProductCateService().getProductCateEntityByCode(p_code);
			
			
			LvzProductCateEntity catModel2 = RSBLL.getstance().getLvzProductCateService().getProductCateEntityByCode(c_code);
			
			
			if(catModel!=null){
				model().add("catModel", catModel);
			}
			
			if(catModel2!=null){
				model().add("catModel2", catModel2);
			}
			
			//product实体
			model().add("pModel",pm);
			model().add("productcode", pm.getProduct_code());
			
			
			
			//cityid和areaid的集合++该区域商品id合集
			Set<Long> proids_cat = new HashSet<Long>();
			Set<Long> proids = new HashSet<Long>();
			Set<Long> cityids = new HashSet<Long>();
			Set<Long> areaids = new HashSet<Long>();
			ArrayList<BFAreasEntity> cityList = new ArrayList<BFAreasEntity>();
			ArrayList<BFAreasEntity> areaList = new ArrayList<BFAreasEntity>();
			
			long cityid = 1;
			long areaid = 0;
			
			//从IP获取城市
			
			long time1 = System.currentTimeMillis();
			String ip = AddressUtils.getIpAddr(beat());
			System.out.println();
      	    String cityreturn = AddressUtils.getAddressByIP(ip);
      	    System.out.println(" get ip address cost::---->"+(System.currentTimeMillis()-time1));
      	    
      	    if(StringUtils.isNotEmpty(cityreturn)){
      	    	
      	    	if(cityreturn.contains("深圳")){
      	    		cityid = 2;
      	    	}
      	    }
			//从参数获取城市
			String citystr = beat().getRequest().getParameter("cityid");
			if(StringUtils.isNotEmpty(citystr)){
				cityid  = Long.valueOf(citystr);
			}
			String areastr = beat().getRequest().getParameter("areaid");
			if(StringUtils.isNotEmpty(areastr)){
				areaid  = Long.valueOf(areastr);
				System.out.println("get parmter areaid is "+areaid);
			}
//			
//			
//			//根据cityid获取该城市下面的商品合集
			
			//把筛选过程放到缓存，时限1小时
			
			
			Object obj1 = MemcachedUtil.get(CACHED_PRE+productid+"_"+cityid);
			Object obj2 = MemcachedUtil.get(CACHED_CAT_PRE+productid+"_"+cityid);
			
			//2步保证从memcached取出的东西不为空
			if(obj1!=null && obj2!=null){
				
				proids = (Set<Long>) obj1;
				proids_cat = (Set<Long>) obj2;
				
			}
			//2步保证从memcached取出的东西不为空
			if(proids.size()>0 && proids_cat.size()>0){
				System.out.println(CACHED_PRE+productid+"finded....");
			}else{
				int pageSize = 99;
				String condition = " sell_upderdesc =  0 and city_id ="+cityid ; 
				int pcount  = RSBLL.getstance().getLvzSellProductService().getCountSellProductEntity(condition);
				
				int pagecount = pcount%pageSize == 0?pcount/pageSize:pcount/pageSize+1;
				
				
				List<LvzSellProductEntity> sell_list = new ArrayList<LvzSellProductEntity>();
				if(pagecount>0){
					
					for(int i= 1 ; i <= pagecount ; i++){
						
						List<LvzSellProductEntity> sell_list_br = RSBLL.getstance().getLvzSellProductService().getSellProductEntityList(condition, i, pageSize, "");
					
						sell_list.addAll(sell_list_br);
					}
				}
					
				
				if(CollectionUtils.isNotEmpty(sell_list)){
					for (int i = 0; i < sell_list.size(); i++) {
						proids.add(sell_list.get(i).getProduct_id());
					}
				}
//				
				
				//获取商品集合
				String child_code = pm==null?"0":pm.getChild_cate_code();
				
				
				//查询本分类下面的所有商品
				List<LvzProductEntity> list_cats = RSBLL.getstance().getLvzProductService().getProductEntityByChildcatecode(child_code);
				
				if(CollectionUtils.isNotEmpty(list_cats)){
					
					for (int i = 0; i < list_cats.size(); i++) {
						Long pid_cat = list_cats.get(i).getProduct_id();
						proids_cat.add(pid_cat);
					}
				}
				
				
				
				
				//添加缓存[pid+cid]
				MemcachedUtil.set(CACHED_PRE+productid+"_"+cityid, proids.size()>0?proids:null, new Date(1000 * 3600));
				MemcachedUtil.set(CACHED_CAT_PRE+productid+"_"+cityid, proids_cat.size()>0?proids_cat:null, new Date(1000 * 3600));
			}
			
			
			
			
			//判断这个城市下如果不包含这个商品ID，就更改一下
			ArrayList<Long> proidlist = new ArrayList<Long>(proids);
			if(!proids.contains(productid) && CollectionUtils.isNotEmpty(proidlist)){
				if(cityid==1){
					//全北京
					return redirect("http://m.lvzheng.com/mdetail/"+productid+".html?cityid="+888);
				}else if(cityid==2){//全深圳
					return redirect("http://m.lvzheng.com/mdetail/"+productid+".html?cityid="+777);
				}else if(cityid!=999){//全国
					return redirect("http://m.lvzheng.com/mdetail/"+productid+".html?cityid="+999);
				}
			}
			
			//查询城市下面商品列表
			List<LvzProductEntity> list_Product = new ArrayList<LvzProductEntity>();
			
			//从缓存取商品列表[pid+cid]
			Object obj3 = MemcachedUtil.get(CACHED_PLIST+productid+"_"+cityid);
			
			if(obj3!=null ){
				
				list_Product = (List<LvzProductEntity>) obj3;
			}
			if(list_Product.size()>0 ){
				System.out.println(CACHED_PLIST+productid+"finded....");
			}else{
				for (int i = 0; i < proidlist.size(); i++) {
					long pid =  proidlist.get(i);
					if(proids_cat.contains(pid)){
						
						LvzProductEntity pm2 = RSBLL.getstance().getLvzProductService().getProductEntityById(pid);
						if(pm2!=null){
							
							list_Product.add(pm2);
						}
					}
				}
				
				//添加缓存
				MemcachedUtil.set(CACHED_PLIST+productid+"_"+cityid, list_Product.size()>0?list_Product:null, new Date(1000 * 3600));
			}
			
			model().add("list", list_Product.size()==0?"":list_Product);
			
			
			/*****以下这块代码+缓存******/
			Object obj4 = MemcachedUtil.get(CACHED_CITYLIST+productid+"_"+cityid);
			Object obj5 = MemcachedUtil.get(CACHED_AREALIST+productid+"_"+cityid);
			
			//2步保证从memcached取出的东西不为空
			if(obj4!=null && obj5!=null){
				
				cityList = (ArrayList<BFAreasEntity>) obj4;
				areaList = (ArrayList<BFAreasEntity>) obj5;
				
			}
			
			//------add by liuft
			//根据cityid和productid获取区域集合
			List<LvzSellProductEntity> list3 = RSBLL.getstance().getLvzSellProductService().getSellProductEntityList(" sell_upderdesc =  0 and  product_id="+productid+" and city_id ="+cityid, 1, 99, "");
			
			//获取当前area集合
			if(CollectionUtils.isNotEmpty(list3)){
				for (int i = 0; i < list3.size(); i++) {
					System.out.println("add earid "+list3.get(i).getArea_id());
					areaids.add(list3.get(i).getArea_id());
				}
			}
			//------end by liuft
			//2步保证从memcached取出的东西不为空
			if(cityList.size()>0 && areaList.size()>0){
				System.out.println(CACHED_AREALIST+CACHED_CITYLIST+productid+"finded....");
				model().add("city", cityList);
				model().add("area", areaList);
			}else{
				//根据选中的产品id获取支持的城市和区域
				List<LvzSellProductEntity> list2 = RSBLL.getstance().getLvzSellProductService().getSellProductEntityList(" sell_upderdesc =  0 and  product_id="+productid+"  ", 1, 99, "");
				
				
				//获取所有city集合
				if(CollectionUtils.isNotEmpty(list2)){
					for (int i = 0; i < list2.size(); i++) {
						cityids.add(list2.get(i).getCity_id());
					}
				}
				
				//获取city详细信息
				getQYInfo(cityids, cityList);
				
				//根据cityid和productid获取区域集合
				//List<LvzSellProductEntity> list3 = RSBLL.getstance().getLvzSellProductService().getSellProductEntityList(" sell_upderdesc =  0 and  product_id="+productid+" and city_id ="+cityid, 1, 99, "");
				
				//获取当前area集合
//				if(CollectionUtils.isNotEmpty(list3)){
//					for (int i = 0; i < list3.size(); i++) {
//						System.out.println("add earid "+list3.get(i).getArea_id());
//						areaids.add(list3.get(i).getArea_id());
//					}
//				}
//				
				//获取当前area集合详细信息
				getQYInfo(areaids, areaList);
				
	
				
				
				//排序城市+区域
				EntityUtils.sortList(cityList, "areaid", true);
				EntityUtils.sortList(areaList, "areaid", true);
				model().add("city", cityList.size()==0?"":cityList);
				model().add("area", areaList.size()==0?"":areaList);
				
				MemcachedUtil.set(CACHED_CITYLIST+productid+"_"+cityid, cityList.size()>0?cityList:null, new Date(1000 * 3600));
				MemcachedUtil.set(CACHED_AREALIST+productid+"_"+cityid, areaList.size()>0?areaList:null, new Date(1000 * 3600));
			
			}
			
			
			/*****以上这块代码+缓存******/
			
			//判断传回来的区域有没有被包含在区域列表
			if(!areaids.contains(areaid)){
				System.out.println("dont got earid in areaids");
				areaid =  areaList.size()>0?areaList.get(0).getAreaid():0;
			}
			
			//判断专题页特殊的选中
			if(StringUtils.isNotEmpty(sel_zt)){
				areaid = Long.valueOf(sel_zt);
			}
			model().add("areaid", areaid);
			model().add("cityid", cityid);
			
			
			
			System.out.println("the fianl earid is "+areaid);
			
			//中文城市+区域
			BFAreasEntity a1 = RSBLL.getstance().getAreaService().getAeasEntityById(areaid);
			if(a1!=null){
				String areaname = a1.getName();
				model().add("areaname", areaname);
			}
			BFAreasEntity a2 = RSBLL.getstance().getAreaService().getAeasEntityById(cityid);
			if(a2!=null){
				String cityname = a2.getName();
				model().add("cityname", cityname);
			}
			
			
			model().add("productid", productid);
			
			
			//计算价格的单条记录
			
			Object obj6 = MemcachedUtil.get(CACHED_SELL_MODEL+productid+"_"+cityid+"_"+areaid);
			
			LvzSellProductEntity model = null;
			if(obj6!=null){
				model = (LvzSellProductEntity) obj6;
				if(model!=null){
					//从缓存取出
					System.out.println(CACHED_SELL_MODEL+productid+"_"+cityid+"_"+areaid+"-----finded.....");
				}else{
					model = RSBLL.getstance().getLvzSellProductService().getSellProductEntityList("  sell_upderdesc =  0 and product_id="+productid+" and city_id="+cityid+" and area_id="+areaid, 1, 99, "").size()>0?RSBLL.getstance().getLvzSellProductService().getSellProductEntityList(" sell_upderdesc =  0 and product_id="+productid+" and city_id="+cityid+" and area_id="+areaid, 1, 99, "").get(0):null;
					
					MemcachedUtil.set(CACHED_SELL_MODEL+productid+"_"+cityid+"_"+areaid, model==null?"":model, new Date(1000 * 3600));
				}
			}else{
				model = RSBLL.getstance().getLvzSellProductService().getSellProductEntityList("  sell_upderdesc =  0 and product_id="+productid+" and city_id="+cityid+" and area_id="+areaid, 1, 99, "").size()>0?RSBLL.getstance().getLvzSellProductService().getSellProductEntityList(" sell_upderdesc =  0 and product_id="+productid+" and city_id="+cityid+" and area_id="+areaid, 1, 99, "").get(0):null;
				
				MemcachedUtil.set(CACHED_SELL_MODEL+productid+"_"+cityid+"_"+areaid, model==null?"":model, new Date(1000 * 3600));
			}
			model().add("model", model==null?"":model);
			
			
			//商品相关信息查询
			LvzProductInfoEntity info = null ;
			List<String> img  = null;
			Object obj7 = MemcachedUtil.get(CACHED_INFO+productid+"_"+cityid+"_img");
			Object obj8 = MemcachedUtil.get(CACHED_INFO+productid+"_"+cityid+"_info");
			
			if(obj7!=null && obj8!=null){
				img = (List<String>) obj7;
				info =  (LvzProductInfoEntity) obj8;
				
			}
			if(CollectionUtils.isNotEmpty(img) && info!=null){
				//从缓存取出
				System.out.println(CACHED_INFO+productid+"_"+cityid+"_img+info-----finded.....");
				model().add("img", img);
				model().add("info", info);
			}else{
				info = RSBLL.getstance().getInfoService().getProductInfoEntityList(" info_id="+productid+" and cityid ="+cityid, 1, 1, "").size()>0?RSBLL.getstance().getInfoService().getProductInfoEntityList(" info_id="+productid+" and cityid ="+cityid, 1, 1, "").get(0):null;
				if(info!=null){
					img = new ArrayList<String>();
					String images = info.getImage_url();
					String[] str = images.split(";");
					for(String s:str){
						img.add(s);
					}
					model().add("img", img);
					model().add("info", info);
					MemcachedUtil.set(CACHED_INFO+productid+"_"+cityid+"_img", img.size()>0?img:null, new Date(1000 * 3600));
					MemcachedUtil.set(CACHED_INFO+productid+"_"+cityid+"_info", info==null?"":info, new Date(1000 * 3600));
				}
			}
			
			
			
			//登录使用的验证码
			CommonUtils.geneCode(beat());
			
			//获取推荐的优惠券
			List<PacketVO> packetList = null;
			Object obj9 = MemcachedUtil.get("yhq_"+productid+"_"+cityid+"_"+areaid);
			if(obj9 !=null){
				packetList = (List<PacketVO>) obj9;
			}
			if(CollectionUtils.isNotEmpty(packetList)){
				//从缓存取出
				model().add("packets",packetList);
				System.out.println("yhq_"+productid+"_"+cityid+"_"+areaid+"-----yhqinfo-----finded.....");
			}else{
				
				packetList = PreferentialMatchBuz.getInstance().matchPacket(0,pm.getProduct_code(),areaid+"",model==null?0:(double)model.getSell_overprice(), 2);
				if (!CollectionUtils.isEmpty(packetList)) {
					packetList = PreferentialUtils.reverseList(packetList);
					model().add("packets",packetList);
					
					MemcachedUtil.set("yhq_"+productid+"_"+cityid+"_"+areaid, packetList, new Date(1000 * 3600));
				}
			}
			
			/*****获取商品包信息start*****/
			int packageCount = PackageService.getInstance().getPackageSellCountByProductAndCityidAndareaid(String.valueOf(productid), String.valueOf(cityid), String.valueOf(areaid));
			if(packageCount > 0 ){
				model().add("packageCount", packageCount);
			}
			/*****获取商品包信息end*****/
			
			//相关服务
//			getRef();
			
			//获取优惠券
//			getPreferential();
			
			//常见问题
			/*long catid2 =  RSBLL.getstance().getArticleCateRelationService().getArticleCateidByProductCateId(catModel2.getCate_id());
			
			List<FAQEntity> faqlist = RSBLL.getstance().getFAQService().getFAQList("cateid2="+catid2+" and state=1", 1, 3, "");
			model().add("faqlist", faqlist);*/
			
			//通过商品id 城市id 及 问题类型（QAtype） 查询商品问答
			//从缓存获取
			List<LvzQAinfoEntity> QAlist = null;
			System.out.println("get key ---::>"+"faqlist_"+productid+"_"+cityid);
			Object obj11 = MemcachedUtil.get("faqlist_"+productid+"_"+cityid);
			if(obj11!=null){
				QAlist = (List<LvzQAinfoEntity>) obj11;
			}
			if(CollectionUtils.isNotEmpty(QAlist)){
				System.out.println("faqlist_"+productid+"_"+cityid+"-----FAQ-----finded.....");
				
			}else{
				
				String condition = " 1=1 ";
				if(productid != 0L){
					condition += " and product_id='" + productid + "'";
				}
				if(cityid != 0L){
					condition += " and city_id='" + cityid + "'";
				}
				//默认问题类型为1-全部问题
				condition += " and qa_type='1'";
				QAlist = RSBLL.getstance().getQAinfoService().getListByCondition(condition, 1, 3, "updatetime desc");
				System.out.println("put key ---::>"+"faqlist_"+productid+"_"+cityid);
				MemcachedUtil.set("faqlist_"+productid+"_"+cityid, CollectionUtils.isEmpty(QAlist)?null:QAlist, new Date(1000 * 3600));
				
				
			}
			model().add("faqlist", CollectionUtils.isEmpty(QAlist)?"":QAlist);
			
			/* 查询最新一条好评   放入model中 +缓存 */
			getGoodEvaluation(productid+"");
			
			
			//服务内容
			
			//转到的页面路径
			
			
			//活动弹窗
			CommonUtils.activeFlag(beat());
			//活动右侧的小图标
			CommonUtils.activeFloatFlag(beat());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return view("/m/product");
	}
	
	
	/**
	 * 获取最新一条好评  通过productId
	 */
	public void getGoodEvaluation(String productId){
		
		Object obj11 = MemcachedUtil.get("haoping_"+productId+"_goodEval");
		Object obj22 = MemcachedUtil.get("haoping_"+productId+"_countAllScope");
		Integer count = 0;
		LvzCusEvaluationEntity  m = null;
		if(obj11!=null && obj22!=null){
			m = (LvzCusEvaluationEntity) obj11;
			count = (Integer) obj22;
		}
		if(m!=null && count!=null){
			model().add("DataFormat", DataFormat.class);
			model().add("goodEval", m);
			model().add("countAllScope", count);
			System.out.println("haoping_"+productId+"_goodEval"+"-----finded----");
		}else{
			String condition = "1=1 and zhpf='5'";
			String countCondition = " 1=1 ";
			count = 0;
			if(StringUtils.isNotBlank(productId)){
				LvzProductEntity prdtEntity = null;
				try {
					prdtEntity = RSBLL.getstance().getLvzProductService().getProductEntityById(Long.parseLong(productId));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				LvzProductCateEntity prdtCateEntity = null;
				try {
					prdtCateEntity = RSBLL.getstance().getLvzProductCateService().getProductCateEntityByCode(prdtEntity.getChild_cate_code());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				condition +=  " and product_cate_id='" + prdtCateEntity.getCate_id() + "'";
				countCondition += " and product_cate_id='" + prdtCateEntity.getCate_id() + "'";
			}
			
			List<LvzCusEvaluationEntity> list = null;
			try {
				count = RSBLL.getstance().getLvzCusEvaluationService().getCountbyCondition(countCondition);
				list = RSBLL.getstance().getLvzCusEvaluationService().getListByCondition(condition, 1, 1, "datetime desc");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(list != null && list.size() > 0){
				model().add("DataFormat", DataFormat.class);
				model().add("goodEval", list.get(0));
				model().add("countAllScope", count);
				MemcachedUtil.set("haoping_"+productId+"_goodEval", CollectionUtils.isEmpty(list)?null:list.get(0), new Date(1000 * 3600));
				MemcachedUtil.set("haoping_"+productId+"_countAllScope", count, new Date(1000 * 3600));
				
			}
		}
		
		
	}
	
	
	@Path("getQAinfoList")
	public ActionResult getQAinfoList(){
		//通过商品id 城市id 及 问题类型（QAtype） 查询商品问答
		String productId = request().getParameter("productId");
		String cityId = request().getParameter("cityId");
		String QAtype = request().getParameter("QAtype");
		String condition = " 1=1 ";
		if(StringUtils.isNotBlank(productId)){
			condition += " and product_id='" + productId + "'";
		}
		if(StringUtils.isNotBlank(cityId)){
			condition += " and city_id='" + cityId + "'";
		}
		if(StringUtils.isNotBlank(QAtype)){
			condition += " and qa_type='"+ QAtype +"'";
		}
		
		List<LvzQAinfoEntity> QAlist = null;
		try {
			QAlist = RSBLL.getstance().getQAinfoService().getListByCondition(condition, 1, 3, "updatetime desc");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ActionResultUtils.renderText(JSONArray.toJSONString(QAlist, mapping));
	}


	
	/**
	 * 查询价格的方法
	 * 根据商品id、城市id、区域id查询价格并且异步返回
	 * @param pid|cityid|areaid
	 * @return
	 */
	@Path("/queryPrice")
	public ActionResult queryPrice(){
		
			String pid = beat().getRequest().getParameter("pid");
			String cityid = beat().getRequest().getParameter("cityid");
			String areaid = beat().getRequest().getParameter("areaid");
			float sellprice = 0 ;
			float marketprice = 0 ;
			long  sellid = 0 ;
			Map map = new HashMap<String, Object>();
			//cityid和areaid的集合
			Set<Long> cityids = new HashSet<Long>();
			Set<Long> areaids = new HashSet<Long>();
			
		
		try {
			//获取商品集合
			String child_code = RSBLL.getstance().getLvzProductService().getProductEntityById(Long.valueOf(pid))==null?"0":RSBLL.getstance().getLvzProductService().getProductEntityById(Long.valueOf(pid)).getChild_cate_code();
			List<LvzProductEntity> list_Product = RSBLL.getstance().getLvzProductService().getProductEntityByChildcatecode(child_code);
			
			
			List<LvzSellProductEntity> list = RSBLL.getstance().getLvzSellProductService().getSellProductEntityList(" sell_upderdesc =  0 and  product_id="+pid+"  ", 1, 99, " sell_overprice asc");
			
			
			//根据产品id填充相关的城市和区域不重复集合
			getSetIds(Long.valueOf(pid), cityids, areaids, list);
			
			if(!cityids.contains(cityid)){
			    cityid  = String.valueOf(list.size()>0?list.get(0).getCity_id():0) ;
			}
			
			if(!areaids.contains(areaid)){
				areaid = String.valueOf(list.size()>0?list.get(0).getArea_id():0);
			}
			
			List<LvzSellProductEntity> slist = RSBLL.getstance().getLvzSellProductService().getSellProductEntityList(" sell_upderdesc =  0 and product_id="+pid+" and city_id="+cityid+" and area_id="+areaid, 1, 99, "");
			
			if(slist!=null){
				if(slist.size()>0){
					 sellprice  = slist.get(0).getSell_overprice();
					 marketprice =  slist.get(0).getSell_markprice();
					 sellid = slist.get(0).getSell_id();
				}
			}
			
			map.put("sellprice", sellprice);
			map.put("marketprice", marketprice);
			map.put("sellid", sellid);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ActionResultUtils.renderJson(JSON.toJSONString(map));
	}

	
	/**
	 * 切换城市的方法
	 * 根据选的城市切换区域列表
	 * @param pid|cityid|areaid
	 * @return
	 */
	@Path("/queryCity")
	public ActionResult queryCity(){
		
			//获取参数
			String pid = beat().getRequest().getParameter("pid");
			String cityid = beat().getRequest().getParameter("cityid");
			
			//定义容器
			ArrayList<BFAreasEntity> areaList = new ArrayList<BFAreasEntity>();
			Set<Long> areaids = new HashSet<Long>();
			Map map = new HashMap<String, Object>();
		
		try {
			//根据cityid获取区域代码集合
			getAreasSetIds(Long.valueOf(pid), Long.valueOf(cityid), areaids);
			//根据区域代码集合获取区域详细信息集合
			getQYInfo(areaids, areaList);
			//区域排序
			ReflectUtils.sortList(areaList, "areaid", true);
			map.put("area", areaList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ActionResultUtils.renderJson(JSON.toJSONString(map));
	}


	/**
	 * 切换区域的方法
	 * 根据商品id、城市id、区域id查询价格并且异步返回
	 * @param pid|cityid|areaid
	 * @return
	 */
	@Path("/queryArea")
	public ActionResult queryArea(){
		
			String pid = beat().getRequest().getParameter("pid");
			String cityid = beat().getRequest().getParameter("cityid");
			String areaid = beat().getRequest().getParameter("areaid");
			float sellprice = 0 ;
			float marketprice = 0 ;
			long  sellid = 0 ;
			Map map = new HashMap<String, Object>();
		
		try {
			List<LvzSellProductEntity> slist = RSBLL.getstance().getLvzSellProductService().getSellProductEntityList(" sell_upderdesc =  0 and product_id="+pid+" and city_id="+cityid+" and area_id="+areaid, 1, 99, "");
			
			if(slist!=null){
				if(slist.size()>0){
					 sellprice  = slist.get(0).getSell_overprice();
					 marketprice =  slist.get(0).getSell_markprice();
					 sellid = slist.get(0).getSell_id();
				}
			}
			
			//中文城市+区域
			BFAreasEntity a1 = RSBLL.getstance().getAreaService().getAeasEntityById(Long.valueOf(areaid));
			if(a1!=null){
				String areaname = a1.getName();
				map.put("areaname", areaname);
			}
			BFAreasEntity a2 = RSBLL.getstance().getAreaService().getAeasEntityById(Long.valueOf(cityid));
			if(a2!=null){
				String cityname = a2.getName();
				map.put("cityname", cityname);
			}
			
			map.put("sellprice", sellprice);
			map.put("marketprice", marketprice);
			map.put("sellid", sellid);
			
			int packageCount = PackageService.getInstance().getPackageSellCountByProductAndCityidAndareaid(pid, cityid, areaid);
			map.put("packageCount", packageCount);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ActionResultUtils.renderJson(JSON.toJSONString(map));
	}


	
	//---------------extracted FUNCTIONS below-----------
	
	/**
	 * 
	 * 根据参数填充area区域的set集合
	 * @param productid
	 * @param cityid
	 * @param areaids
	 * @throws Exception
	 */
	public void getAreasSetIds(long productid, long cityid, Set<Long> areaids)
			throws Exception {
		List<LvzSellProductEntity> list2 = RSBLL.getstance().getLvzSellProductService().getSellProductEntityList("  sell_upderdesc =  0 and product_id="+productid+" and city_id="+cityid, 1, 99, "");
		areaids.clear();
		for(LvzSellProductEntity m:list2){
			areaids.add(m.getArea_id());
		}
	}
	/**
	 * 根据产品id填充相关的城市和区域不重复集合
	 * @param productid
	 * @param cityids
	 * @param areaids
	 * @param list
	 * @throws Exception
	 */
	public void getSetIds(long productid, Set<Long> cityids, Set<Long> areaids,
			List<LvzSellProductEntity> list) throws Exception {
		for(LvzSellProductEntity m:list){
			cityids.add(m.getCity_id());
		}
		long cityid_ = cityids.size()>0?new ArrayList<Long>(cityids).get(0):1; 
		//取得页面显示的区预集合
		List<LvzSellProductEntity> list_ = RSBLL.getstance().getLvzSellProductService().getSellProductEntityList("  sell_upderdesc =  0 and product_id="+productid+" and city_id="+cityid_, 1, 99, "");
		
		for(LvzSellProductEntity m:list_){
			areaids.add(m.getArea_id());
		}
	}

	/**
	 * 根据set集合获取填充城市区域的详细信息集合
	 * @param setids_
	 * @param setList
	 * @throws Exception
	 */
	public void getQYInfo(Set<Long> setids_, ArrayList<BFAreasEntity> setList)
			throws Exception {
		Iterator<Long> setids = setids_.iterator();
		
		while(setids.hasNext()){
			
			BFAreasEntity m = RSBLL.getstance().getCityService().getAeasEntityById(setids.next());
			if(m!=null){
				setList.add(m);
			}
		}
	}
	
	
	/**
	 * 相关服务
	 */
	public void getRef() {
		//相关服务
		List<Map<String, String>> reflist = new ArrayList<Map<String,String>>();
		for (int i = 1; i <= 3; i++) {
			Map refMap = new HashMap<String, String>();
			String name  = PropertiesUtils.getProp("ref"+i+".name");
			String tip   = PropertiesUtils.getProp("ref"+i+".tip");
			String price = PropertiesUtils.getProp("ref"+i+".price");
			String path  = PropertiesUtils.getProp("ref"+i+".path");
			refMap.put("name",name );
			refMap.put("tip",tip  ); 
			refMap.put("price",price); 
			refMap.put("path",path ); 
			reflist.add(refMap);
		}
		
		model().add("reflist", reflist);
	}


}
