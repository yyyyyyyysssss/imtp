package org.imtp.client.controller;

import javafx.scene.control.DialogPane;
import javafx.scene.control.TableCell;
import org.imtp.client.entity.EmoteEntity;
import org.imtp.client.event.EmojiEvent;

/**
 * @Description
 * @Author ys
 * @Date 2024/6/19 13:56
 */
public class ChatEmojiTableCell extends TableCell<EmoteEntity,String>{

    private DialogPane dialogPane;

    public ChatEmojiTableCell(DialogPane dialogPane){
        this.dialogPane = dialogPane;
        setOnMouseClicked(event -> {
            String text = getText();
            dialogPane.fireEvent(new EmojiEvent(EmojiEvent.SELECTED,text));
        });
    }

    @Override
    protected void updateItem(String s, boolean b) {
        super.updateItem(s, b);
        if (b || s ==null){
            setGraphic(null);
            setText(null);
        }else {
            setText(s);
        }
    }
}
