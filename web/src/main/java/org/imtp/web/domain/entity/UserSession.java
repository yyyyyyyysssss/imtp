package org.imtp.web.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @Description
 * @Author ys
 * @Date 2024/12/13 13:08
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_user_session")
public class UserSession {

    @TableId
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("session_id")
    private Long sessionId;

}
