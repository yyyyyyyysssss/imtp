package org.imtp.web.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.imtp.common.enums.DeliveryMethod;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/7 15:15
 */
@Getter
@Setter
public class UserSessionDTO {

    private String userId;

    @NotBlank(message = "not null")
    private String receiverUserId;

    @NotNull(message = "not null")
    private DeliveryMethod deliveryMethod;

}
