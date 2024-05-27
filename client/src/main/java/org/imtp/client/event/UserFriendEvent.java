package org.imtp.client.event;

import javafx.event.Event;
import javafx.event.EventType;
import org.imtp.common.packet.body.UserFriendInfo;

/**
 * @Description
 * @Author ys
 * @Date 2024/5/24 17:59
 */
public class UserFriendEvent extends Event {

    private UserFriendInfo userFriendInfo;

    public static final EventType<UserFriendEvent> ANY = new EventType<>(Event.ANY,"ANY");

    public static final EventType<UserFriendEvent> SEND_MESSAGE = new EventType<>(ANY,"SEND_MESSAGE");

    public UserFriendEvent(EventType<? extends Event> eventType, UserFriendInfo userFriendInfo) {
        super(eventType);
        this.userFriendInfo = userFriendInfo;
    }

    public UserFriendInfo getUserFriendInfo() {
        return userFriendInfo;
    }
}
