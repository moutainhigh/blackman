package com.jx.blackmen.common;

public class UtilTool {

	public static String getMath() {
		String code = "";
		for (int i = 0; i < 6; i++) {
			double random = Math.random() * 9;
			code += (int) random;
		}
		return code;
	}

}
