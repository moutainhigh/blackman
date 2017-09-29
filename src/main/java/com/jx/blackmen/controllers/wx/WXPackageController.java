package com.jx.blackmen.controllers.wx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import com.jx.argo.ActionResult;
import com.jx.argo.Model;
import com.jx.argo.annotations.Path;
import com.jx.blackface.gaea.sell.entity.LvzPackageSellEntity;
import com.jx.blackmen.annotaion.WXCheckLogin;
import com.jx.blackmen.controllers.BaseController;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.service.PackageService;

/****
 * 微信商品包维护类
 * @author flower
 */
@Path("/wx")
public class WXPackageController extends BaseController{
	/***
	 * 查询商品包信息
	 * @return
	 */
	@Path("/packageSellList/{productid:\\S+}/{cityid:\\S+}/{areaid:\\S+}")
	public ActionResult getPackageSellList(String productid,String cityid,String areaid){
		if(StringUtils.isBlank(productid) || StringUtils.isBlank(cityid) || StringUtils.isBlank(areaid) ){
			return view("404");
		}
		Model model = beat().getModel();
		PackageService.getInstance().getPackageSellInfoByProductidAndcityidAndareaid(model,productid,cityid,areaid);
		return view("wx/package/packageList");
	}
	
	/***
	 * 通过商品id城市Id区域ID Post商品包页面
	 * @return
	 */
	@Path("/getPackageCountByProductAndCityAndAreaid")
	public ActionResult getPackageCountByProductAndCityAndAreaid(){
		String productid = request().getParameter("productid");
		String cityid = request().getParameter("cityid");
		String areaid = request().getParameter("areaid");
		if(StringUtils.isBlank(productid) || StringUtils.isBlank(cityid) || StringUtils.isBlank(areaid)){
			return view("");
		}
		int packageCount = PackageService.getInstance().getPackageSellCountByProductAndCityidAndareaid(productid, cityid, areaid);
		model().add("productid", productid);
		model().add("cityid", cityid);
		model().add("areaid", areaid);
		if(packageCount > 0 ){
			model().add("packageCount", packageCount);
		}
		return view("wx/package/postpackageCount");
	}
	
	/***
	 * 点击购买商品包
	 * @param packagesellid
	 * @return
	 */
	@WXCheckLogin
	@Path("/buyPackage/{packagesellid:\\S+}")
	public ActionResult buyPackage(String packagesellid){
		if(StringUtils.isBlank(packagesellid)){
			return view("404");
		}
		Map<String,Object> packageOrderMap = new HashMap<String,Object>();
		try {
			LvzPackageSellEntity lvzPackageSellEntity = RSBLL.getstance().getPackageSellService().getLvzPackageSellEntity(Long.valueOf(packagesellid));
			packageOrderMap = BeanUtils.describe(lvzPackageSellEntity);
			List<Map<String, String>> sellProductMapBySellids = PackageService.getInstance().getPaySellProductMapBySellids(lvzPackageSellEntity.getSellids());
			if(null != sellProductMapBySellids && !sellProductMapBySellids.isEmpty()){
				packageOrderMap.put(String.valueOf(lvzPackageSellEntity.getPackagesell_id()), sellProductMapBySellids);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		model().add("packageOrderMap", packageOrderMap);
		return view("/wx/order/orderconfirm_v1");
	}

}
