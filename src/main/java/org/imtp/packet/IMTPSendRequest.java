package org.imtp.packet;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author ys
 * @Date 2023/4/10 11:28
 */
@Getter
@Setter
public class IMTPSendRequest{


    private Long sendUserId;

    private Long recvUserId;

    private Integer type;



}
