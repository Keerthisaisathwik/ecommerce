package com.project.ecommerce.service.impl;

import com.project.ecommerce.entity.ProductVariant;
import com.project.ecommerce.exception.GenericException;
import com.project.ecommerce.repository.ProductVariantRepository;
import com.project.ecommerce.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository productVariantRepository;

    @Override
    public ProductVariant findByVariantAsin(String variantAsin) throws GenericException {
        return productVariantRepository.findByVariantAsin(variantAsin).orElseThrow(() -> new GenericException("Invalid variant asin value"));
    }

    @Override
    public ProductVariant findByVariantId(Long id) throws GenericException {
        return productVariantRepository.findById(id).orElseThrow(() -> new GenericException("Invalid Variant Id"));
    }
}
