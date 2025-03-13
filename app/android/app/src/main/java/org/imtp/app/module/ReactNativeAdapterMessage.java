package org.imtp.app.module;

import org.imtp.common.enums.Command;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.packet.AbstractTextMessage;

import io.netty.buffer.ByteBuf;

public class ReactNativeAdapterMessage extends AbstractTextMessage {

    public ReactNativeAdapterMessage(ReactNativeMessage reactNativeMessage){
        super(reactNativeMessage.getContent(),
                reactNativeMessage.getContentMetadata(),
                reactNativeMessage.getSender(),
                reactNativeMessage.getReceiver(),
                Command.find(reactNativeMessage.getType().getValue().byteValue()),
                reactNativeMessage.getAckId(),
                reactNativeMessage.getDeliveryMethod().equals(DeliveryMethod.GROUP));
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }
}
