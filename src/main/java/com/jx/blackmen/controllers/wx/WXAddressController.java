package com.jx.blackmen.controllers.wx;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.jx.argo.ActionResult;
import com.jx.argo.annotations.Path;
import com.jx.blackface.gaea.sell.entity.LvzProductEntity;
import com.jx.blackface.gaea.sell.entity.LvzProductTitleEntity;
import com.jx.blackface.orderplug.buzs.OrderBuzForHunter;
import com.jx.blackmen.common.MemcachedUtil;
import com.jx.blackmen.controllers.BaseController;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.service.ProductTitleService;
import com.jx.blackmen.utils.CookieUtils;
import com.jx.blackmen.utils.TokenHandler;
import com.jx.blackmen.vo.ProductAddressVo;
@Path("/wx")
public class WXAddressController extends BaseController {

	@Path("/addresslist.html")
	public ActionResult addressList(){
		
		ActionResult buildWXUrl = buildWXUrl();
		if(buildWXUrl != null){
			return buildWXUrl;
		}
		String openId = model().get("openId").toString();
		model().add("url", "/wx/addresslist.html");
		int pn = 1;
		int pagesize = 10;
		int pagecount = 1;
		
		String ptitle = beat().getRequest().getParameter("ptitle");
		String price = beat().getRequest().getParameter("pricequery");
		String local = beat().getRequest().getParameter("local");
		String pageon = beat().getRequest().getParameter("pageno");
		
		Map<String,String> querymap = new HashMap<String,String>();
		
		String querycondition = "";//页面查询条件 
		String productcondition = "";//实际组装成得条件
		
		if(null != ptitle && !"".equals(ptitle)){
			querymap.put("ptitle", ptitle);
			model().add("ptitle", ptitle);
			querycondition += "ptitle="+ptitle;
			productcondition += "sell_title like '%"+ptitle+"%' and ";
		}
		
		if(null != price && !"".equals(price) && !"0".equals(price)){
			querymap.put("pricequery", price);
			model().add("pricequery", price);
			if(querycondition.equals("")){
				querycondition += "pricequery="+price;
			}else{
				querycondition += "&pricequery="+price;
			}
			
			String[] prarray = price.split("-");
			if(prarray.length == 2){
				productcondition += " sell_overprice > "+prarray[0]+" and sell_overprice < "+prarray[1]+" and ";
			}else if(prarray.length == 1){
				productcondition += " sell_overprice > "+prarray[0]+" and ";
			}
			
		}
		if(null != local && !"".equals(local) && !"0".equals(local)){
			querymap.put("local", local);
			model().add("localval", local);
			if("".equals(querycondition)){
				querycondition += "localid="+local;
			}else{
				querycondition += "&localid="+local;
			}
			productcondition += "area_id="+local+" and ";
		}
		if(null != pageon && !pageon.equals("")){
			//querymap.put("pageno", pageon);
			pn = Integer.parseInt(pageon);
		}
		
		getParmUrl(querymap,"ptitle");
		getParmUrl(querymap,"local");
		getParmUrl(querymap,"pricequery");
		//getParmUrl(querymap,"pageno");
		
		queryTitlelist();
		
		Object obj1 = MemcachedUtil.get("m_address_"+ptitle+"_"+price+"_"+local+"_"+pageon+"_list");
		Object obj2 = MemcachedUtil.get("m_address_"+ptitle+"_"+price+"_"+local+"_"+pageon+"_pn");
		Object obj3 = MemcachedUtil.get("m_address_"+ptitle+"_"+price+"_"+local+"_"+pageon+"_pagecount");
		Object obj4 = MemcachedUtil.get("m_address_"+ptitle+"_"+price+"_"+local+"_"+pageon+"_condition");
		
		List<ProductAddressVo> addlist  = null;
		if(obj1!=null ){
			
			addlist = (List<ProductAddressVo>) obj1;
			if(obj2!=null){
				
				pn = (Integer) obj2;
			}
			if(obj3!=null){
				
				pagecount = (Integer) obj3;
			}
			if(obj4!=null){
				
				querycondition = (String) obj4;
			}
			
		}
		if(CollectionUtils.isNotEmpty(addlist)){
			beat().getModel().add("productlist", addlist);
			System.out.println("m_address_"+ptitle+"_"+price+"_"+local+"_"+pageon+"_list");
		}else{
		
		
				List<LvzProductEntity> lvz = null;
				try {
					lvz = RSBLL.getstance().getLvzProductService().getProductEntityByChildcatecode("GS-001003");//.getProductEntityByCode("");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(lvz != null && lvz.size() > 0){
					productcondition += "(";
					for(LvzProductEntity pe : lvz){
						productcondition += "product_id="+pe.getProduct_id()+" or ";
					}
					if(productcondition.length() > 3){
						productcondition = productcondition.substring(0, productcondition.length() - 3)+")";
					}
					if("".equals(productcondition)){
						productcondition = "sell_upderdesc = 0";
					}else{
						productcondition += " and sell_upderdesc = 0";
					}
					try {
						System.out.println("production condition is "+productcondition);
						int productcount = RSBLL.getstance().getLvzSellProductService().getCountSellProductEntity(productcondition);
						if(productcount > 0){
							pagecount = productcount%pagesize == 0?productcount/pagesize:productcount/pagesize+1;
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					List<ProductAddressVo> prolist = null;
					try {
						prolist = ProductTitleService.stance.getProductlistBycondition(productcondition, pn, pagesize, " sell_overprice ");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(prolist != null && prolist.size() > 0){
						beat().getModel().add("productlist", prolist);
						MemcachedUtil.set("m_address_"+ptitle+"_"+price+"_"+local+"_"+pageon+"_list", prolist);
						MemcachedUtil.set("m_address_"+ptitle+"_"+price+"_"+local+"_"+pageon+"_pn", pn);
						MemcachedUtil.set("m_address_"+ptitle+"_"+price+"_"+local+"_"+pageon+"_pagecount", pagecount);
						MemcachedUtil.set("m_address_"+ptitle+"_"+price+"_"+local+"_"+pageon+"_condition", querycondition);
					
					}
				}
		}
		model().add("pagenumber", pn);
		model().add("pagecount", pagecount);
		model().add("querycondition", querycondition);
		
		//防止多次提交
		String tokenid = TokenHandler.generateToken();
		beat().getRequest().getSession().setAttribute("tokenid", tokenid);
		//保存客户的OPENId
//		try {
//			CookieUtils.saveCookie(CookieUtils.userOpenId, openId, response());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		//-------end
		return view("/wx/addm");
	}
	private void queryTitlelist(){
		List<LvzProductTitleEntity> list = null;
		String condition = "producate='GS-001003'";
		try {
			list = ProductTitleService.stance.getProductTitlebycondition(condition, 1, 99, "titleid");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list != null){
			beat().getModel().add("titlelist", list);
		}
		
		
	}
	@Path("/tobuyaddress/{sellids:\\S+}")
	public ActionResult tobuyAddress(String sellids){
		ActionResult buildWXUrl = buildWXUrl();
		if(buildWXUrl != null){
			return buildWXUrl;
		}
		long userid =  1 ;//CommonUtils.getLoginuserid(beat());
		if(userid <= 0){
			return redirect("/login.html?red_path=/addresslist.html");
		}else{
			Map<String, Object> orderPlace = OrderBuzForHunter.obfhunter.orderPlace(sellids, userid, 1);
			if(orderPlace.containsKey("after") && !"".equals(orderPlace) && !"0".equals(orderPlace)){
				return redirect("http://pay.lvzheng.com/reqpay/"+orderPlace.get("after"));
			}
		}
		
		return null;
	}
	private String getParmUrl(Map<String,String> map,String keystr){
		String url = "?";
		String pageno = "?";
		if(map.size() > 0){
			Set<String> keyset = map.keySet();
			Iterator<String> it = keyset.iterator();
			while(it.hasNext()){
				String keys = it.next();
				pageno = pageno + keys+"="+map.get(keys)+"&";
				if(!keys.equals(keystr)){
					url = url + keys+"="+map.get(keys)+"&";
				}
			}
		}
		pageno = pageno+"pageno=";
		model().add("pagenourl", pageno);
		url += keystr+"=";
		beat().getModel().add(keystr+"url", url);
		return url;
	}
//	public static void main(String[] args){
//		String condition = "1=1";
//		int pcount = 0;
//		try {
//			pcount = RSBLL.getstance().getLvzSellProductService().getCountSellProductEntity(condition);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		if(pcount > 0){
//			int pagecount = pcount%100 == 0?pcount/100:(pcount/100)+1;
//			for(int i =1 ;i<= pagecount;i++){
//				List<LvzSellProductEntity> list = null;
//				try {
//					list = RSBLL.getstance().getLvzSellProductService().getSellProductEntityList(condition, i, 100, "sell_id");
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				if(null != list && list.size() > 0){
//					for(LvzSellProductEntity lv : list){
//						if(null != lv.getSell_imgurl() && !"".equals(lv.getSell_imgurl()) && !"null".equalsIgnoreCase(lv.getSell_imgurl())){
//							String url = lv.getSell_imgurl();
//							System.out.println("url is "+url);
//							if(url.split("/").length > 2){
//								String[] arry = url.split("/");
//								String[] fid = arry[arry.length - 1].split("\\?");
//								System.out.println("fid is "+fid[0]);
//								lv.setSell_imgurl(fid[0]);
//								try {
//									RSBLL.getstance().getLvzSellProductService().updateSellProductEntity(lv);
//								} catch (Exception e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//	}
}
