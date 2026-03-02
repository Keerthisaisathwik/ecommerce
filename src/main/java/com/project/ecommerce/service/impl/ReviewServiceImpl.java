package com.project.ecommerce.service.impl;

import com.project.ecommerce.dto.UpdateReviewDto;
import com.project.ecommerce.entity.Product;
import com.project.ecommerce.entity.ProductVariant;
import com.project.ecommerce.entity.Review;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.enums.OrderStatus;
import com.project.ecommerce.exception.GenericException;
import com.project.ecommerce.repository.ProductRepository;
import com.project.ecommerce.repository.ProductVariantRepository;
import com.project.ecommerce.repository.ReviewRepository;
import com.project.ecommerce.service.OrderService;
import com.project.ecommerce.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final OrderService orderService;

    private final ProductVariantRepository productVariantRepository;

    private final ProductRepository productRepository;

    private final ReviewRepository reviewRepository;

    @Override
    public Review getReview(String variantAsin, User user) throws GenericException {
        ProductVariant productVariant = productVariantRepository.findByVariantAsin(variantAsin).orElseThrow(() -> new GenericException("Invalid VariantAsin"));
        return reviewRepository.findByUserAndProduct(user, productVariant.getProduct()).orElse(null);
    }

    @Override
    @Transactional
    public Review addReview(UpdateReviewDto updateReviewDto, User user) throws GenericException{
        ProductVariant productVariant = productVariantRepository.findByVariantAsin(updateReviewDto.getVariantAsin()).orElseThrow(() -> new GenericException("Invalid VariantAsin"));
        if(!orderService.isPreviouslyOrdered(productVariant.getId(), user, OrderStatus.DELIVERED)){
            throw new GenericException("You are only allowed to write a review for items which you brought previously");
        }
        Product product =productVariant.getProduct();
        Review review = Review.builder()
                .user(user)
                .rating(updateReviewDto.getRating())
                .comment(updateReviewDto.getReviewMessage())
                .product(product)
                .productVariant(productVariant)
                .imageUrls(updateReviewDto.getImageUrls())
                .build();
        product.setTotalRatingSum(product.getTotalRatingSum() + updateReviewDto.getRating());
        product.setTotalReviews(product.getTotalReviews()+1);
        productRepository.save(product);
        reviewRepository.save(review);
        return review;
    }

    @Override
    @Transactional
    public Review updateReview(UpdateReviewDto updateReviewDto, User user) throws GenericException {
        ProductVariant productVariant = productVariantRepository.findByVariantAsin(updateReviewDto.getVariantAsin()).orElseThrow(() -> new GenericException("Invalid VariantAsin"));
        Product product = productVariant.getProduct();
        Review review = reviewRepository.findByUserAndProduct(user, product).orElse(null);
        if(review == null){
            return addReview(updateReviewDto, user);
        }
        review.setComment(updateReviewDto.getReviewMessage());
        review.setRating(updateReviewDto.getRating());
        review.setImageUrls(updateReviewDto.getImageUrls());
        product.setTotalRatingSum(product.getTotalRatingSum() - review.getRating() + updateReviewDto.getRating());
        productRepository.save(product);
        reviewRepository.save(review);
        return review;
    }

    @Override
    @Transactional
    public void deleteReview(String variantAsin, User user) throws GenericException{
        ProductVariant productVariant = productVariantRepository.findByVariantAsin(variantAsin).orElseThrow(() -> new GenericException("Invalid VariantAsin"));
        Product product = productVariant.getProduct();
        Review review = reviewRepository.findByUserAndProduct(user, product).orElse(null);
        if(review == null){
            return ;
        }else if(review.getUser() != user){
            throw new GenericException("You don't have necessary permissions to delete this review");
        }
        product.setTotalRatingSum(product.getTotalRatingSum()-review.getRating());
        product.setTotalReviews(product.getTotalReviews()-1);
        productRepository.save(product);
        reviewRepository.delete(review);
    }

    @Override
    public Page<Review> getAllReviewsBasedOnVariantAsin(String variantAsin, Pageable pageable) throws GenericException {
        ProductVariant productVariant = productVariantRepository.findByVariantAsin(variantAsin).orElseThrow(() -> new GenericException("Invalid variantAsin"));
        return reviewRepository.findByProductVariant_Product_Id(productVariant.getProduct().getId(), pageable);
    }
}
