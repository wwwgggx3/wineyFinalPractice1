package com.green.winey_final.admin;

import com.green.winey_final.admin.model.*;
import com.green.winey_final.common.entity.ProductDto;
import com.green.winey_final.common.entity.ProductEntity;
import com.green.winey_final.common.entity.QProductDto;
import com.green.winey_final.common.entity.SaleDto;
import com.querydsl.core.Tuple;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "관리자 페이지")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService SERVICE;

    @Autowired
    public AdminController(AdminService SERVICE) {
        this.SERVICE = SERVICE;
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Long postProduct(@RequestPart(required = false) MultipartFile pic, @RequestPart ProductInsParam param) {
        return SERVICE.postProduct(pic, param);
    }

    @PutMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Long putProduct(@RequestPart(required = false) MultipartFile pic, @RequestPart ProductUpdParam param) {
        return SERVICE.putProduct(pic, param);
    }


    @GetMapping("/product/list") // @RequestParam(defaultValue = "1") int page
    public Page<ProductDto> getProduct(@RequestParam(defaultValue = "1")int page,
                                       @RequestParam(defaultValue = "20")int row) {
//        @PageableDefault(sort = "product_id", direction = Sort.Direction.DESC, size = 10)
//        Pageable pageable
        return SERVICE.getProduct(page, row);
    }

//    @GetMapping
//    public List<ProductEntity> getProduct1(OrderConditionRequest orderConditionRequest){
//
//        return SERVICE.findAllPersonOrderBy(orderConditionRequest);
//
//    }

}
