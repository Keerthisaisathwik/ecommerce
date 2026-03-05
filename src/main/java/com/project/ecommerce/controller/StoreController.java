package com.project.ecommerce.controller;

import com.project.ecommerce.dto.*;
import com.project.ecommerce.entity.*;
import com.project.ecommerce.enums.CategoryType;
import com.project.ecommerce.enums.OrderStatus;
import com.project.ecommerce.exception.GenericException;
import com.project.ecommerce.service.*;
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

    private final ProductService productService;

    private final ProductVariantService productVariantService;

    private final UserService userService;

    private final CartService cartService;

    private final WishlistService wishlistService;

    private final OrderService orderService;

    private final ReviewService reviewService;

    @GetMapping("/product/{variant_asin}")
    public ResponseEntity<APISuccessResponse<ProductDto>> getProductByVariantId(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader, @PathVariable("variant_asin") String variantAsin) throws GenericException {
        User user = null;
        if(authorizationHeader != null){
            String token = authorizationHeader.substring(7);
            if(userService.validateJwtToken(token)){
                user = userService.findUserByToken(token);
            }
        }
        ProductVariant productVariant = productVariantService.findByVariantAsin(variantAsin);
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
                .averageRating((double)product.getTotalRatingSum()/ product.getTotalReviews())
                .totalReviews(product.getTotalReviews())
                .createdAt(productVariant.getCreatedAt())
                .updatedAt(productVariant.getUpdatedAt())
                .productVariants(list)
                .stockQuantity(productVariant.getStockQuantity())
                .isPreviouslyOrdered(orderService.isPreviouslyOrdered(productVariant.getId(), user, OrderStatus.DELIVERED))
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
        Page<Product> products = productService.getProductsByCategoryName(category, pageable);
        if(products.isEmpty())
            return new ResponseEntity<>(APISuccessResponse.builder().data(new PageImpl<>(Collections.emptyList(), pageable, products.getTotalElements())).build(), HttpStatus.OK);
        List<GetCategoryProductsDTO> listOfProducts = products.stream().filter(product -> !product.getProductVariants().isEmpty()).map(product -> {
            ProductVariant defaultVariant = product.getProductVariants().getFirst();
            return GetCategoryProductsDTO.builder()
                    .variantAsin(defaultVariant.getVariantAsin())
                    .imageUrl(defaultVariant.getImageUrls().getFirst())
                    .title(product.getName())
                    .rating((double)product.getTotalRatingSum()/ product.getTotalReviews())
                    .price(defaultVariant.getPrice())
                    .build();
        }).collect(Collectors.toList());
        return new ResponseEntity<>(APISuccessResponse.<Page<GetCategoryProductsDTO>>builder().data(new PageImpl<>(listOfProducts, pageable, products.getTotalElements())).build(), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<APISuccessResponse<Page<GetCategoryProductsDTO>>> getProductsByQuery(@RequestParam String query, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.searchProductsByQuery(query, pageable);
        List<GetCategoryProductsDTO> listOfProducts = products.stream().map(product -> {
            ProductVariant defaultVariant = product.getProductVariants().getFirst();
            return GetCategoryProductsDTO.builder()
                    .variantAsin(defaultVariant.getVariantAsin())
                    .imageUrl(defaultVariant.getImageUrls().getFirst())
                    .title(product.getName())
                    .rating((double)product.getTotalRatingSum()/ product.getTotalReviews())
                    .price(defaultVariant.getPrice())
                    .build();
        }).collect(Collectors.toList());
        return new ResponseEntity<>(APISuccessResponse.<Page<GetCategoryProductsDTO>>builder().data(new PageImpl<>(listOfProducts, pageable, products.getSize())).build(), HttpStatus.OK);
    }

    @GetMapping("/sentry-test")
    public String test() {
        throw new RuntimeException("Sentry test error");
    }

    @GetMapping("/review/{variant_asin}")
    public ResponseEntity<APISuccessResponse<Page<ProductReviewDto>>> getAllReviewsOfProduct(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable("variant_asin") String variantAsin, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) throws GenericException{
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductReviewDto> reviews = reviewService.getAllReviewsBasedOnVariantAsin(variantAsin, pageable).map(review -> {
                    if(review==null){
                        return null;
                    }
                    return ProductReviewDto.builder()
                        .name(review.getUser().getUserDetails().getFirstName() + " "+ review.getUser().getUserDetails().getLastName())
                        .rating(review.getRating())
                        .reviewMessage(review.getComment())
                        .imageUrls(review.getImageUrls())
                        .variantAttributeList(review.getProductVariant().getAttributes().stream().map(variantAttribute -> VariantAttributeDto.builder()
                                        .attributeName(variantAttribute.getAttributeName())
                                        .attributeValue(variantAttribute.getAttributeValue())
                                        .build())
                                        .toList())
                        .updatedAt(review.getUpdatedAt())
                        .build();
                });
        return new ResponseEntity<>(APISuccessResponse.<Page<ProductReviewDto>>builder().data(reviews).build(), HttpStatus.OK);
    }
}