package com.jx.blackmen.controllers.m;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.jx.argo.ActionResult;
import com.jx.argo.annotations.Path;
import com.jx.blackface.gaea.sell.entity.LvzProductEntity;
import com.jx.blackface.gaea.sell.entity.LvzProductTitleEntity;
import com.jx.blackface.gaea.sell.entity.LvzSellProductEntity;
import com.jx.blackface.gaea.usercenter.entity.BFAreasEntity;
import com.jx.blackface.gaea.usercenter.entity.LvzAddressAreaorderEntity;
import com.jx.blackface.gaea.usercenter.entity.LvzAddressConfEntity;
import com.jx.blackmen.common.MemcachedUtil;
import com.jx.blackmen.controllers.BaseController;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.service.PackageService;
import com.jx.blackmen.vo.AddressConfVO;
import com.jx.blackmen.vo.AddressListVO;
import com.jx.service.preferential.plug.buz.PreferentialMatchBuz;
import com.jx.service.preferential.plug.vo.PacketVO;

/**
 * //虚拟地址-新-M
 * @author zhangyang
 *
 */
public class MVirtualController extends BaseController {
	
	public static final String CACHED_PACKAGE = "package_";

	/**
	 * 跳转--虚拟地址列表
	 * @param serviceid
	 * @return
	 */
	@Path("/m/address/list")
	public ActionResult list(){
		
		//查询一些筛选记录
		try {
			//从缓存取
			Object obj = MemcachedUtil.get("newaddress_");
			if(obj!=null){
				List<AddressListVO> listvo  = (List<AddressListVO>) obj;
				if(listvo!=null && CollectionUtils.isNotEmpty(listvo)){
					model().add("listvo", listvo);
				}
			}else{
				
				//查询排序列表
				int count = RSBLL.getstance().getlvzAddressOrdersByService().getCount("");
				int pagecount = 0;
				int pagesize = 99;
				if(count > 0){
					pagecount = count%pagesize == 0?count/pagesize:count/pagesize+1;
				}
				
				List<LvzAddressAreaorderEntity> pxlist = new ArrayList<LvzAddressAreaorderEntity>();
				if(pagecount>=1){
					
					for (int i = 1; i <= pagecount; i++) {
						List<LvzAddressAreaorderEntity> pxlist_ = RSBLL.getstance().getlvzAddressOrdersByService().getLvzAddressAreaorderEntity("", i, pagesize, " ordersby ");
						
						pxlist.addAll(pxlist_);
					}
				}
				
				
				//组装排序列表的数据
				List<AddressListVO> listvo  = new ArrayList<AddressListVO>();
				if(CollectionUtils.isNotEmpty(pxlist)){
					for (int i = 0; i < pxlist.size(); i++) {
						LvzAddressAreaorderEntity px = pxlist.get(i);
						if(px!=null){
							long cityid = px.getCityid();
							long areaid = px.getAreaid();
							
							String condition = " cityid="+cityid+" and areaid="+areaid;
							List<LvzAddressConfEntity> dzlist = RSBLL.getstance().getlvzAddressConfService().getLvzAddressConfEntity(condition, 1, 99, " ordersby ");
							if(CollectionUtils.isNotEmpty(dzlist)){
								AddressListVO vo = new AddressListVO();
								//把排序的属性设置到vo
								vo.setArea_name(px.getArea_name());
								vo.setAreaid(areaid);
								vo.setAreaorder_id(px.getAreaorder_id());
								vo.setCity_name(px.getCity_name());
								vo.setCityid(cityid);
								vo.setOrdersby(px.getOrdersby());
								
								//把dz-list转成dz-vo存到vo里
								List<AddressConfVO> dzvo = list2VO(dzlist);
								vo.setConfvo(dzvo);
								
								listvo.add(vo);
							}
						}
					}
				}
				
				if(CollectionUtils.isNotEmpty(listvo)){
					model().add("listvo", listvo);
					
					MemcachedUtil.set("newaddress_",listvo, new Date(1000 * 3600));
				}
			}
			
			
			


			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//转到的页面路径
		
		return view("/m/address/mvirlist");
		
	}
	
	/**
	 * 跳转--虚拟地址详情
	 * @param serviceid
	 * @return
	 */
	@Path("/m/address/detail/{id:\\d+}.html")
	public ActionResult detail(long id){
		
		try {
			

			//查询实体
			LvzAddressConfEntity conf = RSBLL.getstance().getlvzAddressConfService().getLvzAddressConfEntityById(id);
			if(conf!=null){
				
				//查询经营范围
				List<LvzProductTitleEntity> seltags = new ArrayList<LvzProductTitleEntity>();
				String strs = conf.getRun_scope();
				if(StringUtils.isNotEmpty(strs)){
					
					String[] str = strs.split(",");
					for (int j = 0; j < str.length; j++) {
						Long tid = Long.valueOf(str[j]);
						LvzProductTitleEntity tag = RSBLL.getstance().getLvzProductTitleService().loadProductByid(tid);
						if(tag!=null){
							seltags.add(tag);
						}
					}
				}
				model().add("seltags", seltags);
				model().add("conf", conf);
				
				
				
				
				//查询详情页的相关信息
				long sellid = conf.getAddress_id();
				long cityid = conf.getCityid();
				long areaid = conf.getAreaid();
				long pid = 0;
				String catcode = "";
				LvzSellProductEntity sellM = RSBLL.getstance().getLvzSellProductService().getSellProductEntityById(sellid);
				if(sellM!=null){
					 pid = sellM.getProduct_id();
					 
					 model().add("sellM", sellM);
				}
				
				
				model().add("areaid", areaid);
				model().add("cityid", cityid);
				model().add("productid", pid);
				
				LvzProductEntity pm = RSBLL.getstance().getLvzProductService().getProductEntityById(pid);
				if(pm!=null){
					catcode = pm.getParent_cate_code();
				}

					/*****获取商品包信息start*****/
					int packageCount = PackageService.getInstance().getPackageSellCountByProductAndCityidAndareaid(String.valueOf(pid), String.valueOf(cityid), String.valueOf(areaid));
					if(packageCount > 0 ){
						model().add("packageCount", packageCount);
					}
					/*****获取商品包信息end*****/
			
					//获取推荐的优惠券
					List<PacketVO> packetList = null;
					Object obj9 = MemcachedUtil.get("yhq_"+pid+"_"+cityid+"_"+areaid);
					if(obj9 !=null){
						packetList = (List<PacketVO>) obj9;
					}
					if(CollectionUtils.isNotEmpty(packetList)){
						//从缓存取出
						model().add("packets",packetList);
						System.out.println("yhq_"+pid+"_"+cityid+"_"+areaid+"-----yhqinfo-----finded.....");
					}else{
						
						packetList = PreferentialMatchBuz.getInstance().matchPacket(0,catcode,areaid+"",sellM==null?0:(double)sellM.getSell_overprice(), 2);
						if (!CollectionUtils.isEmpty(packetList)) {
							model().add("packets",packetList);
							
							MemcachedUtil.set("yhq_"+pid+"_"+cityid+"_"+areaid, packetList, new Date(1000 * 3600));
						}
					}
					
					
			}
				
			
			
			


			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//转到的页面路径
		
		return view("/m/address/mvir");
		
	}
	
	
	/**
	 * 跳转--更多页面
	 * @param serviceid
	 * @return
	 */
	@Path("/m/address/more/{id:\\d+}")
	public ActionResult dt(long id){
		
		try {
			
			LvzAddressConfEntity conf = RSBLL.getstance().getlvzAddressConfService().getLvzAddressConfEntityById(id);
			if(conf !=null){
				model().add("conf", conf);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//转到的页面路径
		
		return view("/m/address/more");
		
	}
	
	
	/**
	 * @param list
	 * @throws Exception
	 */
	public List<AddressConfVO> list2VO(List<LvzAddressConfEntity> list) throws Exception {
		 List<AddressConfVO>  listvo = new ArrayList<AddressConfVO>();
		 if(CollectionUtils.isNotEmpty(list)){
			 for (int i = 0; i < list.size(); i++) {
					LvzAddressConfEntity m = list.get(i);
					long areaid = m.getAreaid();
					long cityid = m.getCityid();
					BFAreasEntity area = RSBLL.getstance().getCityService().getAeasEntityById(areaid);
					BFAreasEntity city = RSBLL.getstance().getCityService().getAeasEntityById(cityid);
					AddressConfVO vo =  new AddressConfVO();
					vo.setAddress_conf_id(m.getAddress_conf_id());
					vo.setAddress_desc(m.getAddress_desc());
					vo.setAddress_id(m.getAddress_id());
					vo.setAddress_name(m.getAddress_name());
					vo.setAreaid(areaid);
					if(area!=null){
						vo.setAreaname(area.getName());
					}
					vo.setCityid(cityid);
					if(city!=null){
						vo.setCityname(city.getName());
					}
					vo.setCom_type(m.getCom_type());
					vo.setDescpation(m.getDescpation());
					vo.setInvoice(m.getInvoice());
					vo.setM_fwbz(m.getM_fwbz());
					vo.setOrdersby(m.getOrdersby());
					vo.setPc_fwbz(m.getPc_fwbz());
					vo.setPc_shbz(m.getPc_shbz());
					vo.setPic_link(m.getPic_link());
					vo.setPic_url(m.getPic_url());
					vo.setReg_money(m.getReg_money());
					vo.setRun_scope(m.getRun_scope());
					vo.setSw_info(m.getSw_info());
					
					//查询sell相关字段
					Long sellid = m.getAddress_id();
					LvzSellProductEntity sellm = RSBLL.getstance().getLvzSellProductService().getSellProductEntityById(sellid);
					if(sellm!=null){
						vo.setMarket_price(  sellm.getSell_markprice());
						vo.setOver_price(sellm.getSell_overprice());
					}
					
					//查询标签
					List<LvzProductTitleEntity> tags = new ArrayList<LvzProductTitleEntity>();
					String strs = m.getRun_scope();
					if(StringUtils.isNotEmpty(strs)){
						
						String[] str = strs.split(",");
						for (int j = 0; j < str.length; j++) {
							Long tid = Long.valueOf(str[j]);
							LvzProductTitleEntity tag = RSBLL.getstance().getLvzProductTitleService().loadProductByid(tid);
							if(tag!=null){
								tags.add(tag);
							}
						}
					}
					vo.setTags(tags);
					
					listvo.add(vo);
				}
		 }
		
		
		return listvo;
	}
	
	


}
