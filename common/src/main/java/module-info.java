

module common {
    requires static lombok;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires com.fasterxml.jackson.databind;
    requires io.netty.all;
    requires io.netty.buffer;
    requires io.netty.codec;
    requires io.netty.common;
    requires io.netty.handler;
    requires io.netty.transport;

    exports org.imtp.common.codec;
    exports org.imtp.common.component;
    exports org.imtp.common.enums;
    exports org.imtp.common.packet;
    exports org.imtp.common.packet.base;
    exports org.imtp.common.packet.body;
    exports org.imtp.common.utils;
    exports org.imtp.common.idwork;
}