package com.project.ecommerce.service.impl;

import com.project.ecommerce.dto.SaveProductDto;
import com.project.ecommerce.entity.Product;
import com.project.ecommerce.entity.ProductVariant;
import com.project.ecommerce.repository.ProductRepository;
import com.project.ecommerce.repository.ProductVariantRepository;
import com.project.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ProductVariantRepository productVariantRepository;

    @Override
    public void saveProduct(SaveProductDto newProduct) {
        Product product = Product.builder()
                .category(newProduct.getCategory())
                .name(newProduct.getName())
                .description(newProduct.getDescription())
                .brand(newProduct.getBrand())
                .build();
        productRepository.save(product);
        ProductVariant productVariant = ProductVariant.builder()
                .product(product)
                .price(newProduct.getPrice())
                .imageUrl(newProduct.getImageUrl())
                .stockQuantity(newProduct.getStockQuantity())
                .build();
        productVariantRepository.save(productVariant);
    }
}
