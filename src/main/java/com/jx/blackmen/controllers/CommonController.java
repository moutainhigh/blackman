package com.jx.blackmen.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jx.argo.ActionResult;
import com.jx.argo.BeatContext;
import com.jx.argo.annotations.Path;
import com.jx.blackface.fileplug.buzs.FileDownloadBuz;
import com.jx.blackface.gaea.sell.entity.LvzCusEvaluationEntity;
import com.jx.blackface.gaea.sell.entity.LvzProductCateEntity;
import com.jx.blackface.gaea.sell.entity.LvzProductEntity;
import com.jx.blackmen.buz.IndexBuz;
import com.jx.blackmen.common.DataFormat;
import com.jx.blackmen.common.ValidateCode;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.utils.ActionResultUtils;
import com.jx.blackmen.utils.MContents;
import com.jx.blackmen.vo.IndexHandVo;
import com.jx.service.dic.entity.DicFileEntity;

/**
 * 微信与m公用方法controller
 * @author duxiaofei
 * @date   2016年4月14日
 */
@Path("/common")
public class CommonController extends BaseController{
	

	/**
	 * 常见问题(微信和m公用)
	 * @return
	 */
	@Path("/FAQ")
	public ActionResult CommonFAQ(){
		return view("/commonFAQ");
	}
	@Path("/toolpage.html")
	public ActionResult toolPage(){
		List<IndexHandVo> volist = IndexBuz.commbuz.getConfigVolist("tools");
		if(null != volist){
			model().add("volist", volist);
		}
		return view("configpage/tools");
	}
	
	/**
	 * 获取图形验证码
	 * @param tokenstr
	 * @return
	 */
	@Path("/picvalidate/{tokenstr:\\S+}")
	public ActionResult picvalidate(final String tokenstr){
		return new ActionResult(){
			@Override
			public void render(BeatContext beatContext) {
				int w = 100;
				String width = beat().getRequest().getParameter("width");
				if(null != width && !"".equals(width) && Integer.parseInt(width) > 0){
					w = Integer.parseInt(width);
				}
				
				int h = 40;
				String height = beat().getRequest().getParameter("height");
				if(null != height && !"".equals(height) && Integer.parseInt(height) > 0){
					h = Integer.parseInt(height);
				}
				
				int c = 4;
				String count = beat().getRequest().getParameter("count");
				if(null != count && !"".equals(count) && Integer.parseInt(count) > 0){
					c = Integer.parseInt(count);
				}
				int lc = 10;
				String lineCount = beat().getRequest().getParameter("lineCount");
				if(null != lineCount && !"".equals(lineCount) && Integer.parseInt(lineCount) > 0){
					lc = Integer.parseInt(lineCount);
				}
				
				ValidateCode vc = new ValidateCode(w,h,c,lc);
				vc.createCode();
				beat().getRequest().getSession().setAttribute("valicode"+tokenstr, vc.getCode());
				try {
					vc.write(beat().getResponse().getOutputStream());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}
	
	@Path("/imgurl/{imgid:\\d+}")
	public ActionResult imgurl(long imgid){
		String maxLength = request().getParameter("maxLength");
		DicFileEntity entity = FileDownloadBuz.getInstance().getLvFileEntity(imgid+"", maxLength);
		if (entity != null) {
			return ActionResultUtils.renderFile(entity.getFileData(), entity.getFileName());
		}
		
		return null;
	}


	@Path("/emp/bindpic/{empid:\\d+}")
	public ActionResult bindWeixin(long empid){
		long uid = empid;
		int scenid = com.jx.blackmen.utils.UtilsHelper.getUniqueSceneid();
		com.jx.blackmen.utils.MContents.scend_id_map.put(scenid,uid);
		System.out.println("cmps con scendid "+scenid+" and emplid "+uid);
		
		String imgurl = "";
		try {
			imgurl = RSBLL.getstance().getWeixinService().makeWeixinQuer(MContents.weixin_app_id, MContents.weixin_app_secret_id,
					scenid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("make pic imgurl is "+imgurl);
		MContents.scend_id_map.put(scenid, empid);
		beat().getModel().add("imgurl", imgurl);
		
		String bindType=beat().getRequest().getParameter("bindType");
		if("vendorBind".equals(bindType)){//是服务商绑定操作
			JSONObject result=new JSONObject();
			result.put("imgurl", imgurl);
			String callback=beat().getRequest().getParameter("jsonpCallback");
			return ActionResultUtils.renderJson(callback+"("+result.toString()+")");
		}
		return view("houtai/bindpic");
	}
	
	
	@Path("getPageListCusEvalByPrdtId")
	public ActionResult getPageListCusEvalByPrdtId(){
		return getPageListCusEvalByPage(1);
	}
	
	@Path("/getPageListByPage/{pageIndex:\\d+}")
	public ActionResult getPageListCusEvalByPage(int pageIndex){
		List<LvzCusEvaluationEntity> list = null;
		String prdtIdStr = request().getParameter("productId");
		String scope = request().getParameter("scope");
		String prdtCateId = request().getParameter("product_cate_id");
		
		
		String paramValue = "test=1";
		String condition = "1=1";
		int count = 0;
		int pageNum = pageIndex;
		int pageSize = 10;
		
		//评分范围
		if(StringUtils.isNotBlank(scope)){
			//检测是否含有“-”  
			if(scope.contains("~")){//按评分范围查询数据
				String[] scopeArr = scope.split("~");
				if(scopeArr.length == 1){//比如 5~  split后只会有一个
					condition += " and zhpf >='"+ scopeArr[0]+"'";
				}else{
					if(StringUtils.isNotBlank(scopeArr[0])){
						condition += " and zhpf >='"+ scopeArr[0]+"'";
					}
					if(StringUtils.isNotBlank(scopeArr[1])){
						condition += " and zhpf <'"+scopeArr[1]+"'";
					}
				}
				paramValue += "&scope="+scope;
			}
		}
		
		if(StringUtils.isBlank(prdtCateId)){//第一次查询，通过getPageListCusEvalByPrdtId 方法进来的
			LvzProductEntity prdtEntity = null;
			try {
				prdtEntity = RSBLL.getstance().getLvzProductService().getProductEntityById(Long.parseLong(prdtIdStr));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			LvzProductCateEntity prdtCateEntity = null;
			try {
				prdtCateEntity = RSBLL.getstance().getLvzProductCateService().getProductCateEntityByCode(prdtEntity.getChild_cate_code());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			prdtCateId = prdtCateEntity.getCate_id() + "";
		}
		
		condition += "  and product_cate_id='" + prdtCateId+ "'";
		paramValue += "&product_cate_id=" + prdtCateId;
		
		try {
			count = RSBLL.getstance().getLvzCusEvaluationService().getCountbyCondition(condition);
			list = RSBLL.getstance().getLvzCusEvaluationService().getListByCondition(condition, pageNum, pageSize, "datetime desc");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(list != null){
			//model().add("list", JSONArray.toJSONString(list, mapping));
			model().add("cusEvalList", list);
		}
		
		model().add("DataFormat", DataFormat.class);
		model().add("cusEvalParamValue", paramValue);
		model().add("pingfenScopeCount", count);
		// 构建分页
		//PageUtils.buildPageModel(model(), pageNum, count, pageSize, "/cusEvaluation/getPageListByPage",paramValue);
		return view("/cusEvaluation/cusEvaluation");
	}
	
	@Path("getAllCountByEvaluationScope")
	public ActionResult getAllCountByEvaluationScope(){
		String prdtIdStr = request().getParameter("productid");
		String scope = request().getParameter("scope");
		String prdtCateId = "";
		
		LvzProductEntity prdtEntity = null;
		try {
			prdtEntity = RSBLL.getstance().getLvzProductService().getProductEntityById(Long.parseLong(prdtIdStr));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		LvzProductCateEntity prdtCateEntity = null;
		try {
			prdtCateEntity = RSBLL.getstance().getLvzProductCateService().getProductCateEntityByCode(prdtEntity.getChild_cate_code());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		prdtCateId = prdtCateEntity.getCate_id()+"";
		
		String[] scopeArr = scope.split("\\|");//通过"|" 分割  比如4-5|3-4|1-3
		Map<String, Integer> scopeMap = new HashMap<String, Integer>();
		for(int i=0;i<scopeArr.length;i++){
			String condition = "1=1 and product_cate_id='" + prdtCateEntity.getCate_id() + "'";
			//检测是否含有“-”  
			if(scopeArr[i].contains("~")){//按评分范围查询数据 比如  4~5
				String[] numArr = scopeArr[i].split("~");
				if(numArr.length == 1){//比如 5~  split后只会有一个
					condition += " and zhpf >='"+ numArr[0]+"'";
				}else{
					if(StringUtils.isNotBlank(numArr[0])){
						condition += " and zhpf >='"+ numArr[0]+"'";
					}
					if(StringUtils.isNotBlank(numArr[1])){
						condition += " and zhpf <'"+numArr[1]+"'";
					}
				}
			}
			
			int count = 0;
			try {
				count = RSBLL.getstance().getLvzCusEvaluationService().getCountbyCondition(condition);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//由于jquery ajax 解析json数据时 类似于pingfenCount3-4 这种无法读取所有要将"-" 替换为 "_"
			//scopeMap.put("pingfenCount"+scopeArr[i].replace("-", "_"), count);
			scopeMap.put("pingfenCount"+scopeArr[i],count);
		}
		return ActionResultUtils.renderJson(JSONObject.toJSONString(scopeMap));
	}
	
}
