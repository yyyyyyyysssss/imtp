package org.imtp.common.enums;

import lombok.Getter;

@Getter
public enum Command {


    LOGIN_REQ((byte) 126,"登录请求"),
    LOGIN_RES((byte) -126,"登录响应"),

    MSG_RES((byte) -1, "通用消息响应"),

    PRIVATE_MSG((byte) 1, "普通文本消息请求"),

    GROUP_CHAT_MSG((byte) 2, "普通文本消息请求"),


    HEARTBEAT_REQ((byte) 127, "心跳请求"),
    HEARTBEAT_RES((byte) -127, "心跳响应"),


    ;
    private byte cmdCode;

    private String desc;

    Command(byte cmdCode, String desc) {
        this.cmdCode = cmdCode;
        this.desc = desc;
    }

    public static Command find(byte c){
        for (Command command : Command.values()){
            if(command.getCmdCode() == c){
                return command;
            }
        }
        return null;
    }

}
