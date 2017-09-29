package com.jx.blackmen.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import com.jx.argo.ArgoTool;



/**
 * @author zhangyang
 *
 */
public class MContents {
	
	
	public static String WX_url_menu_create = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
	
	public static String WX_url_get_access_token = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	
	public static String WX_url_get_media = "https://api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";
	
	public static String WX_url_get_ticket_jsapi = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
	
	public static String WX_url_base_shouquan_page = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APP_ID&redirect_uri=REDIRECT_URI&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
	
	public static String WX_url_oauth2 = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APP_ID&secret=APP_SECRET&code=CODE&grant_type=authorization_code";
	
	public static String WX_url_getUser = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";


	public final static int USER_ADD_CHANNEL = 1;//微信添加用户渠道
	public final static int USER_ADD_CHANNEL_M = 14;//M站用户渠道
	public final static long WX_ADD_ORDER_ACTION_CODE = 1101;
	public static final String ORDER_RECEIVED_KEFU_PHONE = "15810145177";
	public static String pubkey = "4h1c12ktl6hn2r1s2r198fbsp7xzl2xi";
	public final static int BEIJING = 598;//北京除了朝阳的价格
	public final static int CHAOYANG = 598;//朝阳的价格
	public final static int SHENZHEN = 198;//深圳的价格
	public final static float YINHANGKAIHU = 400;//银行开户
	public final static float DISHUIBAODAO = 200;//地税报道

	public final static int pay_wx_js = 1;
	public final static int pay_wx_native = 2;
	
	public static final String path = ArgoTool.getConfigFolder() + ArgoTool.getNamespace()+"/wxconfig.properties";
	public static Properties pro = new Properties();    
	static{
		InputStream ins = null;
		try {
			ins = new BufferedInputStream(new FileInputStream(path));
			pro.load(ins);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static final String TOKEN = "his8Zzjzweizhan1025";
	public static final String bd_prefix = "DHBD";
	public static final String weixin_app_id =pro.getProperty("appid");
	public static final String weixin_app_secret_id = pro.getProperty("appsecretid");//"d406ed53dc1d4984323eba33a7f18571";//"07a6dd9789e28772f6de32a2ec057fc0";//
	public static final String weixin_app_name = pro.getProperty("appname");//"gh_f5c1ef705fad";//"gh_944897e71947";//
	public static final String coupon_module_id = pro.getProperty("couponid");  //"20ZZj0K2uaIYaWD0vA7dMFkacME804FkWlYYiorsyPE"
	
	/**
	 * 优惠券过期提醒id
	 */
	public static final String coupon_tips_id = "9l106J1kVLdWNMkw0A1h-UZ2fXsXJB253KUeqNSSqyc";//"9l106J1kVLdWNMkw0A1h-UZ2fXsXJB253KUeqNSSqyc"
	
	public static final String mch_id = "1243594202";
	public static final String ali_seller_id = "2088811440926922";// 支付宝合作id
	public static final String weixin_hosts = "http://m.lvzheng.com";//"http://www.lvzheng.com";
	public static final int WEIXIN_PAY_ALL_STATE = 2;
	public static final int WEIXIN_PAY_CHANNEL = 4;
	public static Map<Integer,Long> scend_id_map = new HashMap<Integer,Long>();
	
	public static Map<String,String> ts_types = new LinkedHashMap<String,String>();
	
	static{
			
			ts_types.put("1", "法律顾问回电不及时");
			ts_types.put("2", "服务周期过长");
			ts_types.put("3", "法律顾问服务态度/专业度");
			ts_types.put("4", "材料的整理、寄送和沟通问题");
			ts_types.put("5", "在线下单流程不清晰、不好用");
			ts_types.put("6", "其他，可在描述中填写");
		
	}
	
	
	
	//订单状态
	public enum OrderState {

		FUWUZHONG(1, "服务中"), DINGDANQUXIAO(2, "订单取消"), YIPAIDAN(3, "已派单"), FUWUWANCHENG(4, "服务完成"),
		YUYUECHENGGONG(7, "预约成功"), DINGDANWANJIE(10, "订单完结");
		
		// 构造方法
		private OrderState(int key, String value) {
			this.key = key;
			this.value = value;
		}

		// 成员变量
		public int key;
		private String value;


		public int getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}
	}
	
	//付款状态
	public static enum PayState {
		WEIFUKUAN(0, "待支付"),YUFUKUAN(1, "已支付"), WANKUAN(9, "已取消");

		// 成员变量
		private  int key;
		private String value;
		
		public static String getPayStateValueByKey(int key){
			for(PayState p : PayState.values()){
				if(p.getKey() == key){
					return p.value;
				}
			}
			return null;
		}
		
		// 构造方法
		private PayState(int key, String value) {
			this.key = key;
			this.value = value;
		}
		public int getKey() {
			return key;
		}
		public String getValue() {
			return value;
		}
	}
	
	/**
	 * 渠道来源
	 * 用户来源(1：微信。2：58同城。3：线下推广，4新官网注册     25,26 自营系统录单 [666-新用户礼包活动PC] [777-新用户礼包活动M]  )
	 * @author bruce
	 * 
	 */
	public enum Channel {

		WX(1, "微信"), WUBA(2, "58同城"), OFFLINE(3, "线下推广"), PC_NEW(4, "新官网注册"),GITF_PC(666,"新用户礼包活动PC"),GIFT_M(777,"新用户礼包活动M");

		// 成员变量
		public int key;
		public String value;

		// 构造方法
		private Channel(int key, String value) {
			this.key = key;
			this.value = value;
		}

		// 覆盖方法
		@Override
		public String toString() {
			return this.key + "_" + this.value;
		}
	}
}
