package com.green.winey_final.repository;

import com.green.winey_final.common.entity.ProductEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepositorySupport extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;


    public ProductRepositorySupport(JPAQueryFactory queryFactory) {
        super(ProductEntity.class);
        this.queryFactory = queryFactory;
    }
}
