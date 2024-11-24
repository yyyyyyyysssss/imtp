package org.imtp.client.controller;

import com.gluonhq.emoji.Emoji;
import com.gluonhq.emoji.util.TextUtils;
import javafx.scene.Node;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableCell;
import org.imtp.client.event.EmojiEvent;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/6/19 13:56
 */
public class ChatEmojiTableCell extends TableCell<List<Emoji>, Emoji>{

    private DialogPane dialogPane;

    public ChatEmojiTableCell(DialogPane dialogPane){
        this.dialogPane = dialogPane;
        setOnMouseClicked(event -> {
            Emoji emoji = getItem();
            if (emoji != null){
                dialogPane.fireEvent(new EmojiEvent(EmojiEvent.SELECTED,emoji));
            }
        });
    }

    @Override
    protected void updateItem(Emoji emoji, boolean b) {
        super.updateItem(emoji, b);
        if (b || emoji ==null){
            setGraphic(null);
            setText(null);
        }else {
            List<Node> nodes = TextUtils.convertToTextAndImageNodes(emoji.character(),30);
            if (nodes != null && !nodes.isEmpty()){
                setGraphic(nodes.getFirst());
            }
        }
    }
}
