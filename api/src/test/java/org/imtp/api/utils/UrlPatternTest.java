package org.imtp.api.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description
 * @Author ys
 * @Date 2025/5/18 11:57
 */
@Slf4j
public class UrlPatternTest {

    @Test
    public void testUrlPattern(){
        String regex = "^/[\\w\\-./{}:+*\\[\\]()\\\\]+$";
        String test = "/system/authority/{id:\\d+}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(test);
        if(matcher.matches()){
            log.info("✅ 合法规则");
        } else {
            log.info("❌ 非法规则");
        }
    }

}
