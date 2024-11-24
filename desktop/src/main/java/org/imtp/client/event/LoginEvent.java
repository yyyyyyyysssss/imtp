package org.imtp.client.event;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * @Description
 * @Author ys
 * @Date 2024/5/6 14:20
 */
public class LoginEvent extends Event {

    public static final EventType<LoginEvent> ANY = new EventType<>(Event.ANY,"ANY");

    public static final EventType<LoginEvent> LOGIN_SUCCEEDED = new EventType<>(ANY,"LOGIN_SUCCEEDED");

    public static final EventType<LoginEvent> LOGIN_FAILED = new EventType<>(ANY,"LOGIN_FAILED");

    public LoginEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }
}
