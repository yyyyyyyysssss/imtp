package org.imtp.common.enums;

import lombok.Getter;

@Getter
public enum LoginState {

    SUCCESS,
    FAIL,
    NOT_LOGIN,
    ;
    public static LoginState find(byte s){
        for (LoginState state : LoginState.values()){
            if((byte) state.ordinal() == s){
                return state;
            }
        }
        return null;
    }

}
