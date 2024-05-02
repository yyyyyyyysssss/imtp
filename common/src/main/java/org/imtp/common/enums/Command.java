package org.imtp.common.enums;

import lombok.Getter;

@Getter
public enum Command {

    MSG_RES((byte) -1, "通用消息响应"),

    LOGIN_REQ((byte) 126,"登录请求"),
    LOGIN_RES((byte) -126,"登录响应"),

    FRIENDSHIP_REQ((byte)125,"用户好友关系信息拉取"),
    FRIENDSHIP_RES((byte)-125,"用户好友关系信息响应"),

    GROUP_RELATIONSHIP_REQ((byte)124,"用户群组信息拉取"),
    GROUP_RELATIONSHIP_RES((byte)-124,"用户群组信息响应"),

    OFFLINE_MSG_REQ((byte)123,"离线消息拉取"),
    OFFLINE_MSG_RES((byte)-123,"离线消息响应"),

    TEXT_MESSAGE((byte) 10,"文本消息"),

    PRIVATE_CHAT_MSG((byte) 1, "私聊"),
    GROUP_CHAT_MSG((byte) 2, "群聊"),


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
