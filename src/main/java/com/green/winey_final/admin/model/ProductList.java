package com.green.winey_final.admin.model;

import com.green.winey_final.common.entity.ProductEntity;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductList {
    private Slice<ProductEntity> productList;
    List<ProductEntity> productList1;
    Page<ProductEntity> products;
}
