package com.tg.cloudmanagement.util;

import java.util.Comparator;

import com.tg.cloudmanagement.object.SlideItemObj;

import android.text.TextUtils;


public class PinyinComparator implements Comparator<Object> {
	@Override
	public int compare(Object o1, Object o2) {
		String name1 = ((SlideItemObj) o1).name;
		String name2 = ((SlideItemObj) o2).name;
		if(TextUtils.isEmpty(name1)){
			return -1;
		}
		if(TextUtils.isEmpty(name2)){
			return 1;
		}
		PinYinObj obj1; 
		PinYinObj obj2;
		int len1 = name1.length();
		int len2 = name2.length();
		for(int i = 0 ; i < len1; i ++){
			if(i >= len2){
				return 1;
			}
			obj1 = PinYin.getPinYinObj(name1.substring(i, i+1));
			obj2 = PinYin.getPinYinObj(name2.substring(i, i+1));
			if(i == 0){
				String str1 = obj1.hanziPinyin.substring(0, 1);//首字母
				String str2 = obj2.hanziPinyin.substring(0, 1);
				char ch1 = str1.charAt(0);
				char ch2 = str2.charAt(0);
				boolean b1 = isZiMu(ch1);
				boolean b2 = isZiMu(ch2);
				if(b1 == b2 ){
					int r = str1.compareToIgnoreCase(str2);
					if(r != 0){
						return r;
					}else{
						if(obj1.isSource == obj2.isSource){
							int r1 = obj1.hanziPinyin.compareTo(obj2.hanziPinyin);
							if(r1 != 0){
								return r1;
							}else{
								continue;
							}
						}else{
							if(obj1.isSource){
								return -1;
							}else{
								return 1;
							}
						}
					}
				}else{
					if(b1){
						return -1;
					}else{
						return 1;
					}
				}
				
			}else{
				if(obj1.isSource == obj2.isSource){
					int result = obj1.hanziPinyin.compareTo(obj2.hanziPinyin);
					if(result != 0){
						return result;
					}else{
						continue;
					}
				}else{
					if(obj1.isSource){
						return -1;
					}else{
						return 1;
					}
				}
			}
			
		}
		if(len1 < len2){
			return -1;
		}
		return 0;
	}
	
	public boolean isZiMu(char ch){
		if(ch>= 'A' &&  ch <= 'Z' || ch >='a'&& ch <= 'z' ){
			return true;
		}
		return false;
	}
}