package org.imtp.server.enums;

import lombok.Getter;

@Getter
public enum OfflineMsgEnum {
    PRIVATE_CHAT_TYPE(10,"私聊"),
    GROUP_CHAT_TYPE(20,"群聊"),

    WAIT_PUSH(0,"待推送"),
    PUSHED(1,"已推送"),

    ;
    private int type;
    private String desc;

    OfflineMsgEnum(int type, String desc){
        this.type = type;
        this.desc = desc;
    }

}
