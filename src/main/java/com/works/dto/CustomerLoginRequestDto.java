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
public class CustomerLoginRequestDto implements Serializable {
    String email;
    String password;
}