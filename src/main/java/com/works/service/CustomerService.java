package com.works.service;

import com.works.dto.CustomerLoginRequestDto;
import com.works.dto.CustomerRegisterRequestDto;
import com.works.entity.Customer;
import com.works.entity.Role;
import com.works.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService implements UserDetailsService {

    final private CustomerRepository customerRepository;
    final private ModelMapper modelMapper;
    final private PasswordEncoder passwordEncoder;
    final private DriverManagerDataSource dataSource;

    public Customer register(CustomerRegisterRequestDto customerRegisterRequestDto) {
        Customer customer = modelMapper.map(customerRegisterRequestDto, Customer.class);
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        return customerRepository.save(customer);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Customer> optionalCustomer = customerRepository.findByEmailEqualsIgnoreCase(username);
        if (optionalCustomer.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
       Customer customer = optionalCustomer.get();
        return new User(
                customer.getEmail(),
                customer.getPassword(),
                parseRoles(customer.getRoles())
                );
    }

    private Collection<? extends GrantedAuthority> parseRoles(List<Role> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(() -> role.getName());
        }
        return authorities;
    }

    public boolean login(CustomerLoginRequestDto req) {
        try {
            String sql = "select * from customer where email = ? and password = ?";
            PreparedStatement st = dataSource.getConnection().prepareStatement(sql);
            st.setString(1, req.getEmail());
            st.setString(2, req.getPassword());
            ResultSet rs = st.executeQuery();
            return rs.next();
        }catch (Exception e) {
            return false;
        }

    }
}
