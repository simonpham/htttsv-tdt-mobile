package com.simonvn.tdtu.student.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sun.mail.imap.IMAPFolder;

import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Calendar;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;

import com.simonvn.tdtu.student.models.TietHoc;
import com.simonvn.tdtu.student.models.email.EmailAttachment;
import com.simonvn.tdtu.student.models.email.EmailItem;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by Bichan on 7/15/2016.
 */
public class Util {
    public static final int XEPLOAI_GOI = 0;
    public static final int XEPLOAI_KHA = 1;
    public static final int XEPLOAI_TB = 2;
    public static final int XEPLOAI_YEU = 3;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String showCalendar(Calendar c) {
        int year = c.get(Calendar.YEAR);

        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        int millis = c.get(Calendar.MILLISECOND);
        month++;
        return "" + day + " tháng " + month + ", " + year;
    }

    public static String getThuCalendar(Calendar c){
        String thu = "";
        int thuInt = c.get(Calendar.DAY_OF_WEEK);
        if(thuInt == 1)
            thu = "Chủ nhật";
        else
            thu = "Thứ " + thuInt;
        return thu;
    }

    public static int xepLoaiDiem(String diem){
        try {
            double n = Double.parseDouble(diem);
            if(n >= 8 && n <= 10){
                return XEPLOAI_GOI;
            }else if (n >= 6.5 && n < 8){
                return XEPLOAI_KHA;
            }else
                return XEPLOAI_TB;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static String xeLoaiDiemColor(String diem){
        try {
            double n = Double.parseDouble(diem);
            if(n >= 8 && n <= 10){
                return  "#88C90D";
            }else if (n >= 6.5 && n < 8) {
                return "#5E78B7";
            }else if (n >= 5 && n < 6.5){
                return "#ECC83B";
            }else
                return "#CB341F";
        } catch (NumberFormatException e) {
            switch (diem){
                case "F":
                    return "#CB341F";
                case "M":
                    return  "#88C90D";
            }
        }
        return "#ECC83B";
    }

    public static String tinhCaHoc(String a){
        String[] tietArray = a.split("");
        String tiet = "";
        for(int i = 1 ; i <= 16; i++){
            if(!tietArray[i].equals("-")){
                tiet = Integer.toString(i);
                break;
            }
        }
        int tietInt = Integer.parseInt(tiet);
        switch (tietInt){
            case 1:
            case 2:
            case 3:
                return "1";
            case 4:
            case 5:
            case 6:
                return "2";
            case 7:
            case 8:
            case 9:
                return "3";
            case 10:
            case 11:
            case 12:
                return "4";
            case 13:
            case 14:
            case 15:
            case 16:
                return "5";
        }
        return "5";
    }

    public static String tinhTGBatDau(String a){
        String[] tietArray = a.split("");
        String tiet = "";
        for(int i = 1 ; i <= 16; i++){
            if(!tietArray[i].equals("-")){
                tiet = Integer.toString(i);
                break;
            }
        }
        for (int i = 0; i <= TietHoc.tietHocs.length; i++){
            if(TietHoc.tietHocs[i].getTen().equals(tiet)){
                return TietHoc.tietHocs[i].getTgBatDau();
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
        for (int i = 0 ; i < TietHoc.tietHocs.length; i++){
            if(TietHoc.tietHocs[i].getTen().equals(tiet)){
                return TietHoc.tietHocs[i].getTgKetThuc();
            }
        }
        return "";
    }


    public static String formatFileSize(long size) {
        String hrSize = null;

        double b = size;
        double k = size/1024.0;
        double m = ((size/1024.0)/1024.0);
        double g = (((size/1024.0)/1024.0)/1024.0);
        double t = ((((size/1024.0)/1024.0)/1024.0)/1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if ( t>1 ) {
            hrSize = dec.format(t).concat(" TB");
        } else if ( g>1 ) {
            hrSize = dec.format(g).concat(" GB");
        } else if ( m>1 ) {
            hrSize = dec.format(m).concat(" MB");
        } else if ( k>1 ) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" Bytes");
        }

        return hrSize;
    }

    public static EmailItem createEmailItem(Message message, IMAPFolder imapFolder){
        EmailItem emailItem = null;
        try{
            emailItem = new EmailItem();
            emailItem = new EmailItem();
            emailItem.setmId(imapFolder.getUID(message));
            Address[] froms = message.getFrom();
            String email = froms == null ? null : ((InternetAddress) froms[0]).getAddress();
            String personal = froms == null ? null : ((InternetAddress) froms[0]).getPersonal();
            emailItem.setmFrom(email);
            emailItem.setmPersonal(personal);
            emailItem.setmSubject(message.getSubject());
            emailItem.setmSentDate(message.getSentDate().getTime());

            if(message.isSet(Flags.Flag.SEEN)){
                emailItem.setNew(false);
            }else{
                emailItem.setNew(true);
            }

            dumpPart(message, emailItem, 0);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return emailItem;
    }


    public static void dumpPart(Part p, EmailItem emailItem, int level) throws Exception {
        if (p.isMimeType("text/plain")) {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("</br>");
            }
            br.close();
            emailItem.setmBody(sb.toString());
        } else if(p.isMimeType("text/html")){
            emailItem.setmBody(Jsoup.parse(p.getContent().toString()).toString());
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            level++;
            int count = mp.getCount();
            for (int i = 0; i < count; i++)
                dumpPart(mp.getBodyPart(i), emailItem, level);
            level--;
        } else if (p.isMimeType("message/rfc822")) {
            level++;
            dumpPart((Part)p.getContent(), emailItem, level);
            level--;
        }

        if (level != 0 && p instanceof MimeBodyPart && !p.isMimeType("multipart/*")) {
            String disp = p.getDisposition();
            if (disp == null || disp.equalsIgnoreCase(Part.ATTACHMENT)) {
                String filename = p.getFileName();
                if (filename != null) {
                    EmailAttachment emailAttachment = new EmailAttachment();
                    emailAttachment.setId(emailItem.getmId() + "-" + filename);
                    emailAttachment.setName(filename);
                    emailAttachment.setType(p.getContentType().split("; ")[0].toLowerCase());
                    emailItem.getEmailAttachments().add(emailAttachment);
                }
            }
        }
    }
}
