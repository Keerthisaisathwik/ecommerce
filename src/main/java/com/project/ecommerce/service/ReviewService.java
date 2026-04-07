package com.project.ecommerce.service;

import com.project.ecommerce.dto.UpdateReviewDto;
import com.project.ecommerce.entity.Review;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.exception.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {

    Review getReview(String variantAsin, User user) throws GenericException;

    Review addReview(UpdateReviewDto updateReviewDto, User user) throws GenericException;

    Review updateReview(UpdateReviewDto updateReviewDto, User user) throws GenericException;

    void deleteReview(String variantAsin, User user) throws GenericException;

    Page<Review> getAllReviewsBasedOnVariantAsin(String variantAsin, Pageable pageable) throws GenericException;

    Page<Review> getReviewsWithImages(Long productId, int page, int size) throws GenericException;

    Review getReviewById(Long id) throws GenericException;
}
