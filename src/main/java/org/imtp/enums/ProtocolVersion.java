package org.imtp.enums;

import lombok.Getter;

@Getter
public enum ProtocolVersion {

    VER1((byte) 0x01),
    ;
    private final byte ver;

    ProtocolVersion(byte ver){
        this.ver=ver;
    }

    public static ProtocolVersion find(byte b){
        for (ProtocolVersion ver : ProtocolVersion.values()){
            if(ver.getVer() == b){
                return ver;
            }
        }
        return null;
    }

}
