package org.imtp.desktop.event;

import javafx.event.Event;
import javafx.event.EventType;
import org.imtp.desktop.entity.SessionEntity;

/**
 * @Description
 * @Author ys
 * @Date 2024/5/24 17:59
 */
public class UserSessionEvent extends Event {

    private SessionEntity sessionEntity;

    public static final EventType<UserSessionEvent> ANY = new EventType<>(Event.ANY,"ANY");

    public static final EventType<UserSessionEvent> SEND_MESSAGE = new EventType<>(ANY,"SEND_MESSAGE");

    public UserSessionEvent(EventType<? extends Event> eventType,SessionEntity sessionEntity) {
        super(eventType);
        this.sessionEntity = sessionEntity;
    }

    public SessionEntity getSessionEntity() {
        return sessionEntity;
    }
}
