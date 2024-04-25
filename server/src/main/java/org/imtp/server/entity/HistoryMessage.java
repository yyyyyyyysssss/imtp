package org.imtp.server.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/25 13:14
 */
@Getter
@Setter
public class HistoryMessage {

    private Long id;

    private Long sender;

    private Long receiver;

    private Long timestamp;

    private Integer type;

}
