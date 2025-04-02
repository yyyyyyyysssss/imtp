package org.imtp.web.config.idwork;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Description
 * @Author ys
 * @Date 2025/4/2 10:08
 */
@SpringBootTest
public class IdGenTest {

    @Test
    void testGenId(){
        for (int i = 0; i < 100; i++) {
            System.out.println(IdGen.genId());
        }
    }

}
