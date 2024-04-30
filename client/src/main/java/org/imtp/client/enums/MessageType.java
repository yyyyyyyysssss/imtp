package org.imtp.client.enums;

import lombok.Getter;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/30 9:24
 */
@Getter
public enum MessageType {
    PRIVATE(10),
    GROUP(20),
    ;
    private int type;

    MessageType(int type){
        this.type = type;
    }

}
