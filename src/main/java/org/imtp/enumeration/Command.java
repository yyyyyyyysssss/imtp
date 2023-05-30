package org.imtp.enumeration;

import lombok.Getter;

@Getter
public enum Command {

    LOGIN_REQ((byte) 1, "登录请求"),
    LOGIN_RES((byte) -1, "登录响应"),

    SEND_REQ((byte) 2, "发送请求"),
    SEND_RES((byte) -2, "发送响应"),

    RECV_REQ((byte) 3, "接收请求"),
    RECV_RES((byte) -3, "接收响应"),


    HEARTBEAT_REQ((byte) 5, "客户端心跳请求"),
    HEARTBEAT_RES((byte) 6, "服务器端心跳响应"),


    ;
    private byte cmdCode;

    private String desc;

    Command(byte cmdCode, String desc) {
        this.cmdCode = cmdCode;
        this.desc = desc;
    }
}
