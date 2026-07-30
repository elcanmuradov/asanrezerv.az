package com.asanrezerv.authservice.dto.auth.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class InitiateRegister {
    @Email
    private String email;
}
