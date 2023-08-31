package com.green.winey_final.common.entity;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SaleDto {

    private Long saleId;
//    private ProductEntity productEntity;
    private int sale;
    private int salePrice;
    private int saleYn;
    private String startSale; //이거 두개 확인 필요 //LocalDateTime을 String으로 바꿈
    private String endSale;

    public SaleDto() {
    }

    @QueryProjection
    public SaleDto(Long saleId, int sale, int salePrice, int saleYn, String startSale, String endSale) {
        this.saleId = saleId;
        this.sale = sale;
        this.salePrice = salePrice;
        this.saleYn = saleYn;
        this.startSale = startSale;
        this.endSale = endSale;
    }
}
