package com.project.ecommerce.controller;

import com.project.ecommerce.dto.APISuccessResponse;
import com.project.ecommerce.entity.Product;
import com.project.ecommerce.enums.CategoryType;
import com.project.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class StoreController {

    private final ProductRepository productRepository;

    @GetMapping("/product/{product_id}")
    public ResponseEntity<APISuccessResponse<Product>> loginByAuthCode(@PathVariable("product_id") Long product_id) {
        Product product = productRepository.findById(product_id).orElse(null);
        return new ResponseEntity<>(APISuccessResponse.<Product>builder().status(true).data(product).message(null).build(), HttpStatus.OK);
    }

    @GetMapping("/category")
    public ResponseEntity<APISuccessResponse<List<String>>> getAllCategories(){
        List<String> categories = Arrays.stream(CategoryType.values())
                .map(categoryType -> categoryType.name())
                .toList();
        return new ResponseEntity<>(APISuccessResponse.<List<String>>builder().status(true).data(categories).message(null).build(), HttpStatus.OK);
    }

    @GetMapping("/category/{category_type}")
    public ResponseEntity<APISuccessResponse<Page<Product>>> getProductsByCategory(@PathVariable("category_type") CategoryType category, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findByCategory(category, pageable).orElse(null);
        return new ResponseEntity<>(APISuccessResponse.<Page<Product>>builder().status(true).message(null).data(products).build(), HttpStatus.OK);
    }
}