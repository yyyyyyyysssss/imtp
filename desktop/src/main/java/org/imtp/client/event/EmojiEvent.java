package org.imtp.client.event;

import com.gluonhq.emoji.Emoji;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * @Description
 * @Author ys
 * @Date 2024/6/19 18:05
 */
public class EmojiEvent extends Event {

    private Emoji emoji;

    public static final EventType<EmojiEvent> SELECTED = new EventType<>(ANY,"SELECTED");

    public EmojiEvent(EventType<? extends Event> eventType,Emoji emoji) {
        super(eventType);
        this.emoji = emoji;
    }

    public Emoji getEmoji() {
        return emoji;
    }
}
