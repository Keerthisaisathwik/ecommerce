package com.project.ecommerce.service;

import com.project.ecommerce.dto.AddNewVariantToProductDto;
import com.project.ecommerce.dto.SaveProductDto;
import com.project.ecommerce.entity.Product;
import com.project.ecommerce.enums.CategoryType;
import com.project.ecommerce.exception.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductService {

    void saveProduct(SaveProductDto newProduct);

    Optional<Product> getProductById(Long id);

    void addVariant(AddNewVariantToProductDto addNewVariantToProductDto) throws GenericException;

    Page<Product> getProductsByCategoryName(CategoryType category, Pageable pageable);

    Page<Product> searchProductsByQuery(String query, Pageable pageable);
}
