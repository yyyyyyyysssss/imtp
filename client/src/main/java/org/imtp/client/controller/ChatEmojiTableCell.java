package org.imtp.client.controller;

import javafx.scene.control.DialogPane;
import javafx.scene.control.TableCell;
import org.imtp.client.entity.Emoji;
import org.imtp.client.entity.EmojiRowEntity;
import org.imtp.client.event.EmojiEvent;

/**
 * @Description
 * @Author ys
 * @Date 2024/6/19 13:56
 */
public class ChatEmojiTableCell extends TableCell<EmojiRowEntity, Emoji>{

    private DialogPane dialogPane;

    public ChatEmojiTableCell(DialogPane dialogPane){
        this.dialogPane = dialogPane;
        setOnMouseClicked(event -> {
            String text = getText();
            dialogPane.fireEvent(new EmojiEvent(EmojiEvent.SELECTED,text));
        });
    }

    @Override
    protected void updateItem(Emoji emoji, boolean b) {
        super.updateItem(emoji, b);
        if (b || emoji ==null){
            setGraphic(null);
            setText(null);
        }else {
            setText(emoji.getUnicode());
        }
    }
}
