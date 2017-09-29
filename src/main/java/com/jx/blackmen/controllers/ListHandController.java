package com.jx.blackmen.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jsoup.helper.StringUtil;

import com.jx.argo.ActionResult;
import com.jx.argo.annotations.Path;
import com.jx.blackface.gaea.sell.entity.LvzProductCateEntity;
import com.jx.blackface.gaea.sell.entity.LvzProductEntity;
import com.jx.blackface.gaea.sell.entity.LvzSellProductEntity;
import com.jx.blackface.gaea.usercenter.entity.LvzMenuConfEntity;
import com.jx.blackface.gaea.usercenter.entity.LvzMenuEntity;
import com.jx.blackface.gaea.usercenter.entity.LvzProductConfEntity;
import com.jx.blackmen.buz.IndexBuz;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.vo.IndexHandVo;
import com.jx.blackmen.vo.MenuConfVO;

public class ListHandController extends BaseController {

	@Path("/detail/{prodcutcode:\\S+}.html")
	public ActionResult productdeatil(String prodcutcode){
		
		
	
		LvzProductEntity lvp = null;
		try {
			lvp = RSBLL.getstance().getLvzProductService().getProductEntityByCode(prodcutcode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(lvp != null){
			String openid = getOpenid();
			if(StringUtils.isBlank(openid)){//m
				return redirect("/mdetail/"+lvp.getProduct_id()+".html");
			}else{//微信
				return redirect("/wxdetail/"+lvp.getProduct_id()+".html");
			}
		}
		
		
		return view("/404");
	}
	/**
	 * 初始化页面
	 * @return
	 */
	//@Path("/")
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
	//@Path("/m/index")
	public ActionResult myIndex() throws Exception{
			//tab
			model().add("seltab", "home");
		
			//banner
			List<IndexHandVo> bannerlist = IndexBuz.commbuz.getbannerlist();
			if(bannerlist != null){
				model().add("bannerlist", bannerlist);
			}
			
			List<IndexHandVo> hotlist = IndexBuz.commbuz.getConfigVolist("hotlist");
			if(hotlist != null){
				model().add("productlist",hotlist);
			}
			
		
		return view("list/index");
	}
	@Path("/productlist/{catecode:\\S+}.html")
		public ActionResult togetproductlistpage(String catecode){
		
		try {
			
			System.out.println("catecode--in---"+catecode);
			//筛选二级菜单
			Set<Long> set = new HashSet<Long>();
			List<LvzMenuConfEntity> l2 = new ArrayList<LvzMenuConfEntity>();
			List<LvzMenuConfEntity> l1 = RSBLL.getstance().getlvzMenuConfService().getLvzMenuConfEntity(" shelf=1 ", 1, 99, " second_order ");
			if(l1!=null){
				if(l1.size()>0){
					for (int i = 0; i < l1.size(); i++) {
						if(set.add(l1.get(i).getSecond_menu_id())){
							l2.add(l1.get(i));
						}else{
							continue;
						}
					}
				}
			}
			
			
			//获取二级菜单和二级菜单的商品列表数据拼装
			List<MenuConfVO> volist = convertList2VO(l2);
			
			model().add("volist", volist);
			
			
			//基本路径
			String path ="/mdetail/";
			//获取openid
			String openid = getOpenid();
			if(!StringUtil.isBlank(openid)){
				model().add("openid", openid);
				path = "/wxdetail/";
			}
			
			
			List<String> set2 = new ArrayList<String>();
			
			
			//填充catcode
			if(volist.size()>0){
				for (int i = 0; i < volist.size(); i++) {
					MenuConfVO vo = volist.get(i);
					set2.add(vo.getMenu_code2());
					
				}
			}
			//如果没有catcode，设置为第一个
			if(!set2.contains(catecode)){
				catecode = set2.get(0);
			}
			
			if(volist.size()>0){
				for (int i = 0; i < volist.size(); i++) {
					MenuConfVO vo = volist.get(i);
					System.out.println("menucode2--->"+vo.getMenu_code2());
					//获取当前选中二级菜单下的商品列表
					if(StringUtils.isNotEmpty(vo.getMenu_code2())){
						
						if(catecode.equals(vo.getMenu_code2())){
							System.out.println("===========");
							List<LvzProductConfEntity> prolist = vo.getProlist();
							if(null != prolist){
								if(prolist.size()>0){
									for (int j = 0; j < prolist.size(); j++) {
										LvzProductConfEntity pp = prolist.get(j);
										if(3!=(pp.getPathtype())){
											pp.setPath(path+pp.getPath()+".html");
										}
									}
								}
								model().add("prolist", prolist);
							}
						}
					}
					
					
				}
			}
			
			

			
			model().add("modelcode", StringUtils.isNotEmpty(catecode)?catecode:"");
			System.out.println("catecode--set---"+catecode);
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return view("list/sp-list");
	}
	
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public class Listtool{
		public float getMinprice(long productid){
			float miniprice = 0;
			String condition = "product_id="+productid+" and sell_upderdesc=0";
			List<LvzSellProductEntity> splist = null;
			try {
				splist = RSBLL.getstance().getLvzSellProductService().getSellProductEntityList(condition, 1, 1, "sell_standprice");//(Long.parseLong(productid));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(null != splist && splist.size() > 0){
				miniprice = splist.get(0).getSell_standprice();
			}
			return miniprice;
		}
	}
	/**
	 * @param list
	 * @throws Exception
	 */
	public List<MenuConfVO> convertList2VO(List<LvzMenuConfEntity> list) throws Exception {
		List<MenuConfVO> volist = new ArrayList<MenuConfVO>();

		if(list!=null){
			if(list.size()>0){

				for (int i = 0; i < list.size(); i++) {
					MenuConfVO vo  = new MenuConfVO();
					LvzMenuConfEntity menuconf = list.get(i);
					long  menuid1 =    menuconf.getFirst_menu_id();
					long  menuid2 =  menuconf.getSecond_menu_id();
					long  menuconfid =   menuconf.getMenu_conf_id();
					Integer flag = menuconf.getFlag();
					Integer order1 = menuconf.getFirst_order();
					Integer order2 = menuconf.getSecond_order();
					Integer sxj = menuconf.getShelf();
					LvzMenuEntity menu1 = RSBLL.getstance().getlvzMenuService().getLvzMenuEntityById(menuid1);
					LvzMenuEntity menu2 = RSBLL.getstance().getlvzMenuService().getLvzMenuEntityById(menuid2);
					vo.setFlag(flag);
					vo.setMenu_conf_id(menuconfid);
					vo.setMenu_id1(menuid1);
					vo.setMenu_id2(menuid2);
					if(menu1!=null){
						vo.setMenu_name1(menu1.getMenu_name());
						vo.setPathname1(menu1.getPathname());
						if(!(3==menu1.getType())){//商品页||列表页
							long pid = Long.valueOf(menu1.getPath());
							LvzProductEntity pm = RSBLL.getstance().getLvzProductService().getProductEntityById(pid);
							if(pm!=null){
								
								vo.setMenu_code1(pm.getProduct_code());
							}
						}
					}

					if(menu2!=null){

						vo.setMenu_name2(menu2.getMenu_name());
						vo.setPathname2(menu2.getPathname());
						if(1==menu2.getType()){      //商品页
							long pid = Long.valueOf(menu2.getPath());
							LvzProductEntity pm = RSBLL.getstance().getLvzProductService().getProductEntityById(pid);
							if(pm!=null){
								
								vo.setMenu_code2(pm.getProduct_code());
								vo.setPath("/productlist/"+pm.getProduct_code()+".html");
							}
						}else if(2==menu2.getType()){ // ||列表页
							long pid = Long.valueOf(menu2.getPath());
							LvzProductCateEntity pm = RSBLL.getstance().getLvzProductCateService().getProductCateEntityById(pid);
							if(pm!=null){
								
								vo.setMenu_code2(pm.getCate_code());
								vo.setPath("/productlist/"+pm.getCate_code()+".html");
							}
						}else{
							vo.setMenu_code2(menu2.getPath());
							vo.setPath(menu2.getPath());
						}
					}
					vo.setOrder1(order1);
					vo.setOrder2(order2);
					vo.setSxj(sxj);



					//查询商品列表
					List<LvzProductConfEntity> prolist = RSBLL.getstance().getlvzProductMenuService().getLvzProductConfEntity(" menu_conf_id = "+menuconfid+" and shelf=1 ", 1, 99, "orderindex");
					vo.setProlist(prolist);
					volist.add(vo);
				}
			}
		}
		return volist;
	}
}
