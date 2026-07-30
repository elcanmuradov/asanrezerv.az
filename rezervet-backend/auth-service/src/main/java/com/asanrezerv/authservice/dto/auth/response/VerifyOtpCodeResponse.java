package com.asanrezerv.authservice.dto.auth.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifyOtpCodeResponse {
    private String email;

    private String tempToken;
}
