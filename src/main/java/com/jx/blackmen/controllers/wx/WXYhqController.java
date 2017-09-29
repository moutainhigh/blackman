package com.jx.blackmen.controllers.wx;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jx.argo.ActionResult;
import com.jx.argo.annotations.Path;
import com.jx.blackface.gaea.usercenter.entity.BFLoginEntity;
import com.jx.blackface.orderplug.buzs.OrderBuzForHunter;
import com.jx.blackface.orderplug.util.WorkDayUtil;
import com.jx.blackmen.buz.WeiXinBuz;
import com.jx.blackmen.controllers.BaseController;
import com.jx.blackmen.utils.ActionResultUtils;

/***
 * 微信的优惠券处理
 * @author duxiaofei
 * @date   2016年4月11日
 */
@Path("")
public class WXYhqController extends BaseController{
	
	//从paycenter拷过来的代码
	@Path("/deal/zero")
	  public ActionResult dealZeroAfterPreferential(){
	    String payid = beat().getRequest().getParameter("pay");
	    if(!StringUtils.isEmpty(payid)){
	      OrderBuzForHunter.obfhunter.afterpayOperation(Long.parseLong(payid), 1);
	      String url = "/zerosuccess?payid="+payid;
	      return ActionResultUtils.renderText("{\"success\":\"true\",\"url\":\"" + url + "\"}");
	    }
	    return ActionResultUtils.renderText("{\"success\":\"false\"}");
	  }
	
	
	//0元跳转支付成功页面
		@Path("/zerosuccess")
		  public ActionResult zeroSuccess(){
		    String payid = beat().getRequest().getParameter("payid");
		    if(StringUtils.isNotEmpty(payid)){
		    	beat().getModel().add("payid", payid);
		    }
		    //非工作时间提示
		    try {
				Calendar nowtime=Calendar.getInstance();
				nowtime.setTime(new Date());
				if(WorkDayUtil.isWeekday(nowtime) && nowtime.get(Calendar.HOUR_OF_DAY)>9 && nowtime.get(Calendar.HOUR_OF_DAY)<18){
					beat().getModel().add("message1", "15分钟内小微会与您联系，请注意接听。");
				}else{//下班时间
					beat().getModel().add("message1", "小微的工作时间是工作日9:00－18:00，工作时间小微将及时回复您，请您耐心等待 。");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		    return view("/wx/zerosuccess");
		  }
	
	  @Path("/deal/preferential")
	  public ActionResult dealPreferential(){
		  System.out.println("enter to funtion");
		
		long uid = 0;
		
		
				
//		uid = Long.valueOf("38346088915969") ;
		
	    //如果有优惠活动，优惠活动跟payorder绑定，第一个参数是payorder的优惠信息，之后是order的优惠信息
	    //此版本不包含payorder优惠活动计算
	    long zeroPayid = 0;
	    Map<String,String[]> preferentialInfo = beat().getRequest().getParameterMap();
	    String openId = beat().getRequest().getParameter("openId");
	    if(StringUtils.isEmpty(openId)){
	    	return ActionResultUtils.renderText("{\"success\":\"false\"}");
	    }else{
	    	BFLoginEntity bfloginE;
    		try {
    			System.out.println("ajax---openId——--"+openId);
    			bfloginE = WeiXinBuz.wb.getBFLoginEByopenId(openId);
    			if(null != bfloginE){	
    				uid = bfloginE.getUserid();
    				System.out.println("ajax---uid——--"+uid);
    			}
    		} catch (Exception e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
	    }
	    try {
	      if(!preferentialInfo.isEmpty()){
	        String orderid = "";
	        for(Map.Entry<String, String[]> entry : preferentialInfo.entrySet()){
	          if(entry.getValue() != null){
	        	if(entry.getKey().equals("openId")){
	        		continue;
	        	}else {
	        		if(StringUtils.isEmpty(orderid)){
	  	              orderid = entry.getKey();
	  	            }
//	  	            if(!PreferentialBuz.getInstance().orderDiscountPrePay(uid, entry.getKey(), entry.getValue()[0])){
//	  	              continue;
//	  	            }
	        	}
	          }
	        }
	        if(!StringUtils.isEmpty(orderid)){
	        	
	          System.out.println("param===="+orderid);
	          //计算payorder
	          long afterCal =0;// PreferentialBuz.getInstance().payOrderDiscountPrePay(orderid);
	          if(afterCal < 0){
	            return ActionResultUtils.renderText("{\"success\":\"false\"}");
	          }else if(afterCal > 0){
	            zeroPayid = afterCal;
	          }
	        }
	      }
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return ActionResultUtils.renderText("{\"success\":\"true\",\"pay\":\""+ zeroPayid +"\"}");
	  }

}
