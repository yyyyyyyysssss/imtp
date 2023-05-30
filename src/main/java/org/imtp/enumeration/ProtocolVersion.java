package org.imtp.enumeration;

import lombok.Getter;

@Getter
public enum ProtocolVersion {

    IMTP01((byte) 1),
    UNKNOWN((byte)-1)
    ;
    private byte ver;

    ProtocolVersion(byte ver){
        this.ver=ver;
    }
}
