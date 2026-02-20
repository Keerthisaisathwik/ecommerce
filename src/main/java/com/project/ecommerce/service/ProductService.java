package com.project.ecommerce.service;

import com.project.ecommerce.dto.AddNewVariantToProductDto;
import com.project.ecommerce.dto.SaveProductDto;
import com.project.ecommerce.entity.Product;
import com.project.ecommerce.exception.GenericException;

import java.util.Optional;

public interface ProductService {

    void saveProduct(SaveProductDto newProduct);

    Optional<Product> getProductById(Long id);

    void addVariant(AddNewVariantToProductDto addNewVariantToProductDto) throws GenericException;
}
