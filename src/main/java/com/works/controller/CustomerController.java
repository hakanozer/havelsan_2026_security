package com.works.controller;

import com.works.dto.CustomerLoginRequestDto;
import com.works.dto.CustomerRegisterRequestDto;
import com.works.entity.Customer;
import com.works.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("customer")
@RequiredArgsConstructor
public class CustomerController {

    final private CustomerService customerService;

    @PostMapping("register")
    public Customer register(@Valid @RequestBody CustomerRegisterRequestDto customerRegisterRequestDto) {
        return customerService.register(customerRegisterRequestDto);
    }

    // sql injection test login
    @PostMapping("login")
    public boolean login(@RequestBody CustomerLoginRequestDto customerLoginRequestDto) {
        return customerService.login(customerLoginRequestDto);
    }


}
