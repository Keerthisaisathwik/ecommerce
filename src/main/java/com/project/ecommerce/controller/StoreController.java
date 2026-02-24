package com.project.ecommerce.controller;

import com.project.ecommerce.dto.*;
import com.project.ecommerce.entity.*;
import com.project.ecommerce.enums.CategoryType;
import com.project.ecommerce.exception.GenericException;
import com.project.ecommerce.repository.ProductRepository;
import com.project.ecommerce.repository.ProductVariantRepository;
import com.project.ecommerce.service.CartService;
import com.project.ecommerce.service.OrderService;
import com.project.ecommerce.service.UserService;
import com.project.ecommerce.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class StoreController {

    private final ProductRepository productRepository;

    private final ProductVariantRepository productVariantRepository;

    private final UserService userService;

    private final CartService cartService;

    private final WishlistService wishlistService;

    private final OrderService orderService;

    @GetMapping("/product/{variant_asin}")
    public ResponseEntity<APISuccessResponse<ProductDto>> getProductByVariantId(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader, @PathVariable("variant_asin") String variantAsin) throws GenericException {
        User user = null;
        if(authorizationHeader != null){
            String token = authorizationHeader.substring(7);
            if(userService.validateJwtToken(token)){
                user = userService.findUserByToken(token);
            }
        }
        ProductVariant productVariant = productVariantRepository.findByVariantAsin(variantAsin).orElse(null);
        if(productVariant == null)
            throw new GenericException("Given productVariant Id is not correct or does not exist");
        Product product = productVariant.getProduct();
        List<ProductVariantDto> list = new ArrayList<>();
        for(ProductVariant variant : product.getProductVariants()){
            List<VariantAttributeDto> variantAttributeDtoList = new ArrayList<>();
            for(VariantAttribute newVariantAttribute : productVariant.getAttributes()){
                VariantAttributeDto variantAttributeDto = VariantAttributeDto.builder()
                        .attributeName(newVariantAttribute.getAttributeName())
                        .attributeValue(newVariantAttribute.getAttributeValue())
                        .build();
                variantAttributeDtoList.add(variantAttributeDto);
            }
            ProductVariantDto productVariantDto = ProductVariantDto.builder()
                    .variantAsin(variant.getVariantAsin())
                    .price(variant.getPrice())
                    .imageUrl(variant.getImageUrls().getFirst())
                    .isAvailable(variant.getIsAvailable())
                    .discountedPrice(variant.getDiscountedPrice())
                    .variantAttributeList(variantAttributeDtoList)
                    .build();
            list.add(productVariantDto);
        }
        ProductDto productDto = ProductDto.builder()
                .productVariantAsin(productVariant.getVariantAsin())
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
                .isPreviouslyOrdered(orderService.isPreviouslyOrdered(productVariant.getId(), user))
                .cartQuantity(user == null ? 0 : cartService.findSpecificCartItemQuantity(user.getId(), productVariant.getId()))
                .isWishlisted(user != null && wishlistService.existsByUserAndProductVariant(user, productVariant))
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
    public ResponseEntity<APISuccessResponse<?>> getProductsByCategory(@PathVariable("category_type") CategoryType category, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) throws GenericException{
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findByCategory(category, pageable);
        if(products.isEmpty())
            return new ResponseEntity<>(APISuccessResponse.builder().data(new PageImpl<>(Collections.emptyList(), pageable, products.getTotalElements())).build(), HttpStatus.OK);
        List<GetCategoryProductsDTO> listOfProducts = products.stream().filter(product -> !product.getProductVariants().isEmpty()).map(product -> {
            ProductVariant defaultVariant = product.getProductVariants().getFirst();
            return GetCategoryProductsDTO.builder()
                    .variantAsin(defaultVariant.getVariantAsin())
                    .imageUrl(defaultVariant.getImageUrls().getFirst())
                    .title(product.getName())
                    .rating(product.getAverageRating())
                    .price(defaultVariant.getPrice())
                    .build();
        }).collect(Collectors.toList());
        return new ResponseEntity<>(APISuccessResponse.<Page<GetCategoryProductsDTO>>builder().data(new PageImpl<>(listOfProducts, pageable, products.getTotalElements())).build(), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<APISuccessResponse<Page<GetCategoryProductsDTO>>> getProductsByQuery(@RequestParam String query, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.searchProducts(query, pageable);
        List<GetCategoryProductsDTO> listOfProducts = products.stream().map(product -> {
            ProductVariant defaultVariant = product.getProductVariants().getFirst();
            return GetCategoryProductsDTO.builder()
                    .variantAsin(defaultVariant.getVariantAsin())
                    .imageUrl(defaultVariant.getImageUrls().getFirst())
                    .title(product.getName())
                    .rating(product.getAverageRating())
                    .price(defaultVariant.getPrice())
                    .build();
        }).collect(Collectors.toList());
        return new ResponseEntity<>(APISuccessResponse.<Page<GetCategoryProductsDTO>>builder().data(new PageImpl<>(listOfProducts, pageable, products.getSize())).build(), HttpStatus.OK);
    }

    @GetMapping("/sentry-test")
    public String test() {
        throw new RuntimeException("Sentry test error");
    }
}