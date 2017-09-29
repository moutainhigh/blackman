package com.jx.blackmen.service;

import java.util.ArrayList;
import java.util.List;

import com.jx.blackface.gaea.sell.contract.ILvzProdcutTitleService;
import com.jx.blackface.gaea.sell.contract.ILvzSellProductService;
import com.jx.blackface.gaea.sell.entity.LvzProductTitleEntity;
import com.jx.blackface.gaea.sell.entity.LvzSellProductEntity;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.vo.ProductAddressVo;

public class ProductTitleService {
	public static ProductTitleService stance = new ProductTitleService();
	private static ILvzProdcutTitleService pts = RSBLL.getstance().getLvzProductTitleService();
	private static ILvzSellProductService ps = RSBLL.getstance().getLvzSellProductService();
	public List<LvzProductTitleEntity> getProductTitlebycondition(String condition,int pageindex,int pagesize,String sortby)throws Exception{
		return pts.getProductTitlelistBycondition(condition, pageindex, pagesize, sortby);
	}
	public int getProductTitlecountBycondtion(String condition)throws Exception{
		return pts.getProductTitlecountBycondition(condition);
	}
	public List<ProductAddressVo> getProductlistBycondition(String condition,int pageindex,int pagesize,String sortby)throws Exception{
		List<ProductAddressVo> vlist = new ArrayList<ProductAddressVo>();
		
		List<LvzSellProductEntity> list = ps.getSellProductEntityList(condition, pageindex, pagesize, sortby);
		if(null != list && list.size() > 0){
			for(LvzSellProductEntity pe : list){
				ProductAddressVo vo = new ProductAddressVo();
				vo.setImgurl(pe.getSell_imgurl());
				vo.setMarketprice(pe.getSell_markprice());
				vo.setSell_address(pe.getSell_address());
				vo.setSellerid(pe.getSell_id());
				vo.setSellername(pe.getSell_product_name());
				vo.setSellerprice(pe.getSell_overprice());
				String titleids = pe.getSell_title();
				
				List<LvzProductTitleEntity> tlist = new ArrayList<LvzProductTitleEntity>();
				if(null != titleids && !"".equals(titleids)){
					String[] trray = titleids.split(",");
					for(String tid : trray){
						if("".equals(trray)){
							continue;
						}
						long ids = Long.parseLong(tid);
						tlist.add(pts.loadProductByid(ids));
					}
				}
				vo.setTitlelist(tlist);
				vlist.add(vo);
			}
		}
		return vlist;
	}
		
}
