package com.works.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 100)
    private String email;

    @JsonIgnore
    @Column(length = 200)
    private String password;

    @ManyToMany
    private List<Role> roles;

}
