package com.simonvn.tdtu.student.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * Created by Bichan on 6/27/2016.
 */
public class StringUtil {

    public static String getFrom(String mFrom) {
        if (mFrom.contains("<")) {
            return mFrom.substring(mFrom.indexOf("<") + 1, mFrom.indexOf(">"));
        }
        return mFrom;
    }

    public static String formatString(String a){
        Pattern inputSpace = Pattern.compile("^([^\\s]*)\\s", Pattern.MULTILINE);
        return inputSpace.matcher(a).replaceAll("");
    }

    public static boolean isNumber(String s){
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    /**
     * Lấy ngày đăng thông báo
     * @param thongBao tên thông báo
     * @return
     */
    public static String thongBaoGetDate(String thongBao){
        int ngoatSt = thongBao.lastIndexOf("(");
        int ngoatFn = thongBao.lastIndexOf(")");
        return thongBao.substring(ngoatSt + 1, ngoatFn);
    }

    public static String thongBaoFormat(String thongBao){
        int ngoatSt = thongBao.lastIndexOf("(");
        return thongBao.substring(0, ngoatSt);
    }

    public static boolean isThongBaoMoi(String thongBao){
        int ngoatFn = thongBao.lastIndexOf(")");
        String moi  = thongBao.substring(ngoatFn);
        return moi.indexOf("Mới") > 0;
    }

    /*public static String tinhTGBatDau(String a){
        String[] tietArray = a.split("");
        String tiet = "";
        for(int i = 1 ; i <= 16; i++){
            if(!tietArray[i].equals("-")){
                tiet = Integer.toString(i);
                break;
            }
        }
        for (int i = 0 ; i <= CaHoc.caHoc.length; i++){
            if(CaHoc.caHoc[i].getTen().equals(tiet)){
                return CaHoc.caHoc[i].getTgBatDau();
            }
        }
        return "";
    }


    public static String tinhTGKetThuc(String a){
        String[] tietArray = a.split("");
        String tiet = "";
        for(int i = 1 ; i <= 16; i++){
            if(!tietArray[i].equals("-")){
                tiet = Integer.toString(i);
            }
        }
        for (int i = 0 ; i < CaHoc.caHoc.length; i++){
            if(CaHoc.caHoc[i].getTen().equals(tiet)){
                return CaHoc.caHoc[i].getTgKetThuc();
            }
        }
        return "";
    }*/

    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
