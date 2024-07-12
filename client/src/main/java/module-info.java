

module client{
    requires common;
    requires lombok;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires com.fasterxml.jackson.databind;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
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

    exports org.imtp.client;
    exports org.imtp.client.component;
    exports org.imtp.client.constant;
    exports org.imtp.client.context;
    exports org.imtp.client.controller;
    exports org.imtp.client.entity;
    exports org.imtp.client.enums;
    exports org.imtp.client.event;
    exports org.imtp.client.handler;
    exports org.imtp.client.idwork;
    exports org.imtp.client.model;
    exports org.imtp.client.util;
    exports org.imtp.client.view;

    opens org.imtp.client.controller to javafx.fxml;
    opens org.imtp.client to javafx.fxml;
}