package com.jx.blackmen.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jsoup.helper.StringUtil;

import com.jx.argo.BeatContext;
import com.jx.blackface.gaea.usercenter.entity.BFLoginEntity;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.utils.EnterpriseUtils;
import com.jx.blackmen.vo.IncVo;
import com.jx.service.enterprise.contract.ILvEnterpriseRoleRelationService;
import com.jx.service.enterprise.entity.LvEnterpriseEntity;
import com.jx.service.newcore.entity.LoginEntity;

public class IncService {
	private static IncService instance = null;

	public static IncService getInstance() {
		if (instance == null) {
			instance = new IncService();
		}
		return instance;
	}

	/**
	 * 根据手机号获得用户Id
	 * 
	 * @param phoneNum
	 * @return
	 */
	public  String getUserIdByphoneNum(String phoneNum) {
		String userId = "";
		try {
			List<BFLoginEntity> le = RSBLL
					.getstance()
					.getLoginService()
					.getLoginEntity("userphone = '" + phoneNum + "'", 1, 1,
							"userid");
			if (le.size() > 0) {
				userId = le.get(0).getUserid() + "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userId;
	}
	/**
	 * 获得用户对象
	 * @param phoneNum
	 * @return
	 */
	public  BFLoginEntity getLoginEntityByphoneNum(String phoneNum) {
		BFLoginEntity userId = null;
		try {
			List<BFLoginEntity> le = RSBLL
					.getstance()
					.getLoginService()
					.getLoginEntity("userphone = '" + phoneNum + "'", 1, 1,
							"userid");
			if (le.size() > 0) {
				userId = le.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userId;
	}
	
	public List<IncVo> getLoginEntityNoUserinfoStr(BFLoginEntity le){
		
		List<IncVo> list = new ArrayList<IncVo>();
		try {
			if(le != null){
				long userId = le.getUserid();
				list = getBusenessLibraryinfoByuserId(userId);
				String usname = le.getUsername();
				if(StringUtils.isEmpty(usname)){
				}else{
				}
				
				IncVo vo2 = new IncVo();
				vo2.setIncid("new");
				vo2.setIncname("新注册公司");
				
//				String thiridgsmc = "新建公司";
				
//				IncVo vo3 = new IncVo();
//				vo3.setIncid("new");
//				vo3.setIncname(thiridgsmc);
				
				list.add(vo2);
//				list.add(vo3);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 获得用户企业中的公司名称
	 * @param userId
	 * @return
	 */
	public List<IncVo>  getBusenessLibraryinfoByuserId(long userId){
//		List<BusinessLibaryEntity> lbls = null;
		List<IncVo> list = new ArrayList<IncVo>();
		try {

			List<LvEnterpriseEntity> enterpriseList = null;
			try {
				enterpriseList = RSBLL.getstance().getEpEnterpriseService().getEnterpriseListByRoleTypeAndRoleIdWork(ILvEnterpriseRoleRelationService.ROLETYPE_ORDERPERSON, userId + "");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			BFLoginEntity  le = RSBLL.getstance().getLoginService().getLoginEntityById(userId);
			if(enterpriseList != null && !enterpriseList.isEmpty()){
				for(LvEnterpriseEntity enterpriseEntity:enterpriseList){
					IncVo vo = new IncVo();
					vo.setIncid(enterpriseEntity.getEnterpriseId() + "");
					String cname = enterpriseEntity.getName();
					if(StringUtils.isNotEmpty(cname)){
						vo.setIncname(cname);
					}else{
						String phone = le.getUserphone();
						String uname = le.getUsername();
						if(StringUtils.isNotEmpty(uname)){
							vo.setIncname(uname+"的公司");
						}else{
							vo.setIncname(phone+"的公司");
						}
					}
					list.add(vo);
				}
			}
			
			
			
			
/*			lbls = RSBLL.getstance().getBusinessLibaryService()
			.getBusinessLibaryEntityListBypage("addperson ="+userId, 1, 50, "busId");
			if(StringUtil.isListNull(lbls)){
				for(BusinessLibaryEntity ble : lbls){
					IncVo vo = new IncVo();
					vo.setIncid(ble.getBusId()+"");
					String cname = ble.getCompanymc();
					if(StringUtils.isNotEmpty(cname)){
						vo.setIncname(ble.getCompanymc());
					}else{
						String phone = le.getUserphone();
						String uname = le.getUsername();
						if(StringUtils.isNotEmpty(uname)){
							vo.setIncname(uname+"的公司");
						}else{
							vo.setIncname(phone+"的公司");
						}
					}
					list.add(vo);
				}
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 
	 * @param enterpriseMap
	 * @param beat
	 * @return
	 * @throws Exception
	 */
	public long addEnterpriseEntity(String userId, Map<String,String> enterpriseMap, BeatContext beat) throws Exception{
		Long id = 0L;
		Map<String, String> loginInfo = null;
		try {
			loginInfo = EnterpriseUtils.getLoginInfo(beat);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			id = RSBLL.getstance().getEpEnterpriseService().saveEnterpriseAllEntity(enterpriseMap, loginInfo);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if(id > 0L){
			// 保存企业关联人员
			try {
				RSBLL.getstance().getEpEnterpriseRoleRelationService().saveRoleRelationEntity(id, ILvEnterpriseRoleRelationService.ROLETYPE_ORDERPERSON, Long.parseLong(userId), null, loginInfo);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return id;
	}
	
	/**
	 * 
	 * @param enterpriseMap
	 * @param beat
	 * @return
	 * @throws Exception
	 */
/*	public long addEnterpriseEntity(Map<String,String> enterpriseMap, String userId) throws Exception{
		Long id = 0L;
		Map<String, String> loginInfo = null;
		try {
			loginInfo = new HashMap<String, String>();
			loginInfo.put("userId", userId);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			id = RSBLL.getstance().getEnterpriseService().saveEnterpriseAllEntity(enterpriseMap, loginInfo);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if(id > 0L){
			// 保存企业关联人员
			try {
				RSBLL.getstance().getEnterpriseRoleRelationService().saveRoleRelationEntity(id, ILvEnterpriseRoleRelationService.ROLETYPE_ORDERPERSON, Long.valueOf(userId), null, loginInfo);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return id;
	}*/
	
	/**
	 * 获得用户企业中的公司名称
	 * @param userId
	 * @return
	 */
	public LvEnterpriseEntity  getLastIncByUserId(long userId){
/*		List<BusinessLibaryEntity> lbls = null;
		BusinessLibaryEntity bb = null;
		try {
			lbls = RSBLL.getstance().getBusinessLibaryService()
					.getBusinessLibaryEntityListBypage("addperson ="+userId, 1, 50, "busId");
			if(StringUtil.isListNull(lbls)){
				bb = lbls.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		List<LvEnterpriseEntity> enterpriseList = null;
		try {
			enterpriseList = RSBLL.getstance().getEpEnterpriseService().getEnterpriseListByRoleTypeAndRoleIdWork(ILvEnterpriseRoleRelationService.ROLETYPE_ORDERPERSON, userId + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(enterpriseList != null && !enterpriseList.isEmpty()){
			return enterpriseList.get(0);
		}
		return null;
	}
}
