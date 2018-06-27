package com.niejingwei.filemanager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by niejingwei on 2018/6/25.
 * 时间转换的工具类
 */

public class DataTransfer {
    public static String TimestampToDate(long timeStamp,String format){
        SimpleDateFormat sdf=new SimpleDateFormat(format);//这个是你要转成后的时间的格式
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(timeStamp))));   // 时间戳转换成时间
        return sd;
    }
}
