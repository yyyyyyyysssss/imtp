package org.imtp.common.idwork;

import java.util.Random;

/**
 * @Description 使用使用随机数生成工作id
 * @Author ys
 * @Date 2024/4/28 11:13
 */
public class RandomWorkIdService implements WorkIdService {

    @Override
    public long getWorkId() {
        return new Random().nextLong(1024);
    }
}
