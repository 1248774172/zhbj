package com.xiaoer.zhbj.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {
    public static String encoder(String pwd){
        try {
            //加盐
            pwd = pwd + "xiaoer";

            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(pwd.getBytes());
            StringBuilder stringBuffer = new StringBuilder();
            
            for (byte b:
            digest){
                int i = b & 0xff;
                String s = Integer.toHexString(i);
                if(s.length()<2){
                    s = "0"+s;
                }
                stringBuffer.append(s);
            }
            return stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}
