

module client{
    requires common;
    requires lombok;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires com.fasterxml.jackson.databind;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires io.netty.all;
    requires io.netty.buffer;
    requires io.netty.codec;
    requires io.netty.common;
    requires io.netty.handler;
    requires io.netty.handler.proxy;
    requires io.netty.resolver;
    requires io.netty.resolver.dns;
    requires io.netty.transport;
    requires io.netty.transport.unix.common;
    requires io.netty.transport.rxtx;
    requires io.netty.transport.sctp;
    requires io.netty.transport.udt;
    requires io.netty.codec.dns;
    requires io.netty.codec.haproxy;
    requires io.netty.codec.http;
    requires io.netty.codec.http2;
    requires io.netty.codec.memcache;
    requires io.netty.codec.mqtt;
    requires io.netty.codec.redis;
    requires io.netty.codec.smtp;
    requires io.netty.codec.socks;
    requires io.netty.codec.stomp;
    requires com.gluonhq.emoji;
    requires com.gluonhq.richtextarea;
    requires okhttp3;
    requires org.bytedeco.javacv;
    requires java.desktop;

    exports org.imtp.desktop;
    exports org.imtp.desktop.component;
    exports org.imtp.desktop.constant;
    exports org.imtp.desktop.context;
    exports org.imtp.desktop.controller;
    exports org.imtp.desktop.entity;
    exports org.imtp.desktop.enums;
    exports org.imtp.desktop.event;
    exports org.imtp.desktop.handler;
    exports org.imtp.desktop.idwork;
    exports org.imtp.desktop.model;
    exports org.imtp.desktop.util;
    exports org.imtp.desktop.view;

    opens org.imtp.desktop.controller to javafx.fxml;
    opens org.imtp.desktop to javafx.fxml;
}