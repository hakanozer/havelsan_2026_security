package com.works.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link com.works.entity.Customer}
 */
@Value
public class CustomerRegisterRequestDto implements Serializable {
    @NotNull
    @Size(min = 5, max = 100)
    @Email
    @NotEmpty
    String email;
    @NotNull
    @Size(min = 5, max = 15)
    @NotEmpty
    String password;
    @NotNull
    @Size(min = 1, max = 5)
    List<RoleDto> roles;
}