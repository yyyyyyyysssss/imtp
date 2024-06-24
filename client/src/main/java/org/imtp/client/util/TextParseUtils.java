package org.imtp.client.util;

import org.imtp.common.packet.base.Packet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description
 * @Author ys
 * @Date 2024/6/24 17:31
 */
public class TextParseUtils {

    private static final String imgPattern = "((file:/.*?\\.(jpg|jpeg|gif|png)).*?)";

    public static void main(String[] args) {
        Pattern pattern = Pattern.compile(imgPattern);
        Matcher matcher = pattern.matcher("12313131file:/C:/Users/86188/Desktop/工作/loading.gi1fqwewqeqeqeqwefile:/C:/Users/86188/Desktop/工作/loading.gifqwewqeqe");
        while (matcher.find()){
            System.out.println(matcher.group(1));
        }
//        if (matcher.find()){
//            System.out.println(matcher.group(1));
//        }else {
//            System.out.println("2");
//        }
    }

}
