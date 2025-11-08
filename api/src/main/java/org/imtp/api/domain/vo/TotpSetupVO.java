package org.imtp.api.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TotpSetupVO {

    private String qrCodeBase64;

    private String otpAuthUrl;

}
