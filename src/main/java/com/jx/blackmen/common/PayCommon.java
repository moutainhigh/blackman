package com.jx.blackmen.common;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import com.jx.argo.BeatContext;
import com.jx.blackface.gaea.usercenter.entity.PayRecordBFGEntity;
import com.jx.blackface.orderplug.common.MessageHandler;
import com.jx.blackface.orderplug.frame.RSBLL;
import com.jx.blackface.servicecoreclient.entity.PayOrderBFGEntity;
import com.jx.blackface.tools.blackTrack.entity.WebLogs;
import com.jx.service.messagecenter.common.StringValue;
import com.jx.service.messagecenter.util.DateUtils;

public class PayCommon {
	public static PayCommon pbc = new PayCommon();

	public void dealWexixinpay(long payid,BeatContext beat){
		WebLogs logs = WebLogs.getIntanse(PayCommon.class,"testWeixinwap");
		PayOrderBFGEntity poe = null;
		try {
			//poe = RSBLL.getstance().getPayOrderService().getPayOrderByid(payid);
			poe = RSBLL.rb.getPayOrderService().getPayOrderByid(payid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(null != poe){
			String deeplinkstr = makedeeplink(poe.getPayid(),poe.getPaycount(),beat);
			
			logs.printInfoLog();
			beat.getModel().add("deeplinkstr", deeplinkstr);
		}
	}

	private String makedeeplink(long payid,float paycount,BeatContext beat){
		WebLogs logs = WebLogs.getIntanse(PayCommon.class,"makedeeplink");
		String redurl = "";
		String ret = "";
		String totalfee = (int)(paycount*100)+"";
		String ipstr = CommonUtils.getIpAddr(beat);
		//beat().getRequest().getHeader(name)
//		try {
//			ipstr = InetAddress.getLocalHost().getHostAddress();
//		} catch (UnknownHostException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
		SimpleDateFormat sfd = new SimpleDateFormat("yyyyMMddHHmmss");
		
		Date edate = new Date(System.currentTimeMillis()+24*60*60*1000);
		
		String datestr = DateUtils.getFormatDateStr(edate, sfd);
		
		
		PayRecordBFGEntity pre = new PayRecordBFGEntity();
		pre.setOrderid(payid);
		pre.setPrepaystate(0);
		pre.setReqtime(new Date().getTime());
		pre.setTotalfee(Integer.parseInt(totalfee));
		pre.setSyschannel(99);
		long rid = 0;
		
		
		try {
			rid = RSBLL.rb.getPayRecordBFGService().addNewPayrecord(pre);
			logs.putParam("prepayorderid", rid);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			logs.printErrorLog(e1);
		}
		
		String reqstr = "appid=wx76ec5c98f567d0e1&attach=xiaowei&body=lvzheng&mch_id=1243594202&nonce_str=ibuaiVcKdpRxkhJA&notify_url=http://pay.lvzheng.com/wxpay/uporderpay/"+payid+"/"+rid+"/&out_trade_no="+rid+
				"&spbill_create_ip="+ipstr+"&time_expire="+datestr+"&total_fee="+totalfee+"&trade_type=MWEB&key=EKGt478kaAWygmU9AcfkK2vanc8ss8Xj";
				logs.putParam("beformadresgatr", reqstr);//("before md5 regstr is "+reqstr);
				reqstr = StringValue.MD5(reqstr).toUpperCase();
				StringBuffer sb = new StringBuffer();
				sb.append("<xml>");
				sb.append("<appid>wx76ec5c98f567d0e1</appid>");
				sb.append("<attach>xiaowei</attach>");
				sb.append("<body>lvzheng</body>");
				sb.append("<mch_id>1243594202</mch_id>");
				sb.append("<nonce_str>ibuaiVcKdpRxkhJA</nonce_str>");
				sb.append("<notify_url>http://pay.lvzheng.com/wxpay/uporderpay/"+payid+"/"+rid+"/</notify_url>");
				sb.append("<out_trade_no>"+rid+"</out_trade_no>");
				sb.append("<spbill_create_ip>"+ipstr+"</spbill_create_ip>");
				sb.append("<time_expire>"+datestr+"</time_expire>");
				sb.append("<total_fee>"+totalfee+"</total_fee>");
				sb.append("<trade_type>MWEB</trade_type>");
				sb.append("<sign>"+reqstr+"</sign>");
				sb.append("</xml>");
				logs.putParam("sbxmlis", sb.toString());
		
		try {
			ret = CommonUtils.sendMessaeg("https://api.mch.weixin.qq.com/pay/unifiedorder", new String(sb.toString().getBytes(),"ISO8859-1"));//PayBuz.pb.startPayByrecorde(payid, (long)(paycount*100), "", 4, 1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logs.printErrorLog(e);
		}finally{
			logs.putParam("wapresult", ret);
			logs.printInfoLog();
		}
		if(null != ret && !"".equals(ret)){
			redurl = MessageHandler.newstance().getValustrByKeyInWXXML(ret, "mweb_url");
		}
		logs.putParam("redurl", redurl);//System.out.println("deeplink is "+redurl);
		return redurl;
	}
}
