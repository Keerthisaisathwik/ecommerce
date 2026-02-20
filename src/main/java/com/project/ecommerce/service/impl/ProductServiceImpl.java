package com.project.ecommerce.service.impl;

import com.project.ecommerce.dto.AddNewVariantToProductDto;
import com.project.ecommerce.dto.SaveProductDto;
import com.project.ecommerce.dto.SaveProductVariantDto;
import com.project.ecommerce.dto.VariantAttributeRequestDto;
import com.project.ecommerce.entity.Product;
import com.project.ecommerce.entity.ProductVariant;
import com.project.ecommerce.entity.VariantAttribute;
import com.project.ecommerce.exception.GenericException;
import com.project.ecommerce.repository.ProductRepository;
import com.project.ecommerce.repository.ProductVariantRepository;
import com.project.ecommerce.repository.VariantAttributeRepository;
import com.project.ecommerce.service.ProductService;
import com.project.ecommerce.utils.Slug;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ProductVariantRepository productVariantRepository;

    private final VariantAttributeRepository variantAttributeRepository;

    private final Slug slug;

    @Override
    @Transactional
    public void saveProduct(SaveProductDto newProduct) {
        Product product = Product.builder()
                .category(newProduct.getCategory())
                .name(newProduct.getName())
                .description(newProduct.getDescription())
                .brand(newProduct.getBrand())
                .build();
        productRepository.save(product);
        List<ProductVariant> productVariantList = new ArrayList<>();
        List<VariantAttribute> variantAttributesList = new ArrayList<>();
        for(SaveProductVariantDto productVariant : newProduct.getProductVariants()){
            String variantAsin = slug.generateUniqueProductVariantAsin();
            ProductVariant newProductVariant = ProductVariant.builder()
                    .product(product)
                    .price(productVariant.getPrice())
                    .imageUrls(productVariant.getImageUrls())
                    .stockQuantity(productVariant.getStockQuantity())
                    .discountedPrice(productVariant.getDiscountedPrice())
                    .taxPercentage(productVariant.getTaxPercentage())
                    .variantAsin(variantAsin)
                    .build();
            productVariantList.add(newProductVariant);
            for(VariantAttributeRequestDto newVariantAttribute: productVariant.getVariantAttributeList()){
                VariantAttribute newVariant = VariantAttribute.builder()
                        .variant(newProductVariant)
                        .attributeName(newVariantAttribute.getAttributeName())
                        .attributeValue(newVariantAttribute.getAttributeValue())
                        .build();
                variantAttributesList.add(newVariant);
            }
        }
        productVariantRepository.saveAll(productVariantList);
        variantAttributeRepository.saveAll(variantAttributesList);
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public void addVariant(AddNewVariantToProductDto addNewVariantToProductDto) throws GenericException {
        Product product =
                productRepository.findById(addNewVariantToProductDto.getProductId())
                        .orElseThrow(() -> new GenericException("Can't find the id"));
        String variantAsin = slug.generateUniqueProductVariantAsin();
        ProductVariant productVariant = ProductVariant.builder()
                .product(product)
                .price(addNewVariantToProductDto.getPrice())
                .imageUrls(addNewVariantToProductDto.getImageUrls())
                .stockQuantity(addNewVariantToProductDto.getStockQuantity())
                .discountedPrice(addNewVariantToProductDto.getDiscountedPrice())
                .taxPercentage(addNewVariantToProductDto.getTaxPercentage())
                .variantAsin(variantAsin)
                .build();
        List<VariantAttribute> variantAttributesList = new ArrayList<>();
        for(VariantAttributeRequestDto newVariantAttribute: addNewVariantToProductDto.getVariantAttributeList()){
            VariantAttribute newVariant = VariantAttribute.builder()
                    .variant(productVariant)
                    .attributeName(newVariantAttribute.getAttributeName())
                    .attributeValue(newVariantAttribute.getAttributeValue())
                    .build();
            variantAttributesList.add(newVariant);
        }
        productVariantRepository.save(productVariant);
        variantAttributeRepository.saveAll(variantAttributesList);
    }
}
