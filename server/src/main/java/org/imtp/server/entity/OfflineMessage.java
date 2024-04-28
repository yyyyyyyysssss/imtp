package org.imtp.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.imtp.server.idwork.IdGen;

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

    public OfflineMessage(Long msgId,Long userId){
        this.id = IdGen.genId();
        this.msgId = msgId;
        this.userId = userId;
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
