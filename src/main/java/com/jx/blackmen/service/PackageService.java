package com.jx.blackmen.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import com.jx.argo.Model;
import com.jx.blackface.gaea.sell.entity.LvzPackageEntity;
import com.jx.blackface.gaea.sell.entity.LvzPackageSellEntity;
import com.jx.blackface.gaea.sell.entity.LvzProductCateEntity;
import com.jx.blackface.gaea.sell.entity.LvzProductEntity;
import com.jx.blackface.gaea.sell.entity.LvzProductInfoEntity;
import com.jx.blackface.gaea.sell.entity.LvzSellProductEntity;
import com.jx.blackface.gaea.usercenter.entity.BFAreasEntity;
import com.jx.blackmen.frame.RSBLL;

/***
 * 商品包服务类
 * @author flower
 */
public class PackageService {
	
	private static PackageService packageService = null;
	public static PackageService getInstance(){
		if(packageService == null){
			packageService = new PackageService();
		}
		return packageService;
	}
	
	
	
	/******
	 * 根据Sellid获取包含此商品的商品包信息集合
	 * @param sellid
	 */
	public List<Map<String,Object>> getPackageSellInfoBySellid(long sellid){
		LvzSellProductEntity sellEntity = null;
		try {
			sellEntity = RSBLL.getstance().getLvzSellProductService().getSellProductEntityById(sellid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(null != sellEntity){
			List<Map<String,Object>> packageSellList = new ArrayList<Map<String,Object>>();
			try {
				List<LvzPackageSellEntity> packageSellListBySellid = RSBLL.getstance().getPackageSellService().getLvzPackageSellListBySellid(String.valueOf(sellEntity.getSell_id()));  //String.valueOf(sellEntity.getSell_id())
				if(null != packageSellListBySellid && !packageSellListBySellid.isEmpty()){
					for(LvzPackageSellEntity packageSellE : packageSellListBySellid){
						LvzPackageEntity lvzPackageEntity = RSBLL.getstance().getPackageService().getLvzPackageEntity(packageSellE.getPackage_id());
						//判断商品包是否为前台商品包
						if(lvzPackageEntity.getPackage_type() == 1){
							Map<String,Object> packageSellEMap = BeanUtils.describe(packageSellE);
							List<Map<String,String>> sellProductMapBySellids = getSellProductMapBySellids(packageSellE.getSellids());
							if(null != sellProductMapBySellids && !sellProductMapBySellids.isEmpty()){
								//获取sellids集合
								packageSellEMap.put(String.valueOf(packageSellE.getPackagesell_id()), sellProductMapBySellids);
							}
							packageSellList.add(packageSellEMap);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if( packageSellList.size() > 0 ){
				return packageSellList;
			}
		}
		return Collections.emptyList();
	}
	
	/*****
	 * 通过商品包中包含的sellids字段获取定价条目集合
	 * @param sellids
	 * @return
	 */
	public List<Map<String,String>> getSellProductMapBySellids(String sellids){
		if(StringUtils.isBlank(sellids)){
			return null;
		}
		List<Map<String,String>> sellList = new ArrayList<Map<String,String>>();
		//通过  , 号分割
		String[] idArr = sellids.split(",");
		for(int i=0;i<idArr.length;i++){
			Map<String,String> sellMap = new HashMap<String, String>();
			if(StringUtils.isBlank(idArr[i])){
				continue;
			}
			
			LvzSellProductEntity sellProductEntity = null;
			try {
				sellProductEntity = RSBLL.getstance().getLvzSellProductService().getSellProductEntityById(Long.parseLong(idArr[i]));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(sellProductEntity != null){
				//sellList.add(sellProductEntity);
				try {
					sellMap.putAll(BeanUtils.describe(sellProductEntity));
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					e1.printStackTrace();
				} catch (NoSuchMethodException e1) {
					e1.printStackTrace();
				}
				try {
					LvzProductEntity productEntityById = RSBLL.getstance().getLvzProductService().getProductEntityById(sellProductEntity.getProduct_id());
					if(null != productEntityById){
						LvzProductCateEntity productCateEntityByCode = RSBLL.getstance().getLvzProductCateService().getProductCateEntityByCode(productEntityById.getChild_cate_code());
						if(null != productCateEntityByCode){
							sellMap.put("cate_name", productCateEntityByCode.getCate_name());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//根据product获取info信息也就是商品介绍信息
				try {
					List<LvzProductInfoEntity> productInfolist = RSBLL.getstance().getInfoService().getProductInfoEntityList("typid='3' and info_id='"+sellProductEntity.getProduct_id()+"'", 1, 1, null);
					if(null != productInfolist && !productInfolist.isEmpty()){
						String tips = productInfolist.get(0).getTips();
						sellMap.put("tips", tips);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				sellList.add(sellMap);
			}
		}
		return sellList;
	}
	
	/*****
	 * 通过商品包中包含的sellids字段获取定价条目集合
	 * @param sellids
	 * @return
	 */
	public List<Map<String,String>> getPaySellProductMapBySellids(String sellids){
		if(StringUtils.isBlank(sellids)){
			return null;
		}
		List<Map<String,String>> sellList = new ArrayList<Map<String,String>>();
		//通过  , 号分割
		String[] idArr = sellids.split(",");
		for(int i=0;i<idArr.length;i++){
			Map<String,String> sellMap = new HashMap<String, String>();
			if(StringUtils.isBlank(idArr[i])){
				continue;
			}
			
			LvzSellProductEntity sellProductEntity = null;
			try {
				sellProductEntity = RSBLL.getstance().getLvzSellProductService().getSellProductEntityById(Long.parseLong(idArr[i]));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(sellProductEntity != null){
				try {
					sellMap.putAll(BeanUtils.describe(sellProductEntity));
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					e1.printStackTrace();
				} catch (NoSuchMethodException e1) {
					e1.printStackTrace();
				}
				try {
					BFAreasEntity aeasEntity = RSBLL.getstance().getAreaService().getAeasEntityById(sellProductEntity.getArea_id());
					if(aeasEntity != null){
						sellMap.put("aeasname", aeasEntity.getName());
						BFAreasEntity cityEntity = RSBLL.getstance().getAreaService().getAeasEntityById(Long.valueOf(aeasEntity.getParentid()));
						sellMap.put("cityname", cityEntity.getName());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				sellList.add(sellMap);
			}
		}
		return sellList;
	}
	
	
	/******
	 * 根据Sellid查询包含其的商品包数量
	 * @param sellid
	 */
	public int getPackageSellCountBySellid(long sellid){
		int count = 0;
		if(sellid != 0){
			String condition = "package_type='1' and package_state='1' and LOCATE('"+sellid+"',sellids)"; //查询条件
			try {
				count = RSBLL.getstance().getPackageSellService().getCountLvzPackageSell(condition);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return count;
	}
	
	/****
	 * 根据商品ID城市ID和区域ID获取商品包数量
	 * @return
	 */
	public int getPackageSellCountByProductAndCityidAndareaid(String productid,String cityid,String areaid){
		//通过前台穿来的商品ID和城市ID，区域ID查找商品定价条目
		LvzSellProductEntity sellEntity = null;
		try {
			List<LvzSellProductEntity> sellProductEntityList = RSBLL.getstance().getLvzSellProductService().getSellProductEntityList(" sell_upderdesc = '0' and product_id='"+productid+"' and city_id='"+cityid+"' and area_id='"+areaid+"'", 1, 1, "");
			if(sellProductEntityList != null && !sellProductEntityList.isEmpty()){
				sellEntity = sellProductEntityList.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(null != sellEntity){
			return getPackageSellCountBySellid(sellEntity.getSell_id());
		}
		return 0;
	}
	
	/***
	 * 根据商品ID城市ID区域ID获取商品包信息
	 * @param productid
	 * @param cityid
	 * @param areaid
	 */
	@SuppressWarnings("unchecked")
	public void getPackageSellInfoByProductidAndcityidAndareaid(Model model,String productid,String cityid,String areaid){
		List<LvzPackageSellEntity> packageSellList = null;
		Map<String,List<Map<String,String>>> sellProductMap = null;
		
		//通过前台穿来的商品ID和城市ID，区域ID查找商品定价条目
		LvzSellProductEntity sellEntity = null;
		try {
			List<LvzSellProductEntity> sellProductEntityList = RSBLL.getstance().getLvzSellProductService().getSellProductEntityList(" sell_upderdesc = '0' and product_id='"+productid+"' and city_id='"+cityid+"' and area_id='"+areaid+"'", 1, 1, "");
			if(sellProductEntityList != null && !sellProductEntityList.isEmpty()){
				sellEntity = sellProductEntityList.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(null != sellEntity){
			sellProductMap = new HashMap<String, List<Map<String,String>>>();
			packageSellList = new ArrayList<LvzPackageSellEntity>();
			try {
				List<LvzPackageSellEntity> packageSellListBySellid = RSBLL.getstance().getPackageSellService().getLvzPackageSellListBySellid(String.valueOf(sellEntity.getSell_id()));  //
				if(null != packageSellListBySellid && !packageSellListBySellid.isEmpty()){
					for(LvzPackageSellEntity packageSellE : packageSellListBySellid){
						LvzPackageEntity lvzPackageEntity = RSBLL.getstance().getPackageService().getLvzPackageEntity(packageSellE.getPackage_id());
						//判断商品包是否为前台商品包
						if(lvzPackageEntity.getPackage_type() == 1){
							packageSellList.add(packageSellE);
							List<Map<String,String>> sellProductMapBySellids = getSellProductMapBySellids(packageSellE.getSellids());
							if(null != sellProductMapBySellids && !sellProductMapBySellids.isEmpty()){
								//获取sellids集合
								sellProductMap.put(String.valueOf(packageSellE.getPackagesell_id()), sellProductMapBySellids);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(null != packageSellList && packageSellList.size() > 0 ){
				model.add("packageSellList", packageSellList);
				model.add("sellProductMap", sellProductMap);
			}
		}
	}
	
	
	
	/***
	 * 通过商品包IDs返回商品包和商品的拼接字符串 sellid|packageid
	 * @param packageSellids
	 * @return
	 */
	public String getPackageIdAndSellidStr(String packageSellids){
		if(StringUtils.isBlank(packageSellids)){
			return "";
		}
		String returnStr = "";
		String[] packageSplit = StringUtils.split(packageSellids,",");
		for(String packageStr : packageSplit){
			try {
				LvzPackageSellEntity lvzPackageSellEntity = RSBLL.getstance().getPackageSellService().getLvzPackageSellEntity(Long.valueOf(packageStr));
				if(null != lvzPackageSellEntity){
					String sellids = lvzPackageSellEntity.getSellids();
					String[] sellSplit = StringUtils.split(sellids, ",");
					for(String sellStr : sellSplit){
						returnStr += sellStr+"|"+packageStr+",";
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(StringUtils.isNotBlank(returnStr)){
			returnStr = returnStr.substring(0, returnStr.length()-1);
		}
		return returnStr;
	}
	
	/***
	 * 通过packageSellId返回处理过的商品对象
	 * @param packageSellId
	 * @return
	 */
	public List<Map<String,Object>> getPackageSellEntityMap(LvzPackageSellEntity lvzPackageSellEntity){
		try {
			if(null != lvzPackageSellEntity){
				List<Map<String,Object>> packageMapList = new ArrayList<Map<String,Object>>();
				String sellids = lvzPackageSellEntity.getSellids();
				if(StringUtils.isNotBlank(sellids)){
					String[] split_sellid = sellids.split(",");
					for(String sellid : split_sellid){
						LvzSellProductEntity sellProductEntityById = RSBLL.getstance().getLvzSellProductService().getSellProductEntityById(Long.valueOf(sellid));
						Map<String,Object> sellProductMap = new HashMap<String, Object>();
						try {
							sellProductMap = BeanUtils.describe(sellProductEntityById);
							Map<String,Object> map = new HashMap<String, Object>();
							BFAreasEntity aeasEntity = RSBLL.getstance().getAreaService().getAeasEntityById(sellProductEntityById.getArea_id());
							if(aeasEntity != null){
								map.put("aeasname", aeasEntity.getName());
								map.put("aeasid", aeasEntity.getAreaid());
								BFAreasEntity cityEntity = RSBLL.getstance().getAreaService().getAeasEntityById(Long.valueOf(aeasEntity.getParentid()));
								map.put("cityname", cityEntity.getName());
								map.put("cityid", cityEntity.getAreaid());
							}
							sellProductMap.putAll(map);
							//向list中添加map
							packageMapList.add(sellProductMap);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				if(!packageMapList.isEmpty()){
					return packageMapList;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}
}
