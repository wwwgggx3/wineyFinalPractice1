package com.green.winey_final.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProductQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;
}
