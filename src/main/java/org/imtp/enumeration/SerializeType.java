package org.imtp.enumeration;

import lombok.Getter;

@Getter
public enum SerializeType {

    JSON((byte) 1),
    PROTOBUF((byte) 2);
    private byte type;

    SerializeType(byte type) {
        this.type = type;
    }

}
