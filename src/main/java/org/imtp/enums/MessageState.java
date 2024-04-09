package org.imtp.enums;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/8 15:09
 */
public enum MessageState {

    DELIVERED;

    public static MessageState find(byte s){
        for (MessageState state : MessageState.values()){
            if((byte) state.ordinal() == s){
                return state;
            }
        }
        return null;
    }

}
