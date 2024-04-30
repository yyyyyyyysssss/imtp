package org.imtp.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.body.OfflineMessageInfo;
import org.imtp.common.utils.JsonUtil;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/30 14:04
 */
public class OfflineMessageResponse extends SystemTextMessage{

    private List<OfflineMessageInfo> offlineMessageInfos;

    public OfflineMessageResponse(Long receiver) {
        this(receiver,null);
    }

    public OfflineMessageResponse(long receiver, List<OfflineMessageInfo> offlineMessageInfos) {
        super(0, receiver, Command.OFFLINE_MSG_RES);
        if(offlineMessageInfos != null && !offlineMessageInfos.isEmpty()){
            this.text = JsonUtil.toJSONString(offlineMessageInfos);
        }
    }

    public OfflineMessageResponse(ByteBuf byteBuf, Header header) {
        super(byteBuf, header);
        if(!StringUtil.isNullOrEmpty(this.text)){
            this.offlineMessageInfos = JsonUtil.parseArray(this.text, OfflineMessageInfo.class);
        }
    }

    @Override
    public void encodeBodyAsByteBuf0(ByteBuf byteBuf) {

    }

    public List<OfflineMessageInfo> getOfflineMessageInfos() {
        return offlineMessageInfos;
    }
}
