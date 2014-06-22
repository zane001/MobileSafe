package com.zane001.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by zane001 on 2014/6/22.
 */
public class Md5Encoder {
    public static String encode(String password) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");  //获取摘要器
            byte[] result = digest.digest(password.getBytes()); //执行加密操作
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < result.length; i++) {   //将每个byte字节的数据转换成16进制的数据
                int number = result[i] & 0xff;  //先将byte转换成int
                String str = Integer.toHexString(number);   //再将int转换成16进制
                if(str.length() == 1) {
                    sb.append("0");
                    sb.append(str);
                } else {
                    sb.append(str);
                }
            }
            return sb.toString();   //将加密后的字符转换成字符串返回
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }

    }
}
