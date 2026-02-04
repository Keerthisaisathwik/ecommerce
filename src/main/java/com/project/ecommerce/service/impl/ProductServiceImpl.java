package com.project.ecommerce.service.impl;

import com.project.ecommerce.dto.AddNewVariantToProduct;
import com.project.ecommerce.dto.SaveProductDto;
import com.project.ecommerce.entity.Product;
import com.project.ecommerce.entity.ProductVariant;
import com.project.ecommerce.exception.GenericException;
import com.project.ecommerce.repository.ProductRepository;
import com.project.ecommerce.repository.ProductVariantRepository;
import com.project.ecommerce.service.ProductService;
import com.project.ecommerce.utils.Slug;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.project.ecommerce.utils.Slug.*;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ProductVariantRepository productVariantRepository;

    private final Slug slug;

    @Override
    public void saveProduct(SaveProductDto newProduct) {
        String variantAsin = slug.generateUniqueProductVariantAsin();
        Product product = Product.builder()
                .category(newProduct.getCategory())
                .name(newProduct.getName())
                .description(newProduct.getDescription())
                .brand(newProduct.getBrand())
                .slug(slug.generateUniqueSlug(newProduct.getName()))
                .build();
        productRepository.save(product);
        ProductVariant productVariant = ProductVariant.builder()
                .product(product)
                .price(newProduct.getPrice())
                .imageUrls(newProduct.getImageUrls())
                .stockQuantity(newProduct.getStockQuantity())
                .discountedPrice(newProduct.getDiscountedPrice())
                .taxPercentage(newProduct.getTaxPercentage())
                .variantAsin(variantAsin)
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
        String variantAsin = slug.generateUniqueProductVariantAsin();
        ProductVariant productVariant = ProductVariant.builder()
                .product(product)
                .price(addNewVariantToProduct.getPrice())
                .imageUrls(addNewVariantToProduct.getImageUrls())
                .stockQuantity(addNewVariantToProduct.getStockQuantity())
                .discountedPrice(addNewVariantToProduct.getDiscountedPrice())
                .taxPercentage(addNewVariantToProduct.getTaxPercentage())
                .variantAsin(variantAsin)
                .build();
        productVariantRepository.save(productVariant);
    }
}
