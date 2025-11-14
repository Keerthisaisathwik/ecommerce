package com.project.ecommerce.service.impl;

import com.project.ecommerce.dto.ProductDto;
import com.project.ecommerce.entity.Product;
import com.project.ecommerce.repository.ProductRepository;
import com.project.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public void saveProduct(ProductDto newProduct) {
        Product product = Product.builder()
                .category(newProduct.getCategory())
                .name(newProduct.getName())
                .description(newProduct.getDescription())
                .brand(newProduct.getBrand())
                .price(newProduct.getPrice())
                .imageUrl(newProduct.getImageUrl())
                .stockQuantity(newProduct.getStockQuantity())
                .isAvailable(newProduct.getIsAvailable() != null ? newProduct.getIsAvailable() : true)
                .build();
        productRepository.save(product);
    }
}
