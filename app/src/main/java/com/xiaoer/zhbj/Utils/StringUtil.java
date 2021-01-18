package com.xiaoer.zhbj.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StringUtil {
    /**
     *  @describe 流转字符串
     */
    public static String stream2String(InputStream fileInputStream){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte [] buffer = new byte[1024];
        int len = -1;
        try {
            while(((len = fileInputStream.read(buffer))!=-1)) {
                baos.write(buffer,0,len);
            }
            return baos.toString();
        } catch (IOException e) {
                e.printStackTrace();
        }finally {
            try {
                if(fileInputStream!=null) fileInputStream.close();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
