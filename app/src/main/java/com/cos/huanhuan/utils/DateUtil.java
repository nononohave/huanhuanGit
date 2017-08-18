package com.cos.huanhuan.utils;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtil {
	private final static SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");

	private final static SimpleDateFormat sdfDay = new SimpleDateFormat(
			"yyyy-MM-dd");
	
	private final static SimpleDateFormat sdfDays = new SimpleDateFormat(
	"yyyyMMdd");

	private final static SimpleDateFormat sdfTime = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static final String formatStr1 = "yyyy-MM-dd HH:mm:ss";
	public static final String formatStr2 = "yyyy-MM-dd";
	public static final String formatStr3 = "yyyyMMdd";
	public static final String formatStr4 = "yyyy-MM";
	public static final String formatStr5 = "yyyyMMddHHmmss";
	public static final String formatStr6 = "yyyy.MM.dd";
	public static final String formatStr7 = "yyyy-MM-dd HH:mm";

	/**
	 * 获取YYYY格式
	 * 
	 * @return
	 */
	public static String getYear() {
		return sdfYear.format(new Date());
	}

	/**
	 * 获取YYYY-MM-DD格式
	 * 
	 * @return
	 */
	public static String getDay() {
		return sdfDay.format(new Date());
	}

	/**
	 * 获取当前时间 yyyyMMddHHmmss
	 * @return String
	 */
	public static String getCurrTime() {
		Date now = new Date();
		SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String s = outFormat.format(now);
		return s;
	}

	public static Calendar getCalendar(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	/**
	 * 获取YYYYMMDD格式
	 * 
	 * @return
	 */
	public static String getDays(){
		return sdfDays.format(new Date());
	}

	/**
	 * 获取YYYY-MM-DD HH:mm:ss格式
	 * 
	 * @return
	 */
	public static String getTime() {
		return sdfTime.format(new Date());
	}

	/**
	* @Title: compareDate
	* @Description: TODO(日期比较，如果s>=e 返回true 否则返回false)
	* @param s
	* @param e
	* @return boolean  
	* @throws
	* @author luguosui
	 */
	public static boolean compareDate(String s, String e) {
		if(fomatDate(s)==null||fomatDate(e)==null){
			return false;
		}
		return fomatDate(s).getTime() >=fomatDate(e).getTime();
	}

	/**
	 * 格式化日期
	 * 
	 * @return
	 */
	public static Date fomatDate(String date) {
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return fmt.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 校验日期是否合法
	 * 
	 * @return
	 */
	public static boolean isValidDate(String s) {
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		try {
			fmt.parse(s);
			return true;
		} catch (Exception e) {
			// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
			return false;
		}
	}
	public static int getDiffYear(String startTime,String endTime) {
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		try {
			long aa=0;
			int years=(int) (((fmt.parse(endTime).getTime()-fmt.parse(startTime).getTime())/ (1000 * 60 * 60 * 24))/365);
			return years;
		} catch (Exception e) {
			// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
			return 0;
		}
	}
	  /**
     * <li>功能描述：时间相减得到天数
     * @param beginDateStr
     * @param endDateStr
     * @return
     * long 
     * @author Administrator
     */
    public static long getDaySub(String beginDateStr,String endDateStr){
        long day=0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = null;
        Date endDate = null;
        
            try {
				beginDate = format.parse(beginDateStr);
				endDate= format.parse(endDateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
            day=(endDate.getTime()-beginDate.getTime())/(24*60*60*1000);
            //System.out.println("相隔的天数="+day);
      
        return day;
    }

	/**
	 * 获取上一个月的今天
	 * @param date
	 * @return
	 * @throws ParseException
     */
	public static Date getDateOfLastMonth(Date date) throws ParseException {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);
		return c.getTime();
	}

	/**
     * 得到n天之后的日期
     * @param days
     * @return
     */
    public static String getAfterDayDate(String days) {
    	int daysInt = Integer.parseInt(days);
    	
        Calendar canlendar = Calendar.getInstance(); // java.util包
        canlendar.add(Calendar.DATE, daysInt); // 日期减 如果不够减会将月变动
        Date date = canlendar.getTime();
        
        SimpleDateFormat sdfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdfd.format(date);
        
        return dateStr;
    }
    
    /**
     * 得到n天之后是周几
     * @param days
     * @return
     */
    public static String getAfterDayWeek(String days) {
    	int daysInt = Integer.parseInt(days);
    	
        Calendar canlendar = Calendar.getInstance(); // java.util包
        canlendar.add(Calendar.DATE, daysInt); // 日期减 如果不够减会将月变动
        Date date = canlendar.getTime();
        
        SimpleDateFormat sdf = new SimpleDateFormat("E");
        String dateStr = sdf.format(date);
        
        return dateStr;
    }


	public static final String getDateTimeDir(Date date) {
		return DateUtil.getDateFormat(date, "yyyy") + File.separator + DateUtil.getDateFormat(date, "MM")
				+ File.separator + DateUtil.getDateFormat(date, "dd");
	}

	/**
	 * 返回日期字符串："yyyy-MM-dd HH:mm:ss" 格式。
	 *
	 * @param date
	 * @return
	 */
	public static final String getDateTime(Date date) {
		if (date == null) return "";
		DateFormat ymdhmsFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return ymdhmsFormat.format(date);
	}

	/**
	 * 两个时间之间相差距离多少天
	 *
	 * @param
	 * @return 相差天数
	 */
	public static long getDistanceDays(String str1, String str2) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date one;
		Date two;
		long days = 0;
		try {
			one = df.parse(str1);
			two = df.parse(str2);
			long time1 = one.getTime();
			long time2 = two.getTime();
			long diff;
			if (time1 < time2) {
				diff = time2 - time1;
			} else {
				diff = time1 - time2;
			}
			days = diff / (1000 * 60 * 60 * 24);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return days;
	}

	/**
	 * 距离今天相差多少天
	 *
	 * @2012-7-26
	 * @author zhuhuipei
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static long getDistanceDaysTONow(String str) throws Exception {
		Date now = new Date();
		String nowstr = getDateFormat(now, formatStr2);
		return getDistanceDays(str, nowstr);
	}

	public static final String getDateDay(Date date) {
		if (date == null) {
			return "";
		}
		DateFormat ymdhmsFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return ymdhmsFormat.format(date);
	}

	/**
	 * 根据指定的格式，返回指定日期的对应格式
	 *
	 * @param date
	 * @param formatStr 可以是"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm","yyyy-MM-dd HH","yyyy-MM-dd"等等
	 * @return
	 */
	public static String getDateFormat(Date date, String formatStr) {
		if (date == null || formatStr == null) return "";
		DateFormat ymdFormat = new SimpleDateFormat(formatStr);
		String tmpStr = ymdFormat.format(date).toString();
		if (tmpStr == null) return "";
		return tmpStr;
	}

	/**
	 * 时间比较
	 *
	 * @param
	 * @throws ParseException
	 */

	public static boolean isDateBefore(String date1, String date2) {
		try {
			DateFormat df = DateFormat.getDateTimeInstance();
			return df.parse(date1).before(df.parse(date2));
		} catch (ParseException e) {
			System.out.print("[SYS] " + e.getMessage());
			return false;
		}
	}

	/**
	 * 字符传化成时间类型
	 *
	 * @2012-7-19
	 * @author zhuhuipei
	 * @param ds
	 * @return
	 * @throws ParseException
	 */
	public static Date strintToDatetime(String ds) throws ParseException {
		if (ds == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = sdf.parse(ds);
		return d;
	}

	/**
	 * 字符传化成时间类型
	 *
	 * @2012-7-19
	 * @author zhuhuipei
	 * @param ds
	 * @return
	 * @throws ParseException
	 */
	public static Date strintToDatetime(String ds, String DateFormat) throws ParseException {
		if (AppStringUtils.isEmpty(ds)) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat);
		Date d = sdf.parse(ds);
		return d;
	}

	/**
	 * 时间向前推
	 *
	 * @param days
	 * @return
	 */
	public static String getDateAgo(int days) {
		SimpleDateFormat sd = new SimpleDateFormat(DateUtil.formatStr2);
		Calendar calendar = Calendar.getInstance();
		int i = calendar.get(Calendar.DAY_OF_YEAR);
		i = i - days;
		calendar.set(Calendar.DAY_OF_YEAR, i);
		return sd.format(calendar.getTime());
	}
	/**
	 * 时间向前推
	 *
	 * @param days
	 * @return
	 */
	public static String getDateAgo1(int days) {
		SimpleDateFormat sd = new SimpleDateFormat(DateUtil.formatStr2);
		Calendar calendar = Calendar.getInstance();
		int i = calendar.get(Calendar.DAY_OF_YEAR);
		i = i - days;
		calendar.set(Calendar.DAY_OF_YEAR, i);
		return sd.format(calendar.getTime())+" 00:00:00";
	}

	/**
	 * 时间向前推
	 *
	 * @param days
	 * @return
	 */
	public static Date getDateAgoDate(int days, String format) {
		SimpleDateFormat sd = new SimpleDateFormat(format);
		Calendar calendar = Calendar.getInstance();
		int i = calendar.get(Calendar.DAY_OF_YEAR);
		i = i - days;
		calendar.set(Calendar.DAY_OF_YEAR, i);
		Date date = null;
		try {
			date = strintToDatetime(sd.format(calendar.getTime()), format);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 时间向前推
	 *
	 * @param days
	 * @return
	 */
	public static String getDateAgo(int days, String format) {
		SimpleDateFormat sd = new SimpleDateFormat(format);
		Calendar calendar = Calendar.getInstance();
		int i = calendar.get(Calendar.DAY_OF_YEAR);
		i = i - days;
		calendar.set(Calendar.DAY_OF_YEAR, i);
		return sd.format(calendar.getTime());
	}

	/**
	 * 时间推移
	 *
	 * @param
	 * @return
	 */
	public static String getHourLater(int hour, String format) {
		SimpleDateFormat sd = new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, hour);
		return sd.format(cal.getTime());
	}

	/**
	 * 时间推移
	 *
	 * @param
	 * @return
	 */
	public static Date getHourLaterDate(int hour) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, hour);
		return cal.getTime();
	}

	/**
	 * 获取当前周 周一日期 按周一至周日 是一个周期计算。 例如：2013-11-18至2013-11-24
	 *
	 * @autor lijn 2013-11-22
	 * @return
	 */
	public static Date getMonDayCurrentWeek() {
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return cal.getTime();
	}

	public static Date getMonDayLastWeek() {
		return addToDay(getMonDayCurrentWeek(), -7);
	}

	/**
	 * 获取当前周 周日日期 按周一至周日 是一个周期计算。例如：2013-11-18至2013-11-24
	 *
	 * @autor lijn 2013-11-22
	 * @return
	 */
	public static Date getSunDayCurrentWeek() {
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.add(Calendar.DAY_OF_MONTH, 6);
		return cal.getTime();
	}

	public static Date getSunDayLastWeek() {
		return addToDay(getSunDayCurrentWeek(), -7);
	}

	/**
	 * 获得当前日期 + N个天 之后的日期
	 *
	 * @autor lijn 2013-11-22
	 * @param oldDate
	 * @param n
	 * @return
	 */
	public static Date addToDay(Date oldDate, int n) {
		Date newDate = null;
		Calendar calOld = Calendar.getInstance();
		calOld.setTime(oldDate);
		int day = calOld.get(Calendar.DAY_OF_MONTH);
		Calendar calNew = Calendar.getInstance();
		calNew.setTime(oldDate);
		calNew.set(Calendar.DAY_OF_MONTH, n + day);
		newDate = calNew.getTime();
		return newDate;
	}

	/**
	 * 计算两个日期之间相差的天数
	 *
	 * @autor lijn 2013-11-25
	 * @param date1 较小的时间
	 * @param date2 较大的时间
	 * @return 相差天数
	 */
	public static int getBetweenDays(Date date1, Date date2) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date1 = sdf.parse(sdf.format(date1));
			date2 = sdf.parse(sdf.format(date2));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date1);
		long time1 = cal.getTimeInMillis();
		cal.setTime(date2);
		long time2 = cal.getTimeInMillis();

		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return new BigDecimal(String.valueOf(between_days)).abs().intValue();
	}

	/**
	 * 计算日期间相差的秒数
	 *
	 * @author shihl 2014-3-5
	 * @param startDate 起始时间
	 * @param endDate 结束时间
	 * @return
	 */
	public static int getBetweenSeconds(Date startDate, Date endDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		long startTime = cal.getTimeInMillis();
		cal.setTime(endDate);
		long endTime = cal.getTimeInMillis();

		long betweenMillionSeconds = endTime - startTime;

		int betweenSeconds = BigDecimal.valueOf(betweenMillionSeconds).divide(new BigDecimal(1000),
				BigDecimal.ROUND_HALF_DOWN).intValue();

		return betweenSeconds;
	}

	/**
	 * 获取当前系统时间
	 *
	 * @author wangxm 2013年11月4日
	 * @return
	 */
	public static Date getSystemDate() {
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		return curDate;
	}

	/**
	 * 查询上周周一
	 *
	 * @author wangxm 2013年11月21日
	 * @return
	 */
	public static Date getMondayOfLastWeek() {
		Calendar c = Calendar.getInstance();
		int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (day_of_week == 0) day_of_week = 7;
		c.add(Calendar.DATE, -day_of_week + 1 - 7);
		return c.getTime();
	}

	/**
	 * 查询上周周日
	 *
	 * @author wangxm 2013年11月21日
	 * @return
	 */
	public static Date getSundayOfLastWeek() {
		Calendar c = Calendar.getInstance();
		int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (day_of_week == 0) day_of_week = 7;
		c.add(Calendar.DATE, -day_of_week);
		return c.getTime();
	}

	/**
	 * 时间比较
	 *
	 * @author wangxm 2013年11月4日
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isDateBefore(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			return false;
		}
		return date1.before(date2);
	}

	/**
	 * 两个时间之间相差距离多少天
	 *
	 * @author wangxm 2013年11月4日
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static long getDistanceDays(Date date1, Date date2) {
		long days = 0;
		long time1 = date1.getTime();
		long time2 = date2.getTime();
		long diff;
		if (time1 < time2) {
			diff = time2 - time1;
		} else {
			diff = time1 - time2;
		}
		days = diff / (1000 * 60 * 60 * 24);
		return days;
	}

	/**
	 * 两个时间之间相差距离多少分钟
	 *
	 * @author wangxm 2013年11月4日
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static long getDistanceMinutes(Date date1, Date date2) {
		long days = 0;
		long time1 = date1.getTime();
		long time2 = date2.getTime();
		long diff;
		if (time1 < time2) {
			diff = time2 - time1;
		} else {
			diff = time1 - time2;
		}
		days = diff / (1000 * 60);
		return days;
	}

	/**
	 * 格式化date。
	 *
	 * @param date
	 * @return
	 */
	public static final String formatDate(Date date, String format) {
		if (date == null) return "";
		DateFormat ymdhmsFormat = new SimpleDateFormat(format);
		return ymdhmsFormat.format(date);
	}

	public static final Date getFirstDayOfCurrentMonth() {
		Calendar c = Calendar.getInstance();
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.getActualMinimum(Calendar.DAY_OF_MONTH), 0, 0, 0);
		return c.getTime();
	}

	public static final Date getLastDayOfCurrentMonth() {
		Calendar c = Calendar.getInstance();
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.getActualMaximum(Calendar.DAY_OF_MONTH), 0, 0, 0);
		return c.getTime();
	}

	public static final Date getFirstDayOfLastMonth() {
		Calendar c = Calendar.getInstance();
		c.setTime(getFirstDayOfCurrentMonth());
		c.roll(Calendar.MONTH, -1);
		return c.getTime();
	}

	public static final Date getlastDayOfLastMonth() {
		Calendar c = Calendar.getInstance();
		c.setTime(getLastDayOfCurrentMonth());
		c.roll(Calendar.MONTH, -1);
		return c.getTime();
	}



	/**
	 * 字符传化成时间类型
	 * @date 2014年11月11日
	 * @author wanghehua
	 * @param ds
	 * @return
	 * @throws ParseException
	 * @since JDK 1.6
	 * @Description
	 */
	public static Date strintToDatetimeYMD(String ds) throws ParseException {
		if (ds == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date d = sdf.parse(ds);
		return d;
	}
	/**
	 * 字符传化成时间类型
	 * @date 2014年11月11日
	 * @author wanghehua
	 * @param ds
	 * @return
	 * @throws ParseException
	 * @since JDK 1.6
	 * @Description
	 */
	public static Date strintToDatetimeYMDHMS(String ds) throws ParseException {
		if (ds == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = sdf.parse(ds);
		return d;
	}


	/**
	 * 获取两个日期之间的天数
	 * @date 2016年08月15日
	 * @author yuxp
	 * @param
	 * @retur
	 * @throws ParseException
	 * @since JDK 1.6
	 * @Description
	 */
	public static int getDutyDays(Date startDate,Date endDate) {
		int result = 0;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		while (startDate.compareTo(endDate) <= 0) {
			if (startDate.getDay() != 6 && startDate.getDay() != 0)
				result++;
			startDate.setDate(startDate.getDate() + 1);
		}
		return result;
	}

	public static void main(String[] args) {
	}
	/**
	 * 凌晨
	 * @param date
	 * @flag 0 返回yyyy-MM-dd 00:00:00日期<br>
	 *       1 返回yyyy-MM-dd 23:59:59日期
	 * @return
	 * @throws ParseException
	 */
	public static Date weeHours(Date date, int flag) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		//时分秒（毫秒数）
		long millisecond = hour*60*60*1000 + minute*60*1000 + second*1000;
		//凌晨00:00:00
		cal.setTimeInMillis(cal.getTimeInMillis()-millisecond);

		if (flag == 0) {
			return cal.getTime();
		} else if (flag == 1) {
			//凌晨23:59:59
			cal.setTimeInMillis(cal.getTimeInMillis()+23*60*60*1000 + 59*60*1000 + 59*1000);
		}
		String day = sdf.format(cal.getTime());
		return strintToDatetimeYMDHMS(day);
	}

	/**
	 * 获取上一个月第一天的00:00:00
	 * @return
	 * @throws ParseException
	 */
	public static Date lmFirstDay() throws ParseException{
		Calendar cal_1=Calendar.getInstance();//获取当前日期
		cal_1.add(Calendar.MONTH, -1);//设置上个月
		cal_1.set(Calendar.DAY_OF_MONTH,1);//设置为1号,即上月月第一天
		return weeHours(cal_1.getTime(), 0);
	}

	/**
	 * 获取上一个月最后一天的23:59:59
	 * @return
	 * @throws ParseException
	 */
	public static Date lmLastDay() throws ParseException{
		Calendar cale = Calendar.getInstance();
		cale.set(Calendar.DAY_OF_MONTH,0);
		return weeHours(cale.getTime(), 1);
	}

	/**
	 * 获取当月的第一天的00:00:00
	 * @return
	 * @throws ParseException
	 */
	public static Date tmFirstDay() throws ParseException{
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH,1);
		return weeHours(c.getTime(), 0);
	}

	/**
	 * 获取当月的最后一天的23:59:59
	 * @return
	 * @throws ParseException
	 */
	public static Date tmLastDay() throws ParseException {
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		return weeHours(ca.getTime(), 1);
	}

	public static Date getUniqueDate(int days, Date dNow) throws ParseException {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(dNow);
		calendar.add(calendar.DATE, days);// 把日期往前或者往后推移多少天
		dNow = calendar.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat(formatStr1);
		String dateString = formatter.format(dNow);
		return strintToDatetimeYMDHMS(dateString);
	}
}
