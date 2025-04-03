package org.imtp.server.mq;

import lombok.Getter;
import lombok.Setter;
import org.imtp.common.packet.base.Packet;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/10 10:19
 */
@Getter
@Setter
public class ForwardMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public ForwardMessage() {
    }

    public ForwardMessage(List<String> channelIds,Packet message) {
        this.channelIds = channelIds;
        this.message = message;
    }

    private List<String> channelIds;

    private Packet message;

    @Override
    public String toString() {
        return "ForwardMessage{" +
                "channelIds=" + channelIds +
                ", message=" + message +
                '}';
    }
}
