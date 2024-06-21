package org.imtp.client.controller;

import com.gluonhq.emoji.Emoji;
import com.gluonhq.emoji.EmojiData;
import com.gluonhq.emoji.util.TextUtils;
import javafx.scene.Node;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableCell;
import org.imtp.client.entity.EmojiEntity;
import org.imtp.client.entity.EmojiRowEntity;
import org.imtp.client.event.EmojiEvent;

import java.util.List;
import java.util.Optional;

/**
 * @Description
 * @Author ys
 * @Date 2024/6/19 13:56
 */
public class ChatEmojiTableCell extends TableCell<EmojiRowEntity, EmojiEntity>{

    private DialogPane dialogPane;

    public ChatEmojiTableCell(DialogPane dialogPane){
        this.dialogPane = dialogPane;
        setOnMouseClicked(event -> {
            EmojiEntity emojiEntity = getItem();
            dialogPane.fireEvent(new EmojiEvent(EmojiEvent.SELECTED,emojiEntity.getUnicode()));
        });
    }

    @Override
    protected void updateItem(EmojiEntity emojiEntity, boolean b) {
        super.updateItem(emojiEntity, b);
        if (b || emojiEntity ==null){
            setGraphic(null);
            setText(null);
        }else {
            Optional<Emoji> emoji = EmojiData.emojiFromUnicodeString(emojiEntity.getUnicode());
            if (emoji.isPresent()){
                List<Node> nodes = TextUtils.convertToTextAndImageNodes(emoji.get().character(),30);
                setGraphic(nodes.getFirst());
            }else {
                setText(emojiEntity.getUnicode());
            }
        }
    }
}
