package com.works.entity;

import com.works.customValid.CityValid;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;

    private String title;
    private BigDecimal price;
    @CityValid
    private String city;

}
