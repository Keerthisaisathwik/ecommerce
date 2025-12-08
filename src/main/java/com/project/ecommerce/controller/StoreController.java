package com.project.ecommerce.controller;

import com.project.ecommerce.dto.APISuccessResponse;
import com.project.ecommerce.dto.GetCategoryProductsDTO;
import com.project.ecommerce.dto.ProductDto;
import com.project.ecommerce.dto.ProductVariantDto;
import com.project.ecommerce.entity.Product;
import com.project.ecommerce.entity.ProductVariant;
import com.project.ecommerce.enums.CategoryType;
import com.project.ecommerce.repository.ProductRepository;
import com.project.ecommerce.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class StoreController {

    private final ProductRepository productRepository;

    private final ProductVariantRepository productVariantRepository;

    @GetMapping("/product/{id}")
    public ResponseEntity<APISuccessResponse<ProductDto>> getProductByVariantId(@PathVariable("id") Long id) {
        ProductVariant productVariant = productVariantRepository.findById(id).orElse(null);
        Product product = productVariant.getProduct();
        List<ProductVariantDto> list = new ArrayList<>();
        for(ProductVariant variant : product.getProductVariants()){
            ProductVariantDto productVariantDto = ProductVariantDto.builder()
                    .id(variant.getId())
                    .price(variant.getPrice())
                    .imageUrl(variant.getImageUrls().getFirst())
                    .isAvailable(variant.getIsAvailable())
                    .build();
            list.add(productVariantDto);
        }
        ProductDto productDto = ProductDto.builder()
                .id(productVariant.getId())
                .category(product.getCategory())
                .name(product.getName())
                .description(product.getDescription())
                .brand(product.getBrand())
                .price(productVariant.getPrice())
                .imageUrls(productVariant.getImageUrls())
                .averageRating(product.getAverageRating())
                .totalReviews(product.getTotalReviews())
                .createdAt(productVariant.getCreatedAt())
                .updatedAt(productVariant.getUpdatedAt())
                .productVariants(list)
                .stockQuantity(productVariant.getStockQuantity())
                .build();
        return new ResponseEntity<>(APISuccessResponse.<ProductDto>builder().data(productDto).build(), HttpStatus.OK);
    }

    @GetMapping("/category")
    public ResponseEntity<APISuccessResponse<List<String>>> getAllCategories(){
        List<String> categories = Arrays.stream(CategoryType.values())
                .map(categoryType -> categoryType.name())
                .toList();
        return new ResponseEntity<>(APISuccessResponse.<List<String>>builder().data(categories).build(), HttpStatus.OK);
    }

    @GetMapping("/category/{category_type}")
    public ResponseEntity<APISuccessResponse<Page<GetCategoryProductsDTO>>> getProductsByCategory(@PathVariable("category_type") CategoryType category, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findByCategory(category, pageable).orElse(null);
        List<GetCategoryProductsDTO> listOfProducts = products.stream().map(product -> {
            ProductVariant defaultVariant = product.getProductVariants().getFirst();
            GetCategoryProductsDTO getCategoryProductsDTO = GetCategoryProductsDTO.builder()
                    .id(defaultVariant.getId())
                    .imageUrl(defaultVariant.getImageUrls().getFirst())
                    .title(product.getName())
                    .rating(product.getAverageRating())
                    .price(defaultVariant.getPrice())
                    .build();
            return getCategoryProductsDTO;
        }).collect(Collectors.toList());
        return new ResponseEntity<>(APISuccessResponse.<Page<GetCategoryProductsDTO>>builder().data(new PageImpl<>(listOfProducts, pageable, products.getSize())).build(), HttpStatus.OK);
    }
}