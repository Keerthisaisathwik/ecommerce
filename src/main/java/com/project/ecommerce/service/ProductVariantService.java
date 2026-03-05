package com.project.ecommerce.service;

import com.project.ecommerce.entity.ProductVariant;
import com.project.ecommerce.exception.GenericException;

public interface ProductVariantService {
    ProductVariant findByVariantAsin(String variantAsin) throws GenericException;

    ProductVariant findByVariantId(Long id) throws GenericException;
}
