package com.tg.cloudmanagement.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
/**
 * 判断当前时间是否是今天
 * @author Administrator
 *
 */
public class IsTodayutil {
	private static ThreadLocal<SimpleDateFormat> DateLocal = new ThreadLocal<SimpleDateFormat>();
	/** 
     * 判断是否为今天(效率比较高) 
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以 
     * @return true今天 false不是 
     * @throws ParseException 
     */  
    public boolean IsToday(String day) throws ParseException {  
  
        Calendar pre = Calendar.getInstance();  
        Date predate = new Date(System.currentTimeMillis());  
        pre.setTime(predate);  
  
        Calendar cal = Calendar.getInstance();  
        Date date = getDateFormat().parse(day);  
        cal.setTime(date);  
  
        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {  
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)  
                    - pre.get(Calendar.DAY_OF_YEAR);  
  
            if (diffDay == 0) {  
                return true;  
            }  
        }  
        return false;  
    } 
    public static SimpleDateFormat getDateFormat() {  
        if (null == DateLocal.get()) {  
            DateLocal.set(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA));  
        }  
        return DateLocal.get();  
    } 
}
