package org.imtp.desktop.entity;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.imtp.common.enums.MessageTypeV2;
import org.imtp.desktop.component.ClassPathImageUrlParse;
import org.imtp.desktop.context.ClientContextHolder;
import org.imtp.desktop.context.DefaultClientUserChannelContext;
import org.imtp.desktop.context.UserContextHolder;
import org.imtp.desktop.enums.MessageStatus;
import org.imtp.desktop.idwork.IdGen;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.enums.MessageType;
import org.imtp.common.packet.MessageMetadata;

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

    private final ObjectProperty<Image> imageStatusIcon = new SimpleObjectProperty<>();

    private Long id;

    private String name;

    private boolean self;

    private String avatar;

    private String content;

    private MessageTypeV2 messageType;

    private DeliveryMethod deliveryMethod;

    private MessageMetadata messageMetadata;

    private Image selfVideoThumbnailImage;

    private Object contentObject;

    private ObjectProperty<MessageStatus> messageStatus = new SimpleObjectProperty<>();

    private ObjectProperty<Image> avatarImage = new SimpleObjectProperty<>();

    public ObjectProperty<Image> imageStatusIconProperty() {
        return imageStatusIcon;
    }

    public void setImageStatusIcon(Image image) {
        this.imageStatusIcon.set(image);
    }

    public Image getImageStatusIconImage() {
        return imageStatusIcon.get();
    }


    public ObjectProperty<MessageStatus> messageStatusProperty() {
        return messageStatus;
    }

    public void setMessageStatus(MessageStatus messageStatus) {
        this.messageStatus.set(messageStatus);
    }

    public MessageStatus getMessageStatus() {
        return messageStatus.get();
    }

    public void setAvatar(String avatar) {
        if (!avatar.equals(this.avatar)) {
            this.avatar = avatar;
            setAvatarImage(avatar);  // 更新头像
        }
    }

    public ObjectProperty<Image> avatarImageProperty() {

        return avatarImage;
    }

    public void setAvatarImage(String url) {
        if (avatarImage.get() == null || !url.equals(avatarImage.get().getUrl())) {
            this.avatarImage.set(new Image(url, true));
        }
    }

    public static ChatItemEntity createSelfChatItemEntity(){
        ChatItemEntity chatItemEntity = new ChatItemEntity();
        chatItemEntity.setId(IdGen.genId());
        chatItemEntity.setSelf(true);
        chatItemEntity.avatarImageProperty().bindBidirectional(UserContextHolder.userContext().avatarImageProperty());
        return chatItemEntity;
    }

    public Object getContentObject(){
        switch (messageType){
            case TEXT,FILE,VOICE:
                this.contentObject = this.content;
                break;
            case IMAGE:
                if(contentObject == null){
                    this.contentObject = new Image(this.content,true);
                }
                break;
            case VIDEO:
                if(contentObject == null){
                    this.contentObject = new Image(this.messageMetadata.getThumbnailUrl(),true);
                }
                break;
        }
        return contentObject;
    }

}
