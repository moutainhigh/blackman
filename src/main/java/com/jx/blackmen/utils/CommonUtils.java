package com.jx.blackmen.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;

import com.jx.argo.BeatContext;
import com.jx.blackface.gaea.sell.entity.LvzProductEntity;
import com.jx.blackface.gaea.sell.entity.LvzSellProductEntity;
import com.jx.blackmen.frame.RSBLL;

public class CommonUtils {
	//
	public static void geneCode(BeatContext beat) {
		String token = TokenHandler.generateToken();
    	beat.getModel().add("token", token);
    	beat.getRequest().getSession().setAttribute("token", token);
	}
	
	//标准基数
	private static final int fianlNumber = 863;
	
	/**
	 * 根据商品id获取商品所对应的基数 (目前未随机数,需后期完善)
	 * @param sellid
	 */
	public static int getProductBaseNumberBySellid(String sellid){
		if(StringUtils.isBlank(sellid)){
			return fianlNumber;
		}
		int number = fianlNumber + RandomUtils.nextInt(10);
		return number;
	}
	
	/**
	 * 根据二级code获取购买人数
	 * @param child_cate_code
	 * @return
	 */
	public static String getPurchaseNumberByChildCode(String child_cate_code){
		if(StringUtils.isBlank(child_cate_code)){
			return null;
		}
		Integer number = fianlNumber;
		try {
			List<LvzProductEntity> productEntityList = RSBLL.getstance().getLvzProductService().getProductEntityByChildcatecode(child_cate_code);
			if(null != productEntityList && !productEntityList.isEmpty()){
				for(LvzProductEntity lvzPorductE : productEntityList){
					List<LvzSellProductEntity> sellProductEntityList = RSBLL.getstance().getLvzSellProductService().getSellProductEntityList("sell_upderdesc='0' and product_id='"+lvzPorductE.getProduct_id()+"'", 1, 50, null);
					if(null != sellProductEntityList && !sellProductEntityList.isEmpty()){
						for(LvzSellProductEntity lvzSellProductE : sellProductEntityList){
							//根据商品id获取订单count 未判读订单状态
							int countBySellid = RSBLL.getstance().getOrderBFGService().getOrderCountBycondition("sellerid='"+lvzSellProductE.getSell_id()+"'");
							number = number + countBySellid;
						}
					}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return number.toString();
	}
	
	/**
	 * 根据定价条目id获取购买人数
	 * @param sellid
	 * @return
	 */
	public static String getPurchaseNumberBySellid(String sellid){
		if(StringUtils.isBlank(sellid)){
			return null;
		}
		Integer number = fianlNumber;  //getProductBaseNumberBySellid(sellid); 暂时不调用此方法
		try {
			//根据商品id获取订单count 未判读订单状态
			int countBySellid = RSBLL.getstance().getOrderBFGService().getOrderCountBycondition("sellerid='"+sellid+"'");
			number = number + countBySellid;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return number.toString();
	}
	
	/**
	 * 得到年报时间
	 * @param beat
	 */
	public static void getTimer(BeatContext beat){
		Long time = Timers.getBetweenDay("2016-06-20", Timers.riqiToStr(new Date().getTime()));
		String times = "00";
		if(time >= 0){
			times = time.toString();
			if(!"0".equals(times)){
				if(times.length() == 1){
					times = "0" + times;
				}
			}
		}
		beat.getModel().add("timer", times);
	}
	
	public static boolean isActiveTrueTime(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 20);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		
		long startTime = cal.getTime().getTime();
		long endTime = startTime + 1000*60*60*4;
		
		long nowTime = System.currentTimeMillis();
		
		if (nowTime > startTime && nowTime < endTime)
			return true;
		
		return false;
	}
	
	public static void activeFloatFlag(BeatContext beat) {
		int flag = isActiveTrueTime() ? 1 : 0;
		beat.getModel().add("active_time", flag);
	}
	
	public static void activeFlag(BeatContext beat) {
		try {

			HttpServletRequest request = beat.getRequest();
			HttpServletResponse response = beat.getResponse();
			String flag = "0";
			if (isActiveTrueTime()){
			
				String referer = request.getHeader("Referer");
				if (!StringUtils.contains(referer, ".lvzheng.com")){
					String actFlag = CookieUtils.getCookieValueByName("lv_active_f", request);
					if (StringUtils.isBlank(actFlag)) {
						CookieUtils.write(response, "lv_active_f","1", getExpiresTime());
						flag = "1";
					}
				}
			}
			beat.getModel().add("active_flag", flag);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private static int getExpiresTime() {
		Calendar starCalendar = Calendar.getInstance();
		starCalendar.set(Calendar.MILLISECOND, 0);
		Date startDate=starCalendar.getTime();
		
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(startDate);
		endCalendar.set(endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DATE), 23, 59, 59);
		
		Date endDate=endCalendar.getTime();
		int expire=(int) ((endDate.getTime()-startDate.getTime())/1000);
		return expire;
	}
	
	
	public static void main(String[] args) {
	}
}
