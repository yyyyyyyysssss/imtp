package org.imtp.web.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.imtp.common.packet.common.OfflineMessageDTO;
import org.imtp.web.config.idwork.IdGen;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/25 13:14
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_offline_msg")
public class OfflineMessage {

    public OfflineMessage(OfflineMessageDTO offlineMessageDTO){
        this.id = IdGen.genId();
        this.msgId = offlineMessageDTO.getMsgId();
        this.userId = offlineMessageDTO.getUserId();
        this.state = false;
    }

    @TableId
    private Long id;

    @TableField("msg_id")
    private Long msgId;

    @TableField("user_id")
    private Long userId;

    @TableField("state")
    private boolean state;

}
