package org.imtp.client.controller;

import org.imtp.common.enums.DeliveryMethod;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/23 16:51
 */
public interface Controller {

    void send(String msg, long receiver, DeliveryMethod deliveryMethod);

}
