package com.green.winey_final.repository;

import com.green.winey_final.common.entity.ProductDto;
import com.green.winey_final.common.entity.ProductEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity,Long>{

//    List<ProductEntity> findAllByOrderByProductIdAsc();
//
//    List<ProductEntity> findAllByOrderByProductIdDesc();
//    List<ProductEntity> findAllByOrderByPricedAsc();
//    List<ProductEntity> findAllByOrderByPricedDesc();
//    List<ProductEntity> findAllByOrderByQuantityAsc();
//
//    List<ProductEntity> findAllByOrderByQuantityDesc();
//    List<ProductEntity> findAllByOrderByBeginnerAndPromotionAsc();
//
//    List<ProductEntity> findAllByOrderByBeginnerAndPromotionDesc();



}
