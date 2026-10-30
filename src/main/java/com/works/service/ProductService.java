package com.works.service;

import com.works.entity.Product;
import com.works.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @CacheEvict(value = "products", allEntries = true)
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Cacheable(value = "products")
    public List<Product> list() {
        return productRepository.findAll();
    }

}
