package com.green.winey_final.common.entity;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductDto {

    private Long productId;
    private String nmKor;
    private int price;
    private int promotion;
    private int beginner;
    private int quantity;
    private int sale;
    private int salePrice;

    public ProductDto() {
    }

    @QueryProjection
    public ProductDto (Long productId, String nmKor, int price, int promotion, int beginner, int quantity, int sale, int salePrice) {
        this.productId = productId;
        this.nmKor = nmKor;
        this.price = price;
        this.promotion = promotion;
        this.beginner = beginner;
        this.quantity = quantity;
        this.sale = sale;
        this.salePrice = salePrice;
    }
}
