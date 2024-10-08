package org.imtp.client.entity;

import javafx.beans.property.*;
import javafx.scene.image.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.imtp.client.component.ClassPathImageUrlParse;
import org.imtp.client.context.ClientContextHolder;
import org.imtp.client.context.DefaultClientUserChannelContext;
import org.imtp.client.idwork.IdGen;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.enums.MessageType;
import org.imtp.common.packet.MessageMetadata;

import java.util.Date;

/**
 * @Description
 * @Author ys
 * @Date 2024/5/20 16:37
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatItemEntity {

    private final ObjectProperty<Image> image = new SimpleObjectProperty<>();

    private Long id;

    private String name;

    private boolean self;

    private String avatar;

    private String content;

    private MessageType messageType;

    private DeliveryMethod deliveryMethod;

    private MessageMetadata messageMetadata;

    private Image selfVideoThumbnailImage;



    public ObjectProperty<Image> imageProperty() {
        return image;
    }

    public void setImage(Image image) {
        this.image.set(image);
    }

    public Image getImage() {
        return image.get();
    }


    public static ChatItemEntity createSelfChatItemEntity(){
        ChatItemEntity chatItemEntity = new ChatItemEntity();
        chatItemEntity.setId(IdGen.genId());
        chatItemEntity.setSelf(true);
        DefaultClientUserChannelContext clientContext = (DefaultClientUserChannelContext)ClientContextHolder.clientContext();
        String at = clientContext.getUserInfo().getAvatar();
        chatItemEntity.setAvatar(new ClassPathImageUrlParse().loadUrl(at));

        return chatItemEntity;
    }

}
