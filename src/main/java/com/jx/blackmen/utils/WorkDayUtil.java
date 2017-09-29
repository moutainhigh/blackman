package com.jx.blackmen.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jx.blackmen.frame.RSBLL;
import com.jx.service.newcore.entity.WorkDayEntity;

public class WorkDayUtil {
	public static Map<String, Integer> workdayMap = new HashMap<String, Integer>();
	static {
		try {
			Calendar c= Calendar.getInstance();
			c.setTime(new Date());
			List<WorkDayEntity> lwe = RSBLL.getstance().getWorkDayService().getWorkDayForYears(c.get(Calendar.YEAR), 3);
			if (lwe != null && lwe.size() > 0) {
				for (WorkDayEntity wde : lwe) {
					workdayMap.put(wde.getDate(), wde.getIswork());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取指定工作日的截至日期
	 * @param from
	 * @param dead
	 * @return
	 * @throws Exception
	 */
	public static Date getDeadLineDate(Date from, int dead) throws Exception {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(from);
		for (int i = 0; i < dead;) {
			gc.add(GregorianCalendar.DATE, 1);
			if (isWeekday(gc)) {
				// 往后加1天
				i++;
			}
		}
		return gc.getTime();
	}
	/**
	 * 计算延迟时间
	 * @param deadLine
	 * @param dateNow
	 * @return
	 * @throws Exception 
	 */
	public static float getDelayTime(Date deadLine,Date dateNow) throws Exception{
		
		int workDay = 0;
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(deadLine);

		GregorianCalendar gc2 = new GregorianCalendar();
		gc2.setTime(dateNow);
		// 两个日期相差的天数
		long time = gc2.getTime().getTime() - gc.getTime().getTime();
		
		if(time<0){
			return 0;
		}
		
		long day = time / 86400000 + 1;  
	
		if (day < 0) {
			// 如果前日期大于后日期，将返回false
			return 0;
		}
		for (int i = 0; i < day; i++) {
			if (isWeekday(gc)) {
				workDay++;
			}
			// 往后加1天
			gc.add(GregorianCalendar.DATE, 1);
			time -= 86400000;
		}
		if( workDay == 1 && time<86400000 ){
			return 0.5f;
		}else{
			workDay -= 1;
		}
		return workDay;

	}
	
	public static void main(String[] args) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		float time=0f;
		Date test1=null;
		Date test2=null;
		try {
			test1=sdf.parse("2016-06-12 17:49:55");
			test2=sdf.parse("2016-06-12 09:31:28");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		System.out.println(sdf.format(test1));
		try {
			time=getDelayTime(test1,test2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(time);
	}
	


	/**
	 * 某一天是否工作日
	 * 
	 * @param calendar
	 * @return
	 * @throws Exception
	 */
	public static boolean isWeekday(Calendar calendar) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String dayStr = sdf.format(calendar.getTime());
		if (calendar.get(GregorianCalendar.DAY_OF_WEEK) != GregorianCalendar.SATURDAY
				&& calendar.get(GregorianCalendar.DAY_OF_WEEK) != GregorianCalendar.SUNDAY) {
			// 平时
			if (workdayMap.containsKey(dayStr)) {
				if (workdayMap.get(dayStr) == 1) {
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		} else {
			// 周末
			if (workdayMap.containsKey(dayStr)) {
				if (workdayMap.get(dayStr) == 0) {
					return false;
				} else {
					return true;
				}
			} else {
				return false;
			}
		}

	}

}
