package org.imtp.server.mq;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor
public class ForwardMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<String> receivers;

    private byte[] message;

    @Override
    public String toString() {
        return "ForwardMessage{" +
                "receivers=" + receivers +
                ", message=" + message +
                '}';
    }
}
