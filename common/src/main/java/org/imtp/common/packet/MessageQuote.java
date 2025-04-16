package org.imtp.common.packet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.imtp.common.enums.MessageType;

/**
 * @Description
 * @Author ys
 * @Date 2025/4/16 13:53
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageQuote {

    private MessageType type;

    private String name;

    private String content;

    private Boolean self;

    private MessageMetadata contentMetadata;

}
