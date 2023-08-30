package com.green.winey_final.common.entity;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class ProductDto {

    private Long productId;
    private String nmKor;
    private int price;
    private int promotion;
    private int beginner;
    private int quantity;

    public ProductDto() {
    }

    @QueryProjection
    public ProductDto (Long productId, String nmKor, int price, int promotion, int beginner, int quantity) {
        this.productId = productId;
        this.nmKor = nmKor;
        this.price = price;
        this.promotion = promotion;
        this.beginner = beginner;
        this.quantity = quantity;
    }
}
