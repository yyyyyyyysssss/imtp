package org.imtp.client.entity;

import javafx.beans.property.SimpleStringProperty;

/**
 * @Description
 * @Author ys
 * @Date 2024/6/18 15:22
 */
public class EmoteEntity {

    private final SimpleStringProperty[] emotes;

    public EmoteEntity(String[] emotes) {
        this.emotes = new SimpleStringProperty[emotes.length];
        for (int i = 0; i < emotes.length; i++) {
            this.emotes[i] = new SimpleStringProperty(emotes[i]);
        }
    }

    public SimpleStringProperty getEmoteAt(int index) {
        return emotes[index];
    }

}
