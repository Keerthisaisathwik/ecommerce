package com.project.ecommerce.utils;

import com.project.ecommerce.repository.ProductRepository;
import com.project.ecommerce.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Slug {

    private static final int ASIN_UNIQUE_LENGTH = 10;

    private final ProductRepository productRepository;

    private final ProductVariantRepository productVariantRepository;

    private String generate(int length) {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, length)
                .toUpperCase();
    }

    public String generateUniqueProductVariantAsin() {
        String asin;
        do {
            asin = generate(ASIN_UNIQUE_LENGTH);
        } while (productVariantRepository.existsByVariantAsin(asin));
        return asin;
    }

}
