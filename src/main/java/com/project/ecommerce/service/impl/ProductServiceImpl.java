package com.project.ecommerce.service.impl;

import com.project.ecommerce.dto.AddNewVariantToProduct;
import com.project.ecommerce.dto.SaveProductDto;
import com.project.ecommerce.entity.Product;
import com.project.ecommerce.entity.ProductVariant;
import com.project.ecommerce.exception.GenericException;
import com.project.ecommerce.repository.ProductRepository;
import com.project.ecommerce.repository.ProductVariantRepository;
import com.project.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
                .imageUrls(newProduct.getImageUrls())
                .stockQuantity(newProduct.getStockQuantity())
                .build();
        productVariantRepository.save(productVariant);
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public void addVariant(AddNewVariantToProduct addNewVariantToProduct) throws GenericException {
        Product product =
                productRepository.findById(addNewVariantToProduct.getProductId())
                        .orElseThrow(() -> new GenericException("Can't find the id"));
        ProductVariant productVariant = ProductVariant.builder()
                .product(product)
                .price(addNewVariantToProduct.getPrice())
                .imageUrls(addNewVariantToProduct.getImageUrls())
                .stockQuantity(addNewVariantToProduct.getStockQuantity())
                .build();
        productVariantRepository.save(productVariant);
    }
}
