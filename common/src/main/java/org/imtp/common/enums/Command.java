package org.imtp.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Command {

    AUTHORIZATION_REQ((byte) 120,"授权请求"),
    AUTHORIZATION_RES((byte) -120,"授权响应"),

    TEXT_MESSAGE((byte) 1,"文本"),
    VOICE_MESSAGE((byte) 2,"语音"),
    MEME_MESSAGE((byte) 3,"表情包"),
    IMAGE_MESSAGE((byte) 4,"图片"),
    VIDEO_MESSAGE((byte) 5,"视频"),
    FILE_MESSAGE((byte) 6,"文件"),
    VOICE_CALL_MESSAGE((byte) 7,"语音通话"),
    VIDEO_CALL_MESSAGE((byte) 8,"视频通话"),

    SIGNALING_PRE_OFFER((byte) 40,"webrtc预提案"),
    SIGNALING_OFFER((byte) 41,"webrtc信令提案"),
    SIGNALING_ANSWER((byte) 42,"webrtc信令应答"),
    SIGNALING_CANDIDATE((byte) 43,"webrtc信令候选信息"),
    SIGNALING_BUSY((byte) 44,"webrtc信令忙线"),
    SIGNALING_CLOSE((byte) 49,"webrtc信令关闭"),

    MSG_RES((byte) -1, "通用消息响应"),

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

    @JsonValue
    public byte getCmdCode() {
        return cmdCode;
    }
}
