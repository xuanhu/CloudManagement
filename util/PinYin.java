package com.tg.cloudmanagement.util;

import java.util.ArrayList;
import java.util.Locale;

import com.tg.cloudmanagement.util.HanziToPinyin.Token;


public class PinYin {
	//汉字返回拼音，字母原样返回，都转换为小写
	public static String getPinYin(String input) {
		ArrayList<Token> tokens = HanziToPinyin.getInstance().get(input);
		StringBuilder sb = new StringBuilder();
		if (tokens != null && tokens.size() > 0) {
			for (Token token : tokens) {
				if (Token.PINYIN == token.type) {
					sb.append(token.target);
				} else { 
					sb.append(token.source);
				} 
			}
		}
		return sb.toString();
	}
	
	public static PinYinObj getPinYinObj(String input) {
		PinYinObj obj = new PinYinObj();
		ArrayList<Token> tokens = HanziToPinyin.getInstance().get(input);
		if (tokens != null && tokens.size() > 0) {
			Token token = tokens.get(0);
			if (Token.PINYIN == token.type) {
				obj.hanziPinyin = token.target.toLowerCase(Locale.US);
				obj.isSource = false;
			} else {
				obj.hanziPinyin = token.source;
				obj.isSource = true;
			}
			
		}else{
			obj.hanziPinyin = "";
		}
		return obj;
	}
	
	public static String getInitialsOfHanZi(String input) {
		String str = "";
		ArrayList<Token> tokens = HanziToPinyin.getInstance().get(input);
		if (tokens != null && tokens.size() > 0) {
			Token token  = tokens.get(0);
			if (Token.PINYIN == token.type) {
				str = token.target.substring(0, 1);
			} else {
				str = token.source.substring(0, 1);
			}
		}
		return str;
	}
}
