package com.project.ecommerce.service;

import com.project.ecommerce.dto.SaveProductDto;
import com.project.ecommerce.entity.Product;

import java.util.Optional;

public interface ProductService {

    void saveProduct(SaveProductDto newProduct);

    Optional<Product> getProductById(Long id);
}
