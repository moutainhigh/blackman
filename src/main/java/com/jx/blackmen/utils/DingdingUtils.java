package com.jx.blackmen.utils;

import org.apache.commons.lang.StringUtils;


import com.jx.tools.dingding.auth.AuthHelper;
import com.jx.tools.dingding.exception.OApiException;
import com.jx.tools.dingding.message.ChatMessageDelivery;
import com.jx.tools.dingding.message.MessageHelper;
import com.jx.tools.dingding.message.TextMessage;



/**
 * simple introduction
 *
 * <p>detailed comment</p>
 * @author chuxuebao 2016年1月4日
 * @see
 * @since 1.0
 */

public class DingdingUtils {
	
	public static boolean sendDingding(String chatid, String sender, String content){
		// 获取access token
		String accessToken = null;
		try {
			accessToken = AuthHelper.getAccessToken();
		} catch (OApiException e) {
			e.printStackTrace();
		}
		if(StringUtils.isBlank(accessToken)){
			System.out.println("error: getAccessToken failed.");
			return false;
		}
		TextMessage textMessage = new TextMessage(content);
		ChatMessageDelivery chatMessageDelivery = new ChatMessageDelivery(chatid, sender);
		chatMessageDelivery.withMessage(textMessage);
		try {
			MessageHelper.send(accessToken, chatMessageDelivery);
		} catch (OApiException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
