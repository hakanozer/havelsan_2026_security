package com.works.dto;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.works.entity.Role}
 */
@Value
public class RoleDto implements Serializable {
    Long rid;
    String name;
}