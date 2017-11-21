package com.tg.cloudmanagement.net;

import java.security.MessageDigest;
import java.util.Locale;



public class MD5 {

	/**
	 * MD5加密密文返回
	 * */
	public static String getMd5Value(String sSecret) throws Exception {
		MessageDigest bmd5 = MessageDigest.getInstance("MD5");
		bmd5.update(sSecret.getBytes());
		StringBuffer buf = new StringBuffer();
		String result = byte2hex(bmd5.digest(buf.toString().getBytes("UTF-8")));
		return result;
	}

	/**
	 * 二进制转字符串
	 */
	private static String byte2hex(byte[] b) {
		StringBuffer hs = new StringBuffer();
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs.append("0").append(stmp);
			else
				hs.append(stmp);
		}
		return hs.toString().toUpperCase(Locale.ENGLISH);
	}

}
