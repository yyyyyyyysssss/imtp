package org.imtp.client.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    private String avatar;

    private String content;

    private MessageType messageType;

}
