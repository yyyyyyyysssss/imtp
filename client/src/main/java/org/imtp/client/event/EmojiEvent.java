package org.imtp.client.event;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * @Description
 * @Author ys
 * @Date 2024/6/19 18:05
 */
public class EmojiEvent extends Event {

    private String emojiValue;

    public static final EventType<EmojiEvent> SELECTED = new EventType<>(ANY,"SELECTED");

    public EmojiEvent(EventType<? extends Event> eventType,String emojiValue) {
        super(eventType);
        this.emojiValue = emojiValue;
    }

    public String getEmojiValue() {
        return emojiValue;
    }
}
