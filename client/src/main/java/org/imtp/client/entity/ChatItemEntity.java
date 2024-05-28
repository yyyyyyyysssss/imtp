package org.imtp.client.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.imtp.client.component.ClassPathImageUrlParse;
import org.imtp.client.context.ClientContextHolder;
import org.imtp.client.context.DefaultClientUserChannelContext;
import org.imtp.client.idwork.IdGen;
import org.imtp.common.enums.MessageType;

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


    private Long id;

    private boolean self;

    private String avatar;

    private String content;

    private MessageType messageType;

    private Integer sent;

    public void doSending(){
        this.sent = 1;
    }

    public void doSentSuccessfully(){
        this.sent = 2;
    }

    public void doSentFailure(){
        this.sent = 3;
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
