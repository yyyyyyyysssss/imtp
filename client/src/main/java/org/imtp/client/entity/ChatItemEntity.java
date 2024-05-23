package org.imtp.client.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.imtp.client.component.ClassPathImageUrlParse;
import org.imtp.client.context.ClientContextHolder;
import org.imtp.client.context.DefaultClientUserChannelContext;
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


    private boolean self;

    private String avatar;

    private String content;

    private MessageType messageType;

    public static ChatItemEntity createSelfChatItemEntity(){
        ChatItemEntity chatItemEntity = new ChatItemEntity();
        chatItemEntity.setSelf(true);
        DefaultClientUserChannelContext clientContext = (DefaultClientUserChannelContext)ClientContextHolder.clientContext();
        String at = clientContext.getUserInfo().getAvatar();
        chatItemEntity.setAvatar(new ClassPathImageUrlParse().loadUrl(at));

        return chatItemEntity;
    }

}
