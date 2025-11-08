package org.imtp.api.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotpSetupDTO {

    @NotBlank(message = "令牌不能为空")
    private String code;

}
