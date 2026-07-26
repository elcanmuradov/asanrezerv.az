package com.rezervet.authservice.dto.auth.request;

import com.rezervet.authservice.utils.enums.AccountType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @Email
    private String email;

    @Size(min = 6)
    private String password;

    @Size(min = 8)
    private String fullName;

    @NotNull
    private AccountType accountType;

    @NotNull
    private String tempToken;

}
