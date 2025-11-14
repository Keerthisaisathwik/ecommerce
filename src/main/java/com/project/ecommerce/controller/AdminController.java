package com.project.ecommerce.controller;

import com.project.ecommerce.dto.APISuccessResponse;
import com.project.ecommerce.dto.ProductDto;
import com.project.ecommerce.repository.ProductRepository;
import com.project.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductRepository productRepository;

    private final ProductService productService;

    @PostMapping("/add-product")
    public ResponseEntity<APISuccessResponse<?>> loginByAuthCode(@RequestBody ProductDto newProduct) {
        productService.saveProduct(newProduct);
        return new ResponseEntity<>(APISuccessResponse.builder().data(null).build(), HttpStatus.OK);
    }
}
