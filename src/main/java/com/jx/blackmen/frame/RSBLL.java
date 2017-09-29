package com.jx.blackmen.frame;

import com.jx.argo.ArgoTool;
import com.jx.blackface.gaea.sell.contract.ILvzCusEvaluationService;
import com.jx.blackface.gaea.sell.contract.ILvzEnterpriseService;
import com.jx.blackface.gaea.sell.contract.ILvzPackageSellService;
import com.jx.blackface.gaea.sell.contract.ILvzPackageService;
import com.jx.blackface.gaea.sell.contract.ILvzPersonService;
import com.jx.blackface.gaea.sell.contract.ILvzProdcutTitleService;
import com.jx.blackface.gaea.sell.contract.ILvzProductCateService;
import com.jx.blackface.gaea.sell.contract.ILvzProductInfoService;
import com.jx.blackface.gaea.sell.contract.ILvzProductService;
import com.jx.blackface.gaea.sell.contract.ILvzProviderService;
import com.jx.blackface.gaea.sell.contract.ILvzQAinfoService;
import com.jx.blackface.gaea.sell.contract.ILvzSellProductService;
import com.jx.blackface.gaea.usercenter.contract.IAreasService;
import com.jx.blackface.gaea.usercenter.contract.IEmployersService;
import com.jx.blackface.gaea.usercenter.contract.ILoginService;
import com.jx.blackface.gaea.usercenter.contract.ILvzAddressAreaorderService;
import com.jx.blackface.gaea.usercenter.contract.ILvzAddressConfService;
import com.jx.blackface.gaea.usercenter.contract.ILvzMenuConfService;
import com.jx.blackface.gaea.usercenter.contract.ILvzMenuService;
import com.jx.blackface.gaea.usercenter.contract.ILvzPrizeRecordService;
import com.jx.blackface.gaea.usercenter.contract.ILvzPrizeService;
import com.jx.blackface.gaea.usercenter.contract.ILvzProductConfService;
import com.jx.blackface.gaea.usercenter.contract.IPayProcessService;
import com.jx.blackface.gaea.vendor.contract.IVendorServeService;
import com.jx.blackface.servicecoreclient.contract.IOrderBFGService;
import com.jx.blackface.servicecoreclient.contract.IOrderFollowupBFGService;
import com.jx.blackface.servicecoreclient.contract.IPayOrderBFGService;
import com.jx.service.dic.contract.ILvDicService;
import com.jx.service.enterprise.contract.ILvEnterpriseAddressService;
import com.jx.service.enterprise.contract.ILvEnterpriseBusinessDataService;
import com.jx.service.enterprise.contract.ILvEnterpriseBusinessService;
import com.jx.service.enterprise.contract.ILvEnterpriseDicDataService;
import com.jx.service.enterprise.contract.ILvEnterpriseMainBusinessService;
import com.jx.service.enterprise.contract.ILvEnterprisePersonService;
import com.jx.service.enterprise.contract.ILvEnterpriseRoleRelationService;
import com.jx.service.enterprise.contract.ILvEnterpriseService;
import com.jx.service.enterprise.contract.ILvGovService;
import com.jx.service.messagecenter.contract.IMoblieSmsService;
import com.jx.service.messagecenter.contract.IWeixinActionService;
import com.jx.service.messagecenter.contract.IWeixinService;
import com.jx.service.newcore.contract.IArticleCateRelationService;
import com.jx.service.newcore.contract.ICouponsService;
import com.jx.service.newcore.contract.IFAQService;
import com.jx.service.newcore.contract.IProductNewService;
import com.jx.service.newcore.contract.IProductPropertiesService;
import com.jx.service.newcore.contract.ISorderService;
import com.jx.service.newcore.contract.IUserCouponsService;
import com.jx.service.newcore.contract.IWorkDayService;
import com.jx.service.preferential.contract.IPreferentialAccountService;
import com.jx.service.workflow.contract.ILvHistoryService;
import com.jx.service.workflow.contract.ILvNoticeService;
import com.jx.service.workflow.contract.ILvProcessService;
import com.jx.service.workflow.contract.ILvReceiveTaskService;
import com.jx.service.workflow.contract.ILvTaskService;
import com.jx.spat.gaea.client.GaeaInit;
import com.jx.spat.gaea.client.proxy.builder.ProxyFactory;

/**
 * @author zhangyang
 *
 */
public class RSBLL {
	
	private String GAEA_WF = "workflow";
	private String GAEA_UC = "usercenter";
	private String GAEA_EP = "enterprise";
	private String GAEA_UN = "union";
	private String GAEA_MSG ="jxmessage";
	private String GAEA_SELL ="sellcore";
	private String GAEA_SERVICE ="servicecore";
	private String GAEA_VENDOR ="vendorcore";
	private String GAEA_DIC = "dicservice";
	

	static{
		String url = ArgoTool.getConfigFolder()+ArgoTool.getNamespace()+"/gaea.config";
		GaeaInit.init(url);
	}
	
	private static RSBLL newstance = null;
	private static Object lock = new Object();
	public static RSBLL getstance(){
		if(newstance == null){
			synchronized (lock) {
				if(newstance == null){
					newstance = new RSBLL();
				}
			}
		}
		return newstance;
	}
	
	
	
	/**
	 * 工作日  
	 */
	public IWorkDayService getWorkDayService(){
		return ProxyFactory.create(IWorkDayService.class, "tcp://jxcore/WorkDayService");
	}
	
	public IWeixinService getWeixinService(){
		return ProxyFactory.create(IWeixinService.class, "tcp://"+GAEA_MSG+"/WeixinService");
	}
	//微信操作接口
	public IWeixinActionService getWeixinActionService(){
		return ProxyFactory.create(IWeixinActionService.class, "tcp://"+GAEA_MSG+"/WeixinActionService");
	}
	/*手机验证码服务****/
	public IMoblieSmsService getMoblieSmsService() {
		return ProxyFactory.create(IMoblieSmsService.class, "tcp://"+GAEA_MSG+"/MoblieSmsService");
	}
	
	//返回客户表的service
	public ILoginService getLoginService(){
		return ProxyFactory.create(ILoginService.class, "tcp://" + GAEA_UC + "/LoginService");
	}
	
	/*雇员服务**/
	public IEmployersService getEmployerService(){
		return ProxyFactory.create(IEmployersService.class, "tcp://" + GAEA_UC + "/EmployersService");
	} 
	/*地区服务**/
	public IAreasService getAreaService(){
		return ProxyFactory.create(IAreasService.class, "tcp://" + GAEA_UC + "/AreasService");
	}
	
	/**
	 * 
	 * @return
	 */
	public ILvzProdcutTitleService getLvzProductTitleService(){
		return ProxyFactory.create(ILvzProdcutTitleService.class, "tcp://"+GAEA_SELL+"/LvzProdcutTitleService");
	} 
	/**定价条目实体*/
	public ILvzSellProductService getLvzSellProductService(){
		ILvzSellProductService lvzSellProductService = ProxyFactory.create(ILvzSellProductService.class, "tcp://"+GAEA_SELL+"/LvzSellProductService");
		return lvzSellProductService;
	}


	/**商品类别实体*/
	public ILvzProductCateService getLvzProductCateService(){
		ILvzProductCateService productCateService = ProxyFactory.create(ILvzProductCateService.class, "tcp://"+GAEA_SELL+"/LvzProductCateService");
		return productCateService;
	}
	/**
	 * 商品实体
	 * @return
	 */
	public ILvzProductService getLvzProductService(){
		ILvzProductService lvzProductService=ProxyFactory.create(ILvzProductService.class, "tcp://"+GAEA_SELL+"/LvzProductService");
		return lvzProductService;
	}
	
	/**
	 * 地区服务
	 * @return
	 */
	public IAreasService getCityService(){
		IAreasService cityService = ProxyFactory.create(IAreasService.class, "tcp://"+GAEA_UC+"/AreasService");
		return cityService;

	}
	
	/**
	 * 商品信息服务
	 * @return
	 */
	public ILvzProductInfoService getInfoService() {
		ILvzProductInfoService infoService = ProxyFactory.create(ILvzProductInfoService.class, "tcp://"+GAEA_SELL+"/LvzProductInfoService");
		return infoService;

	}

	//得到雇员对象
	public IEmployersService getEmployersService(){
		String url ="tcp://jxcore/EmployersService";
		IEmployersService isc = ProxyFactory.create(IEmployersService.class, url);
		return isc;
	}
	
	/**
	 * 企业库 - 行业特点信息
	 * @return
	 */
	public ILvEnterpriseMainBusinessService getEpEnterpriseMainBusinessService(){
		return ProxyFactory.create(ILvEnterpriseMainBusinessService.class, "tcp://" + GAEA_EP + "/LvEnterpriseMainBusinessService");
	}
	/**
	 * 
	* @Title: getEnterprisePersonService
	* @Description: TODO(企业库- 人员信息)
	* @param @return    设定文件
	* @return ILvEnterprisePersonService    返回类型
	* @author: RENQI  
	* @date 2016年4月18日 下午2:40:43
	* @throws
	 */
	public ILvEnterprisePersonService getEpEnterprisePersonService(){
		String url = "tcp://"+GAEA_EP+"/LvEnterprisePersonService";
		return ProxyFactory.create(ILvEnterprisePersonService.class, url);
	}
	
	/**
	 * 
	* @Title: getE@DescriptionnterpriseRoleRelationService
	* : TODO(企业库- 角色关联信息)
	* @param @return    设定文件
	* @return ILvEnterpriseRoleRelationService    返回类型
	* @author: RENQI  
	* @date 2016年4月18日 下午2:42:50
	* @throws
	 */
	public ILvEnterpriseRoleRelationService getEpEnterpriseRoleRelationService(){
		String url = "tcp://"+GAEA_EP+"/LvEnterpriseRoleRelationService";
		return ProxyFactory.create(ILvEnterpriseRoleRelationService.class, url);
	}
	
	/**
	 * 企业库 - 业务信息
	 * @return
	 */
	public ILvEnterpriseBusinessService getEpEnterpriseBusinessService(){
		return ProxyFactory.create(ILvEnterpriseBusinessService.class, "tcp://" + GAEA_EP + "/LvEnterpriseBusinessService");
	}
	/**
	 * 企业库 - 业务扩展信息
	 * @return
	 */
	public ILvEnterpriseBusinessDataService getEpEnterpriseBusinessDataService(){
		return ProxyFactory.create(ILvEnterpriseBusinessDataService.class, "tcp://" + GAEA_EP + "/LvEnterpriseBusinessDataService");
	}
	/**
	 * 企业库 - 主表信息
	 * @return
	 */
	public ILvEnterpriseService getEpEnterpriseService(){
		return ProxyFactory.create(ILvEnterpriseService.class, "tcp://" + GAEA_EP + "/LvEnterpriseService");
	}
	
	public ILvEnterpriseDicDataService getEpEnterpriseDicDataService(){
		String url = "tcp://"+GAEA_EP+"/LvEnterpriseDicDataService";
		return ProxyFactory.create(ILvEnterpriseDicDataService.class, url);
	}
	
	/**
	 * ep - enterprise gov
	 * @return
	 */
	public ILvGovService getEpGovService(){
		return ProxyFactory.create(ILvGovService.class , "tcp://"+GAEA_EP+"/LvGovService");
	}
	
	/**
	 * ep - enterprise person
	 * @return
	 */
	public ILvEnterpriseAddressService getEpEnterpriseAddressService(){
		return ProxyFactory.create(ILvEnterpriseAddressService.class , "tcp://"+GAEA_EP+"/LvEnterpriseAddressService");
	}
	
	/*新订单服务*/
	public IOrderBFGService getOrderBFGService(){
		return ProxyFactory.create(IOrderBFGService.class, "tcp://"+GAEA_SERVICE+"/OrderBFGService");
	}
	/*新支付服务*/
	public IPayProcessService getPayProcessBFGService(){
		return ProxyFactory.create(IPayProcessService.class, "tcp://"+GAEA_SERVICE+"/PayProcessService");
	}
	/*支付服务*/
	public IPayOrderBFGService getPayOrderService(){
		return ProxyFactory.create(IPayOrderBFGService.class, "tcp://"+GAEA_SERVICE+"/PayOrderBFGService");
	}
	
	/*** follow记录服务  */
	public IOrderFollowupBFGService getFollowService(){
		return ProxyFactory.create(IOrderFollowupBFGService.class, "tcp://"+GAEA_SERVICE+"/OrderFollowupBFGService");
	}
	
	/**
	 * 资讯站问答服务
	 * @return
	 */
	public IFAQService getFAQService(){
		String url = "tcp://jxcore/FAQService";
		IFAQService ifaqService = ProxyFactory.create(IFAQService.class, url);
		return ifaqService;
	}
	/**
	 * 咨询类别商品库类别关系服务
	 * @return
	 */
	public IArticleCateRelationService getArticleCateRelationService(){
		String url = "tcp://jxcore/ArticleCateRelationService";
		IArticleCateRelationService articleCateRelationService = ProxyFactory.create(IArticleCateRelationService.class, url);
		return articleCateRelationService;
	}
	
	/***
	 * 服务商服务表服务
	 * @return
	 */
	public IVendorServeService getVendorServeService(){
		IVendorServeService vendorService = ProxyFactory.create(IVendorServeService.class, "tcp://"+GAEA_VENDOR+"/VendorServeService");
		return vendorService;
	}
	/***
	 * 服务商主表服务
	 * @return
	 */
	public ILvzProviderService getLvzProviderService(){
		return ProxyFactory.create(ILvzProviderService.class, "tcp://"+GAEA_SELL+"/LvzProviderService");
	}
	/***
	 * 企业服务商服务
	 * @return
	 */
	public ILvzEnterpriseService getLvzEnterpriseService(){
		return ProxyFactory.create(ILvzEnterpriseService.class, "tcp://"+GAEA_SELL+"/LvzEnterpriseService");
	}
	/***
	 * 个人服务商服务
	 * @return
	 */
	public ILvzPersonService getLvzPersonService(){
		return ProxyFactory.create(ILvzPersonService.class, "tcp://"+GAEA_SELL+"/LvzPersonService");
	}
	
	
	/**
	 * 热门问答服务
	 */
	public ILvzQAinfoService getQAinfoService(){
		String url = "tcp://sellcore/LvzQAinfoService";
		ILvzQAinfoService iLvzQAinfoService = ProxyFactory.create(ILvzQAinfoService.class, url);
		return iLvzQAinfoService;
	}
	
	/**
	 * 客户评价
	 */
	public ILvzCusEvaluationService getLvzCusEvaluationService(){
		String url = "tcp://"+GAEA_SELL+"/LvzCusEvaluationService";
		ILvzCusEvaluationService lvzCusEvaluationService = ProxyFactory.create(ILvzCusEvaluationService.class, url);
		return lvzCusEvaluationService;
	}
	
	/***
	 * 抽奖服务
	 * @return
	 */
	public ILvzPrizeService getPrizeService(){
		return ProxyFactory.create(ILvzPrizeService.class, "tcp://"+GAEA_UC+"/LvzPrizeService");
	}
	
	/***
	 * 抽奖记录服务
	 * @return
	 */
	public ILvzPrizeRecordService getPrizeRecordService(){
		return ProxyFactory.create(ILvzPrizeRecordService.class, "tcp://"+GAEA_UC+"/LvzPrizeRecordService");
	}
	
	/**
	 * wf - history
	 * @return
	 */
	public ILvHistoryService getWfHistoryService(){
		return ProxyFactory.create(ILvHistoryService.class, "tcp://" + GAEA_WF + "/LvHistoryService");
	}
	
	/**
	 * wf - receive task
	 * @return
	 */
	public ILvReceiveTaskService getWfReceiveTaskService(){
		return ProxyFactory.create(ILvReceiveTaskService.class, "tcp://" + GAEA_WF + "/LvReceiveTaskService");
	}
	
	/**
	 * wf - process
	 * @return
	 */
	public ILvProcessService getWfProcessService(){
		return ProxyFactory.create(ILvProcessService.class, "tcp://" + GAEA_WF + "/LvProcessService");
	}
	
	/**
	 * wf - Task
	 * @return
	 */
	public ILvTaskService getWfTaskService(){
		String url = "tcp://"+GAEA_WF+"/LvTaskService";
		return ProxyFactory.create(ILvTaskService.class, url);
	}
	
	/**
	 * wf - dic data
	 * @return
	 */
	public ILvDicService getDicService(){
		return ProxyFactory.create(ILvDicService.class, "tcp://" + GAEA_DIC + "/LvDicService");
	}
	
	//得到优惠券表接口
	public ICouponsService getCouponsService(){
		return ProxyFactory.create(ICouponsService.class, "tcp://jxcore/CouponsService");
	}
	
	//得到用户优惠券表接口
	public IUserCouponsService getUserCouponsService(){
		return ProxyFactory.create(IUserCouponsService.class, "tcp://jxcore/UserCouponsService");
	}
	
	
	// 自营订单 - 子单主表
	public ISorderService getSorderService(){
		return ProxyFactory.create(ISorderService.class, "tcp://jxcore/SorderService");
	}
	
	//优惠券发送短信 临时
	public ILvNoticeService getNoticeService(){
		return ProxyFactory.create(ILvNoticeService.class, "tcp://"+GAEA_WF+"/LvNoticeService");
	}
	
	/**
	 * 商品操作对象
	 * @return
	 */
	public IProductNewService getProductNewService(){
		String url ="tcp://jxcore/ProductNewService";
		IProductNewService ipe =  ProxyFactory.create(IProductNewService.class, url);
		return ipe;
	}
	
	public IProductPropertiesService getIProductPropertiesService(){
		String url ="tcp://jxcore/ProductPropertiesService";
		IProductPropertiesService ipe =  ProxyFactory.create(IProductPropertiesService.class, url);
		return ipe;
	}
	
	
	/**
	 * 优惠券
	 * @return
	 */
	public static IPreferentialAccountService getPreferentialAccountService(){
	    return ProxyFactory.create(IPreferentialAccountService.class, "tcp://preferential/PreferentialAccountService");
	}
	
	/***** 商品包服务start*/
	public ILvzPackageSellService getPackageSellService(){
		return ProxyFactory.create(ILvzPackageSellService.class, "tcp://" + GAEA_SELL + "/LvzPackageSellService");
	}
	
	public ILvzPackageService getPackageService(){
		return ProxyFactory.create(ILvzPackageService.class, "tcp://" + GAEA_SELL + "/LvzPackageService");
	}
	/**商品包服务end*/
	
	
	/*菜单服务**/
	public ILvzMenuService getlvzMenuService(){
		return ProxyFactory.create(ILvzMenuService.class, "tcp://" + GAEA_UC + "/LvzMenuService");
	} 
	
	/*菜单配置服务**/
	public ILvzMenuConfService getlvzMenuConfService(){
		return ProxyFactory.create(ILvzMenuConfService.class, "tcp://" + GAEA_UC + "/LvzMenuConfService");
	} 
	
	/*商品配置服务**/
	public ILvzProductConfService getlvzProductMenuService(){
		return ProxyFactory.create(ILvzProductConfService.class, "tcp://" + GAEA_UC + "/LvzProductConfService");
	} 
	
	/*地址服务**/
	public ILvzAddressConfService getlvzAddressConfService(){
		return ProxyFactory.create(ILvzAddressConfService.class, "tcp://" + GAEA_UC + "/LvzAddressConfService");
	} 
	
	/*地址排序服务**/
	public ILvzAddressAreaorderService getlvzAddressOrdersByService(){
		return ProxyFactory.create(ILvzAddressAreaorderService.class, "tcp://" + GAEA_UC + "/LvzAddressAreaorderService");
	} 

}
