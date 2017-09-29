package com.jx.blackmen.controllers.m;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jx.argo.ActionResult;
import com.jx.argo.Model;
import com.jx.argo.annotations.Path;
import com.jx.blackface.gaea.sell.entity.LvzEnterpriseEntity;
import com.jx.blackface.gaea.sell.entity.LvzPersonEntity;
import com.jx.blackface.gaea.sell.entity.LvzProviderServiceEntity;
import com.jx.blackface.gaea.sell.entity.LvzSellProductEntity;
import com.jx.blackface.gaea.usercenter.entity.BFAreasEntity;
import com.jx.blackface.gaea.usercenter.entity.BFLoginEntity;
import com.jx.blackface.gaea.vendor.entity.VendorServeEntity;
import com.jx.blackface.servicecoreclient.entity.OrderBFGEntity;
import com.jx.blackface.tools.webblack.utils.DateUtils;
import com.jx.blackmen.annotaion.MCheckLogin;
import com.jx.blackmen.common.CommonUtils;
import com.jx.blackmen.controllers.BaseController;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.service.MyServiceService;
import com.jx.blackmen.utils.EntityUtils;
import com.jx.blackmen.utils.TimeUtils;
import com.jx.blackmen.vo.AgencyTaskMappingVo;
import com.jx.blackmen.vo.AgencyTaskVo;
import com.jx.service.newcore.entity.SorderExtEntity;
import com.jx.service.workflow.entity.LvProcInstEntitiy;
import com.jx.service.workflow.entity.LvTaskEntity;

/***
 * 微信端我的服务
 * @author duxiaofei
 * @date   2016年4月13日
 */
@Path("/m/myservice")
public class MServiceController extends BaseController{
	private String VAR_BUS_ID = "busid";
	private String PROC_DEF_KEY_BJ_COMPANY_REG  = "bj-all-company_reg";
	private String PROC_DEF_KEY_BJ_LOCAL_TAX  = "bj-all-local_tax_reg";
	private String PROC_DEF_KEY_BJ_NATIONAL_TAX  = "bj-all-national_tax_reg";
	
	
	/***
	 * 查看服务
	 * @return
	 */
	@Path("/lookService/{orderid:\\S+}")
	public ActionResult lookService(String orderid){
		Model model = beat().getModel();
		if(StringUtils.isBlank(orderid)){
			return view("/404");
		}
		//调用公共的查看服务初始化页面
		MyServiceService.myservice.showServiceInit(model, Long.valueOf(orderid));
		
		return view("/m/service/lookservice");
	}
	
	/***
	 * 查看代办服务服务
	 * @return
	 */
	@Path("/lookAgencyService")
	public ActionResult lookAgencyService(){
		String procInstId = request().getParameter("procInstId");
		String serviceName = request().getParameter("serviceName");
		String pro_def_id = request().getParameter("proDefId");
		String cityname = request().getParameter("cityname");
		String areaname = request().getParameter("areaname");
		String deleteReasonFlag = request().getParameter("deleteReasonFlag");
		if(deleteReasonFlag != null){
			model().add("deleteReasonFlag", deleteReasonFlag);
		}
		if(StringUtils.isNotBlank(cityname)){
			model().add("cityname", cityname);
		}
		if(StringUtils.isNotBlank(areaname)){
			model().add("areaname", areaname);
		}
		if(StringUtils.isNotBlank(serviceName)){
			model().add("serviceName", serviceName);
		}
		
		//procInstId = "10812236651880448";
		
		List<LvTaskEntity> hisTaskList = null;
		try {
			hisTaskList = RSBLL.getstance().getWfHistoryService().getPageHisTaskListByProcessInstanceId(procInstId, 0, 100);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(hisTaskList == null || hisTaskList.size() == 0){
			//转到的页面路径
			return view("/m/service/agencyLookservice");
		}
		
		Map<String,String> map = getShowTaskContent(hisTaskList);//前台需要展示的当前环节数据
		if(map == null){
			List<String> showTaskList = null;
			Map<String,String> handleNodeMap = null;
			//判断流程类型（公司注册或地税报道或国税报道....）
			String[] strArr = pro_def_id.split(":");
			if(PROC_DEF_KEY_BJ_COMPANY_REG.equals(strArr[0])){
				showTaskList = AgencyTaskMappingVo.getShowCompanyRegList();
				handleNodeMap = AgencyTaskMappingVo.getCompanyRegHandleNodeMap();
			}else if(PROC_DEF_KEY_BJ_LOCAL_TAX.equals(strArr[0])){
				showTaskList = AgencyTaskMappingVo.getShowlocalTaxList();
				handleNodeMap = AgencyTaskMappingVo.getLocalTaxHandleNodeMap();
			}else if(PROC_DEF_KEY_BJ_NATIONAL_TAX.equals(strArr[0])){
				showTaskList = AgencyTaskMappingVo.getShownationalTaxList();
				handleNodeMap = AgencyTaskMappingVo.getNationalTaxHandleNodeMap();
			}
			
			if(showTaskList != null && handleNodeMap != null){
				model().add("handleNodeKeyList", showTaskList);
				model().add("handleNodeMap", handleNodeMap);
			}
			
			//转到的页面路径
			return view("/m/service/agencyLookservice");
		}
		
		model().addAll(map);
		//前台需要展示的历史环节数据
		List<AgencyTaskVo> list = getHisHandleNodeData(hisTaskList,pro_def_id);
		list = EntityUtils.sortList(list, "datatime", false);
		
		if(list != null){
			model().add("hisTaskNodes", list);
		}
		
		//转到的页面路径
		return view("/m/service/agencyLookservice");
	}
	
	
	public List<AgencyTaskVo> getHisHandleNodeData(List<LvTaskEntity> taskList,String proc_def_id){
		List<AgencyTaskVo> list = new ArrayList<AgencyTaskVo>();
		List<String> showTaskList = null;
		Map<String,String> handleNodeMap = null;
		Map<String,String> handleNoteMap = null;
		Map<String,String> handleStateMap = null;
		
		//String proc_def_id = taskEntity.getProcessDefinitionId();
		//判断流程类型（公司注册或地税报道或国税报道....）
		String[] strArr = proc_def_id.split(":");
		if(PROC_DEF_KEY_BJ_COMPANY_REG.equals(strArr[0])){
			showTaskList = AgencyTaskMappingVo.getShowCompanyRegList();
			handleNoteMap = AgencyTaskMappingVo.getCompanyRegHandleNoteMap();
			handleStateMap = AgencyTaskMappingVo.getCompanyRegHandleStateMap();
			handleNodeMap = AgencyTaskMappingVo.getCompanyRegHandleNodeMap();
		}else if(PROC_DEF_KEY_BJ_LOCAL_TAX.equals(strArr[0])){
			showTaskList = AgencyTaskMappingVo.getShowlocalTaxList();
			handleNoteMap = AgencyTaskMappingVo.getLocalTaxHandleNoteMap();
			handleStateMap = AgencyTaskMappingVo.getLocalTaxHandleStateMap();
			handleNodeMap = AgencyTaskMappingVo.getLocalTaxHandleNodeMap();
		}else if(PROC_DEF_KEY_BJ_NATIONAL_TAX.equals(strArr[0])){
			showTaskList = AgencyTaskMappingVo.getShownationalTaxList();
			handleNoteMap = AgencyTaskMappingVo.getNationalTaxHandleNoteMap();
			handleStateMap = AgencyTaskMappingVo.getNationalTaxHandleStateMap();
			handleNodeMap = AgencyTaskMappingVo.getNationalTaxHandleNodeMap();
		}else{
			return null;
		}
		
		model().add("handleNodeKeyList", showTaskList);
		model().add("handleNodeMap", handleNodeMap);
		
		for(LvTaskEntity taskEntity : taskList){
			for(int i=0;i<showTaskList.size();i++){
				if((taskMapping(taskEntity.getTaskDefinitionKey()) == null 
						? taskEntity.getTaskDefinitionKey() : taskMapping(taskEntity.getTaskDefinitionKey()))
						.equals(showTaskList.get(i))){
					AgencyTaskVo taskVo = new AgencyTaskVo();
					taskVo.setDatatime(DateUtils.getFormatDateStr(taskEntity.getEndTime(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
					taskVo.setHandleStateText(handleStateMap.get(showTaskList.get(i)));
					taskVo.setHandleNoteText(handleNoteMap.get(showTaskList.get(i)));
					list.add(taskVo);
					break;
				}
			}
		}
		return list;
	}
	
	
	/***通过平台orderid查询自营的orderid*/;
	public List<String> getZYorderidListByPTorderid(String ptOrderid){
		List<String> list = new ArrayList<String>();
		//1.查询t_sorder_ext表，通过dataKey=terraceOrderId and dataValue='{ptOrderid}'
		List<SorderExtEntity> sorderExtList = null;
		try {
			sorderExtList = RSBLL.getstance().getSorderService().getSorderExtByDataKeyAndDataValue("terraceOrderId",ptOrderid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(sorderExtList == null || sorderExtList.size() == 0){
			return null;
		}
		
		SorderExtEntity sorderExt = null;
		for(int i=0;i<sorderExtList.size();i++){
			list.add(sorderExtList.get(i).getOrderId()+"");//自营的订单id
		}
		
		return list;
	};
	
	/***
	 * 我的代办服务
	 * @return
	 */
	@MCheckLogin
	@Path("/agency.html")
	public ActionResult agency(){
		Model model = beat().getModel();
		model.add("tab", "agency");
		BFLoginEntity userLoginEntity = null;
		String result = "/m/service";
		try{
			userLoginEntity = CommonUtils.getLoginEntity(beat());
			//userLoginEntity = new BFLoginEntity();
			//userLoginEntity.setUserid(38546352173313L);
			
			List<Map<String,Object>> listmap = new ArrayList<Map<String,Object>>();
			List<String> zyOrderidList = new ArrayList<String>();
			long count = 0;
			
			if(userLoginEntity != null){
				List<VendorServeEntity> pageVendorServList = RSBLL.getstance().getVendorServeService().getVendorServeListBycondition(" userid ="+userLoginEntity.getUserid()+" and (status = 9 or status = 10) ", 1, 99, " starttime desc ");
				if(pageVendorServList != null){
					if(pageVendorServList.size()>0){
						
						for(VendorServeEntity entity : pageVendorServList){
							//通过平台orderid查询自营的orderid
							List<String> ZYorderids = getZYorderidListByPTorderid(entity.getOrderid()+"");
							//List<String> ZYorderids = getZYorderidListByPTorderid("41442261603841");
							if(ZYorderids != null && ZYorderids.size() > 0){
								zyOrderidList.addAll(ZYorderids);
							}
							
							
							//Map<String,Object> map = new HashMap<String, Object>();
							
							long orderid_1 = entity.getOrderid();
							
							//查找服务商电话--------start
							LvzProviderServiceEntity provider = RSBLL.getstance().getLvzProviderService().getEntityById(entity.getVendorid());
							
							if(provider!=null){
								//服务商类别   1:[企业]  2:[个人]
								int flag = provider.getProvider_flag();
								String tel = "";
								if(1==flag){
									List<LvzEnterpriseEntity> lvzEnterpriseList = RSBLL.getstance().getLvzEnterpriseService().getLvzEnterpriseList(" pid = "+provider.getProvider_id(), 1, 99, "");
									if(lvzEnterpriseList!=null){
										if(lvzEnterpriseList.size()>0){
											tel = lvzEnterpriseList.get(0).getContactPhone();
										}
									}
								}else if(2==flag){
									List<LvzPersonEntity> lvzPersionEntityList = RSBLL.getstance().getLvzPersonService().getLvzPersionEntityList(" pid = "+provider.getProvider_id(), 1, 99, "");
									
									if(lvzPersionEntityList!=null){
										if(lvzPersionEntityList.size()>0){
											tel = lvzPersionEntityList.get(0).getPersonPhone();
										}
									}
								}
								model().add("tel", tel);
							}
							//查找服务商电话--------end
							OrderBFGEntity order_1 = RSBLL.getstance().getOrderBFGService().loadOrderBFGEntityByid(orderid_1);
							if(order_1!=null){
								long sellid = order_1.getSellerid();
								long payid = order_1.getPayid();
								long orderid = order_1.getOrderid();
								//根据sellid查询
								LvzSellProductEntity sellmodel = RSBLL.getstance().getLvzSellProductService().getSellProductEntityById(sellid);
								if(sellmodel!=null){
									//map.put("sname", sellmodel.getSell_product_name());
									//map.put("starttime",TimeUtils.DateToStr(e.getStarttime()));
									//map.put("sellid",sellid);
									//map.put("payid", payid);
									//map.put("orderid", orderid);
									//map.put("", value);
									
									BFAreasEntity aeasEntityById = RSBLL.getstance().getAreaService().getAeasEntityById(sellmodel.getArea_id());
									BFAreasEntity cityEntityById = RSBLL.getstance().getAreaService().getAeasEntityById(sellmodel.getCity_id());
									if(cityEntityById!=null){
										model().add("cityname", cityEntityById.getName());
									}
									if(aeasEntityById!=null){
										model().add("areaname", aeasEntityById.getName());
									}
									
									//map.put("status", entity.getStatus());
									//map.put("endtime", TimeUtils.DateToStr(entity.getEndtime()));
									
									//listmap.add(map);
								}
							}
							
						}
						
						List<LvProcInstEntitiy> lvProcInstList = null;
						
						Map<String, Object> variable = new HashMap<String, Object>();
						variable.put("taskOrderId", zyOrderidList);
						List<String> varKeyList = new ArrayList<String>();
						varKeyList.add(VAR_BUS_ID);
						lvProcInstList = RSBLL.getstance().getWfHistoryService().getNativePageHisProcListWithVarByVariable(variable, 0, 99, varKeyList);
						count = RSBLL.getstance().getWfHistoryService().getNativePageHisProcListWithVarByVariableCount(variable);
						if(lvProcInstList == null || lvProcInstList.size() == 0){
							return view(result);
						}
						
						for (LvProcInstEntitiy proInstEntity : lvProcInstList) {
							//公司名称
							Map<String, Object> processVariables = proInstEntity.getProcessVariables();
							if(processVariables != null){
								Object busidObj = processVariables.get(VAR_BUS_ID);
								if(busidObj != null){
									String enterpriseName = null;
									try {
										enterpriseName = RSBLL.getstance().getEpEnterpriseService().getMainValueByEnterpriseIdAndKey(busidObj.toString(), "name");
									} catch (Exception e2) {
										e2.printStackTrace();
									}
									if(StringUtils.isNotBlank(enterpriseName)){
										processVariables.put("enterpriseName", enterpriseName);
									}
								}
							}
							
							String endTime = DateUtils.getFormatDateStr(proInstEntity.getEndTime(), new SimpleDateFormat("yyyy-MM-dd"));
							processVariables.put("handleEndTime", endTime);
							String startTime = DateUtils.getFormatDateStr(proInstEntity.getStartTime(), new SimpleDateFormat("yyyy-MM-dd"));
							processVariables.put("handleStartTime", startTime);
						
							
							//服务名称
							String proc_def_id = proInstEntity.getProcessDefinitionId();
							//判断流程类型（公司注册或地税报道或国税报道....）
							String[] strArr = proc_def_id.split(":");
							if(PROC_DEF_KEY_BJ_COMPANY_REG.equals(strArr[0])){//公司注册
								processVariables.put("serviceName", "公司注册");
							}else if(PROC_DEF_KEY_BJ_LOCAL_TAX.equals(strArr[0])){//地税
								processVariables.put("serviceName", "地税报到");
							}else if(PROC_DEF_KEY_BJ_NATIONAL_TAX.equals(strArr[0])){//国税
								processVariables.put("serviceName", "国税报到");
							}else{
								System.out.println("代办服务未知流程："+proc_def_id);
								processVariables.put("serviceName", "");
							}
						}
						
						model().add("pageVendorServList", lvProcInstList);
					}
				}	
			}
			
		//model.add("listmap", listmap);

		//热门
		List<Map<String, String>> hotlist = getHostList("m");
		
		model().add("hotlist", hotlist);
		}catch(Exception e){
			e.printStackTrace();
		}
		return view(result);
	}
	
	
	/***
	 * 我的代办服务
	 * @return
	 *//*
	//@MCheckLogin
	@Path("/agency.html")
	public ActionResult agency(){
		Model model = beat().getModel();
		model.add("tab", "agency");
		BFLoginEntity userLoginEntity = null;
		String result = "/m/service";
		try{
			//userLoginEntity = CommonUtils.getLoginEntity(beat());
			userLoginEntity = new BFLoginEntity();
			userLoginEntity.setUserid(35422031366401L);
			List<Map<String,Object>> listmap = new ArrayList<Map<String,Object>>();
			if(userLoginEntity != null){
				List<VendorServeEntity> list = RSBLL.getstance().getVendorServeService().getVendorServeListBycondition(" userid ="+userLoginEntity.getUserid()+" and (status = 9 or status = 10) ", 1, 99, " starttime desc ");
				if(list!=null){
					if(list.size()>0){
						
						
						for (VendorServeEntity e:list) {
							
							Map<String,Object> map = new HashMap<String, Object>();
							
							long orderid_1 = e.getOrderid();
							
							//查找服务商电话--------start
							LvzProviderServiceEntity provider = RSBLL.getstance().getLvzProviderService().getEntityById(e.getVendorid());
							
							if(provider!=null){
								//服务商类别   1:[企业]  2:[个人]
								int flag = provider.getProvider_flag();
								String tel = "";
								if(1==flag){
									List<LvzEnterpriseEntity> lvzEnterpriseList = RSBLL.getstance().getLvzEnterpriseService().getLvzEnterpriseList(" pid = "+provider.getProvider_id(), 1, 99, "");
									if(lvzEnterpriseList!=null){
										if(lvzEnterpriseList.size()>0){
											tel = lvzEnterpriseList.get(0).getContactPhone();
										}
									}
								}else if(2==flag){
									List<LvzPersonEntity> lvzPersionEntityList = RSBLL.getstance().getLvzPersonService().getLvzPersionEntityList(" pid = "+provider.getProvider_id(), 1, 99, "");
									
									if(lvzPersionEntityList!=null){
										if(lvzPersionEntityList.size()>0){
											tel = lvzPersionEntityList.get(0).getPersonPhone();
										}
									}
								}
								map.put("tel", tel);
							}
							//查找服务商电话--------end
							
							OrderBFGEntity order_1 = RSBLL.getstance().getOrderBFGService().loadOrderBFGEntityByid(orderid_1);
							if(order_1!=null){
								long sellid = order_1.getSellerid();
								long payid = order_1.getPayid();
								long orderid = order_1.getOrderid();
								//根据sellid查询
								LvzSellProductEntity sellmodel = RSBLL.getstance().getLvzSellProductService().getSellProductEntityById(sellid);
								if(sellmodel!=null){
									map.put("sname", sellmodel.getSell_product_name());
									map.put("starttime",TimeUtils.DateToStr(e.getStarttime()));
									map.put("sellid",sellid);
									map.put("payid", payid);
									map.put("orderid", orderid);
									//map.put("", value);
									
									BFAreasEntity aeasEntityById = RSBLL.getstance().getAreaService().getAeasEntityById(sellmodel.getArea_id());
									BFAreasEntity cityEntityById = RSBLL.getstance().getAreaService().getAeasEntityById(sellmodel.getCity_id());
									if(cityEntityById!=null){
										map.put("cityname", cityEntityById.getName());
									}
									if(aeasEntityById!=null){
										map.put("areaname", aeasEntityById.getName());
									}
									
									map.put("status", e.getStatus());
									map.put("endtime", TimeUtils.DateToStr(e.getEndtime()));
									
									listmap.add(map);
								}
							}
							
						}
						
						
						
					}
				}	
			}
		model.add("listmap", listmap);

		//热门
		List<Map<String, String>> hotlist = getHostList("m");
		
		model().add("hotlist", hotlist);
		}catch(Exception e){
			e.printStackTrace();
		}
		return view(result);
	}
	*/
	
	/**通过orderid 到查询 此订单的 历史节点*/
	public List<AgencyTaskVo> getHisTaskListByOrderid(long orderid){
		//1.查询t_sorder_ext表，通过dataKey=terraceOrderId and dataValue='{orderid}'
		List<SorderExtEntity> sorderExtList = null;
		List<AgencyTaskVo> agencyTaskVoList = new ArrayList<AgencyTaskVo>();
		try {
			sorderExtList = RSBLL.getstance().getSorderService().getSorderExtByOrderId(orderid+"");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(sorderExtList == null || sorderExtList.size() == 0){
			return null;
		}
		
		SorderExtEntity sorderExt = null;
		for(int i=0;i<sorderExtList.size();i++){
			String dataKey = sorderExtList.get(i).getDataKey();
			if("terraceOrderId".equals(dataKey)){//平台的订单id
				sorderExt = sorderExtList.get(i);
				break;
			}
		}
		
		if(sorderExt == null){
			return null;
		}
		
		//2.查询act_hi_variable表，通过TEXT_='{t_sorder_ext.orderId}' and NAME_='{taskOrderId}'
		List<LvProcInstEntitiy> lvProcInstList = null;
		Map<String, Object> variable = new HashMap<String, Object>();
		variable.put("taskOrderId", orderid);
		/*try {
			lvProcInstList = RSBLL.getstance().getWfHistoryService().getPageHisProcListByVariable(variable, 0, 20);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		List<String> varKeyList = new ArrayList<String>();
		varKeyList.add(VAR_BUS_ID);
		try {
			lvProcInstList = RSBLL.getstance().getWfHistoryService().getPageHisProcListWithVarByVariableWithOutSubProc(variable, 0, 20, varKeyList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(lvProcInstList == null){
			return null;
		}
		
		for(LvProcInstEntitiy lvProcInstEntitiy:lvProcInstList){
			AgencyTaskVo agencyTaskVo = new AgencyTaskVo();
			//3.查询act_hi_taskinst表，通过PROC_INST_ID='{act_hi_variable.PROC_INST_ID}',获取到(历史任务节点)
			// 历史任务
			int pageSize = 100;
			List<LvTaskEntity> hisTaskList = null;
			try {
				hisTaskList = RSBLL.getstance().getWfHistoryService().getPageHisTaskListByProcessInstanceId(lvProcInstEntitiy.getId(), 0, pageSize);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//公司名称
			Map<String, Object> processVariables = lvProcInstEntitiy.getProcessVariables();
			if(processVariables != null){
				Object busidObj = processVariables.get(VAR_BUS_ID);
				if(busidObj != null){
					String enterpriseName = null;
					try {
						enterpriseName = RSBLL.getstance().getEpEnterpriseService().getMainValueByEnterpriseIdAndKey(busidObj.toString(), "name");
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(StringUtils.isNotBlank(enterpriseName)){
						processVariables.put("enterpriseName", enterpriseName);
						agencyTaskVo.setCompanyName(enterpriseName);
					}
				}
			}
			
			agencyTaskVo.setHisTaskNodes(hisTaskList);
			agencyTaskVoList.add(agencyTaskVo);
			
		}
		
		//4.返回
		return agencyTaskVoList;
	}
	
	
	
	public Map<String,String> getShowTaskContent(List<LvTaskEntity> hisTaskList){
		int index = -1;//下标
		String endTime = "";
		Map<String,String> map = null;
		
		// 历史任务
		List<String> showTaskList = null;
		Map<String,String> handleNoteMap = null;
		Map<String,String> handleStateMap = null;
		Map<String,String> handleNodeMap = null;
		
		for(LvTaskEntity taskEntity : hisTaskList){
			String proc_def_id = taskEntity.getProcessDefinitionId();
			//判断流程类型（公司注册或地税报道或国税报道....）
			String[] strArr = proc_def_id.split(":");
			if(PROC_DEF_KEY_BJ_COMPANY_REG.equals(strArr[0])){
				showTaskList = AgencyTaskMappingVo.getShowCompanyRegList();
				handleNoteMap = AgencyTaskMappingVo.getCompanyRegHandleNoteMap();
				handleStateMap = AgencyTaskMappingVo.getCompanyRegHandleStateMap();
				handleNodeMap = AgencyTaskMappingVo.getCompanyRegHandleNodeMap();
			}else if(PROC_DEF_KEY_BJ_LOCAL_TAX.equals(strArr[0])){
				showTaskList = AgencyTaskMappingVo.getShowlocalTaxList();
				handleNoteMap = AgencyTaskMappingVo.getLocalTaxHandleNoteMap();
				handleStateMap = AgencyTaskMappingVo.getLocalTaxHandleStateMap();
				handleNodeMap = AgencyTaskMappingVo.getLocalTaxHandleNodeMap();
			}else if(PROC_DEF_KEY_BJ_NATIONAL_TAX.equals(strArr[0])){
				showTaskList = AgencyTaskMappingVo.getShownationalTaxList();
				handleNoteMap = AgencyTaskMappingVo.getNationalTaxHandleNoteMap();
				handleStateMap = AgencyTaskMappingVo.getNationalTaxHandleStateMap();
				handleNodeMap = AgencyTaskMappingVo.getNationalTaxHandleNodeMap();
			}else{
				return null;
			}
			
			//与前台要展示的节点对比，并记录下标
			for(int i=0;i<showTaskList.size();i++){
				if((taskMapping(taskEntity.getTaskDefinitionKey()) == null 
						? taskEntity.getTaskDefinitionKey() : taskMapping(taskEntity.getTaskDefinitionKey()))
						.equals(showTaskList.get(i))){
					if(index < i){
						index = i;
						endTime = DateUtils.getFormatDateStr(taskEntity.getEndTime(), new SimpleDateFormat("yyyy-MM-dd"));
					}
					break;
				}
			}
		}
		
		if(index == -1){
			return null;
		}else{
			map = new HashMap<String, String>();
			map.put("curHandleNodeKey", showTaskList.get(index)) ;
			map.put("curHandleNodeText", handleNodeMap.get(showTaskList.get(index)));
			map.put("handleStateText", handleStateMap.get(showTaskList.get(index)));
			map.put("handleNoteText", handleNoteMap.get(showTaskList.get(index)));
			map.put("handleEndTime", endTime);
		}
		return map;
	}
	
	
	/**判断是否存在任务映射*/
	public String taskMapping(String curTaskDefKey){
		String key = AgencyTaskMappingVo.getExclusiveTaskMapping().get(curTaskDefKey);
		return key;
	}
	
	/**获取前台最终要展示的节点key*/
	/*public Map<String,String> getShowTaskInfo(AgencyTaskVo agencyTaskVo){
		int index = -1;//下标
		String endTime = "";
		Map<String,String> map = new HashMap<String, String>();
		if(agencyTaskVo.getHisTaskNodes() == null || agencyTaskVo.getHisTaskNodes().size() == 0){
			return null;
		}
		List<LvTaskEntity> taskList = agencyTaskVo.getHisTaskNodes();
		List<String> showTaskList = null;
		Map<String,String> handleNoteMap = null;
		Map<String,String> handleStateMap = null;
		Map<String,String> handleNodeMap = null;
		
		for(LvTaskEntity taskEntity : taskList){
			String proc_def_id = taskEntity.getProcessDefinitionId();
			//判断流程类型（公司注册或地税报道或国税报道....）
			String[] strArr = proc_def_id.split(":");
			if(PROC_DEF_KEY_BJ_COMPANY_REG.equals(strArr[0])){
				showTaskList = AgencyTaskMappingVo.getShowCompanyRegList();
				handleNoteMap = AgencyTaskMappingVo.getCompanyRegHandleNoteMap();
				handleStateMap = AgencyTaskMappingVo.getCompanyRegHandleStateMap();
				handleNodeMap = AgencyTaskMappingVo.getCompanyRegHandleNodeMap();
			}else if(PROC_DEF_KEY_BJ_LOCAL_TAX.equals(strArr[0])){
				showTaskList = AgencyTaskMappingVo.getShowlocalTaxList();
				handleNoteMap = AgencyTaskMappingVo.getLocalTaxHandleNoteMap();
				handleStateMap = AgencyTaskMappingVo.getLocalTaxHandleStateMap();
				handleNodeMap = AgencyTaskMappingVo.getLocalTaxHandleNodeMap();
			}else if(PROC_DEF_KEY_BJ_NATIONAL_TAX.equals(strArr[0])){
				showTaskList = AgencyTaskMappingVo.getShownationalTaxList();
				handleNoteMap = AgencyTaskMappingVo.getNationalTaxHandleNoteMap();
				handleStateMap = AgencyTaskMappingVo.getNationalTaxHandleStateMap();
				handleNodeMap = AgencyTaskMappingVo.getNationalTaxHandleNodeMap();
			}else{
				return null;
			}
			
			//与前台要展示的节点对比，并记录下标
			for(int i=0;i<showTaskList.size();i++){
				if(taskEntity.getTaskDefinitionKey().equals(showTaskList.get(i))){
					if(index < i){
						index = i;
						endTime = DateUtils.getFormatDateStr(taskEntity.getEndTime(), new SimpleDateFormat("yyyy-MM-dd"));
					}
					break;
				}
			}
			
		}
		
		if(index == -1){
			return null;
		}else{
			map.put("curHandleNodeKey", showTaskList.get(index)) ;
			map.put("curHandleNodeText", handleNodeMap.get(showTaskList.get(index)));
			map.put("handleStateText", handleStateMap.get(showTaskList.get(index)));
			map.put("handleNoteText", handleNoteMap.get(showTaskList.get(index)));
			map.put("handleEndTime", endTime);
		}
		return map;
	}*/
	
	
	
}
