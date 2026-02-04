package com.project.ecommerce.utils;

import com.project.ecommerce.repository.ProductRepository;
import com.project.ecommerce.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Slug {

    private static final int MAX_SLUG_LENGTH = 255;
    private static final int SLUG_UNIQUE_LENGTH = 6;
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

    public String generateUniqueSlug(String name) {

        String baseSlug = name.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        // Reserve space for suffix
        int maxBaseLength = MAX_SLUG_LENGTH - (SLUG_UNIQUE_LENGTH + 1); // +1 for '-'

        if (baseSlug.length() > maxBaseLength) {
            baseSlug = baseSlug.substring(0, maxBaseLength);
            baseSlug = baseSlug.replaceAll("-$", ""); // remove trailing dash after cut
        }

        // First try without suffix
        if (!productRepository.existsBySlug(baseSlug)) {
            return baseSlug;
        }

        // Collision → add random suffix
        for (int i = 0; i < 5; i++) {
            String suffix = generate(SLUG_UNIQUE_LENGTH);
            String slug = baseSlug + "-" + suffix;

            if (!productRepository.existsBySlug(slug)) {
                return slug;
            }
        }

        throw new IllegalStateException("Failed to generate unique slug");
    }


}
