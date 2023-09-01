package com.green.winey_final.admin;

import com.green.winey_final.admin.model.*;
import com.green.winey_final.common.entity.*;
import com.green.winey_final.common.entity.QProductDto;
import com.green.winey_final.common.entity.QSaleDto;
import com.green.winey_final.repository.*;
import com.green.winey_final.utils.MyFileUtils;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.green.winey_final.common.entity.QProductEntity.productEntity;
import static com.green.winey_final.common.entity.QSaleEntity.saleEntity;

@Slf4j
@Service
//@RequiredArgsConstructor
public class AdminService {

    private final AdminMapper MAPPER;
    private final String FILE_DIR;

    private final ProductRepository productRep;
    private final FeatureRepository featureRep;
    private final SaleRepository saleRep;
    private final AromaRepository aromaRep;
    private final CountryRepository countryRep;
    private final CategoryRepository categoryRep;
    private final AromaCategoryRepository aromaCategoryRep;
    private final WinePairingRepository winePairingRep;
    private final SmallCategoryRepository smallCategoryRep;
    private final JPAQueryFactory queryFactory;

    @Autowired
    public AdminService(AdminMapper MAPPER, @Value("${file.dir}") String FILE_DIR, ProductRepository productRep, FeatureRepository featureRep, SaleRepository saleRep, AromaRepository aromaRep, CountryRepository countryRep, CategoryRepository categoryRep, AromaCategoryRepository aromaCategoryRep, WinePairingRepository winePairingRep, SmallCategoryRepository smallCategoryRep, JPAQueryFactory queryFactory) {
        this.MAPPER = MAPPER;
        this.FILE_DIR = MyFileUtils.getAbsolutePath(FILE_DIR);
        this.productRep = productRep;
        this.featureRep = featureRep;
        this.saleRep = saleRep;
        this.aromaRep = aromaRep;
        this.countryRep = countryRep;
        this.categoryRep = categoryRep;
        this.aromaCategoryRep = aromaCategoryRep;
        this.winePairingRep = winePairingRep;
        this.smallCategoryRep = smallCategoryRep;
        this.queryFactory = queryFactory;
    }

    public Long postProduct(MultipartFile pic, ProductInsParam param) {
        //t_feature 인서트
        FeatureEntity featureEntity = FeatureEntity.builder()
                .acidity(param.getAcidity())
                .sweety(param.getSweety())
                .body(param.getBody())
                .build();
        FeatureEntity featureResult = featureRep.save(featureEntity);

        //t_product
        ProductEntity productEntity = ProductEntity.builder()
                .nmKor(param.getNmKor())
                .nmEng(param.getNmEng())
                .price(param.getPrice())
                .promotion(param.getPromotion())
                .beginner(param.getBeginner())
                .alcohol(param.getAlcohol())
                .quantity(param.getQuantity())
                .featureEntity(featureResult)
                .countryEntity(countryRep.getReferenceById(param.getCountry()))
                .categoryEntity(categoryRep.getReferenceById(param.getCategory()))
                .build();


        //t_sale
        SaleEntity saleEntity = SaleEntity.builder()
                .sale(param.getSale())
                .salePrice(param.getSalePrice())
                .startSale(param.getStartSale())
                .endSale(param.getEndSale())
                .productEntity(productEntity) // *******
                .build();

        //t_aroma
        AromaEntity aromaEntity = new AromaEntity();

        //페어링음식 t_wine_pairing에 인서트
        WinePairingEntity winePairingEntity = new WinePairingEntity();

        //사진 파일 업로드 로직 1 (사진업로드 하고 상품 등록할 때 실행되는 부분)
        //임시경로에 사진 저장
        if(pic != null) { //만약에 pic가 있다면
            File tempDic = new File(FILE_DIR, "/temp");
            if (!tempDic.exists()) { // /temp 경로에 temp폴더가 존재하지 않는다면 temp폴더를 만든다.
                tempDic.mkdirs();
            }

            String savedFileName = MyFileUtils.makeRandomFileNm(pic.getOriginalFilename());

            File tempFile = new File(tempDic.getPath(), savedFileName);

            try {
                pic.transferTo(tempFile);
            } catch (Exception e) {
                e.printStackTrace();
            }

            productEntity.setPic(savedFileName);

            //사진파일 업로드 로직2
            //로직2 안에 t_product 인서트
            //로직2 안에 t_sale 인서트
            //로직2 안에 t_aroma인서트
            //로직2 안에 t_winepairing인서트

            //t_product에 인서트
            ProductEntity productResult = productRep.save(productEntity);
            try {
                if (productResult == null) {
                    throw new Exception("상품을 등록할 수 없습니다.");
                }
            } catch (Exception e) {
                tempFile.delete();
                return 0L;
            }
            if (productResult != null) {
                String targetPath = FILE_DIR + "/winey/product/" + productResult.getProductId();
                File targetDic = new File(targetPath);
                if (!targetDic.exists()) {
                    targetDic.mkdirs();
                }
                File targetFile = new File(targetPath, savedFileName);
                tempFile.renameTo(targetFile);
                //t_sale 인서트
                saleRep.save(saleEntity);

                //t_aroma 인서트
                aromaEntity.setProductEntity(productResult);
                for (int i = 0; i < param.getAroma().size(); i++) {
                    AromaCategoryEntity aromaCategoryResult = aromaCategoryRep.getReferenceById(param.getAroma().get(i));
                    aromaEntity.setAromaCategoryEntity(aromaCategoryResult);
                    aromaRep.save(aromaEntity);
                }
                //페어링음식 t_wine_pairing에 인서트
                winePairingEntity.setProductEntity(productResult);

                for (int i = 0; i < param.getSmallCategoryId().size(); i++) {
                    SmallCategoryEntity smallCategoryResult = smallCategoryRep.getReferenceById(param.getSmallCategoryId().get(i));
                    winePairingEntity.setSmallCategoryEntity(smallCategoryResult);
                    winePairingRep.save(winePairingEntity);
                }
                return productResult.getProductId();
            }
        }
        //사진업로드 안하고 상품 등록할 때 실행되는 부분
        ProductEntity productResult = productRep.save(productEntity);
        // 할인율, 할인가격 t_sale에 인서트 (product_id 이용해서) , 할인시작일과 종료일은(3차 때 구현)
        saleRep.save(saleEntity);
        //t_aroma 인서트
        aromaEntity.setProductEntity(productResult);
        for (int i = 0; i < param.getAroma().size(); i++) {
            AromaCategoryEntity aromaCategoryResult = aromaCategoryRep.getReferenceById(param.getAroma().get(i));
            aromaEntity.setAromaCategoryEntity(aromaCategoryResult);
            aromaRep.save(aromaEntity);
        }
        //페어링음식 t_wine_pairing에 인서트
        winePairingEntity.setProductEntity(productResult);
        for (int i = 0; i < param.getSmallCategoryId().size(); i++) {
            SmallCategoryEntity smallCategoryResult = smallCategoryRep.getReferenceById(param.getSmallCategoryId().get(i));
            winePairingEntity.setSmallCategoryEntity(smallCategoryResult);
            winePairingRep.save(winePairingEntity);
        }
        return productResult.getProductId();
    }

    public Long putProduct(MultipartFile pic, ProductUpdParam param) {
        //수정할 상품의pk 가져오기
//        ProductEntity productEntity = productRep.findById(param.getProductId()).get();
        //t_product
        ProductEntity productEntity = ProductEntity.builder()
                .productId(productRep.findById(param.getProductId()).get().getProductId())
                .nmKor(param.getNmKor())
                .nmEng(param.getNmEng())
                .price(param.getPrice())
                .countryEntity(countryRep.getReferenceById(param.getCountry()))
                .categoryEntity(categoryRep.getReferenceById(param.getCategory()))
                .build();
        //t_sale
        SaleEntity saleEntity = SaleEntity.builder()
                .sale(param.getSale())
                .salePrice(param.getSalePrice())
                .productEntity(productEntity)
                .build();
        // 세일시작/종료날짜 로직
        LocalDate parseStartDate = LocalDate.parse(param.getStartSale(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));//String startSale을 LocalDate로 변환
        LocalDate parseEndDate = LocalDate.parse(param.getEndSale(),DateTimeFormatter.ofPattern("yyyy-MM-dd"));//String endSale을 LocalDate로 변환
        LocalDate startSale = parseStartDate.withDayOfMonth(1); //할인시작월의 1일
        LocalDate endSale = parseEndDate.withDayOfMonth(parseEndDate.lengthOfMonth()); //할인종료월의 마지막 일
        //t_feature
        FeatureEntity featureEntity = FeatureEntity.builder()
                .sweety(param.getSweety())
                .acidity(param.getAcidity())
                .body(param.getBody())
                .build();
        //t_aroma update
        //삭제
        aromaRep.deleteByProductEntity(productEntity);
        //인서트
        for(int i=0;i<param.getAroma().size();i++) {
            aromaRep.save(AromaEntity.builder()
                    .productEntity(productEntity)
                    .aromaCategoryEntity(aromaCategoryRep.getReferenceById(param.getAroma().get(i)))
                    .build());
        }
        //t_sale update
        //saleDate가 이번 달과 똑같은 달이면 실행되는 로직
        if(parseStartDate.getMonthValue() == LocalDate.now().getMonthValue()) {
            saleEntity.setStartSale(LocalDate.now().plusDays(1).toString());
            saleEntity.setEndSale(endSale.toString());
            saleRep.save(saleEntity);
        } else { //saleDate가 이번 달이 아니면 실행되는 로직
            saleEntity.setStartSale(startSale.toString());
            saleEntity.setEndSale(endSale.toString());
            saleRep.save(saleEntity);
        }
        //t_feature 테이블 update 하고 productEntity에 featureId를 set
        productEntity.setFeatureEntity(featureRep.save(featureEntity));

        //t_small_category table update (t_wine_pairing)
        //삭제
        winePairingRep.deleteByProductEntity(productEntity);

        //인서트
        for(int i=0;i<param.getSmallCategoryId().size();i++) {
            WinePairingEntity winePairingEntity = WinePairingEntity.builder()
                    .productEntity(productEntity)
                    .smallCategoryEntity(smallCategoryRep.getReferenceById(param.getSmallCategoryId().get(i)))
                    .build();
            winePairingRep.save(winePairingEntity);
        }
        //사진 파일 업로드 로직 1
        //임시경로에 사진 저장
        if(pic != null) { //만약에 pic가 있다면
            File tempDic = new File(FILE_DIR, "/temp");
            if(!tempDic.exists()) { // /temp 경로에 temp폴더가 존재하지 않는다면 temp폴더를 만든다.
                tempDic.mkdirs();
            }

            String savedFileName = MyFileUtils.makeRandomFileNm(pic.getOriginalFilename());

            File tempFile = new File(tempDic.getPath(), savedFileName);

            try{
                pic.transferTo(tempFile);
            } catch(Exception e) {
                e.printStackTrace();
            }
            //dto는 productupddto
            productEntity.setPic(savedFileName);
            //t_product테이블 update
            //사진 파일 업로드 로직 2
            ProductEntity result = productRep.save(productEntity);

            try {
                if(result == null) {
                    throw new Exception("상품을 등록할 수 없습니다.");
                }
            } catch(Exception e) {
                tempFile.delete();
                return 0L;
            }
            if (result == null) {
                String targetPath = FILE_DIR + "/winey/product/" + productEntity.getProductId();
                File targetDic = new File(targetPath);
                if(!targetDic.exists()) {
                    targetDic.mkdirs();
                }
                File targetFile = new File(targetPath, savedFileName);
                tempFile.renameTo(targetFile);
            }
            System.out.println("101010101010");
            return productEntity.getProductId(); //성공시 상품PK값 리턴
        }
        System.out.println("00000000000");
        //수정시 사진파일을 수정하지 않을 경우 (pic = null)
        ProductEntity result2 = productRep.save(productEntity);
        if(result2 != null) {
            return productEntity.getProductId();
        }
        return 0L; // result2가 0이면 수정에 실패했다는 의미로 0 리턴

    }

    //등록 상품 리스트 출력 (전체 상품)
    public Page<ProductDto> getProduct(int page, int row) {
        /* ok. join없는 select 쿼리문
        return queryFactory.select(new QProductDto(productEntity.productId, productEntity.nmKor, productEntity.price, productEntity.promotion, productEntity.beginner, productEntity.quantity))
                .from(productEntity)
                .fetch();

         */
        Pageable pageable = PageRequest.of(page,row);

        List<ProductDto> list = queryFactory.select(new QProductDto(productEntity.productId, productEntity.nmKor, productEntity.price, productEntity.promotion, productEntity.beginner, productEntity.quantity, saleEntity.sale, saleEntity.salePrice))
                .from(productEntity)
                .leftJoin(saleEntity)
                .on(saleEntity.productEntity.eq(productEntity))
                .offset(pageable.getPageSize())
                .limit(pageable.getOffset())
                .fetch();

        //count로직1
        Long count = queryFactory
                .select(productEntity.count())
                .from(productEntity)
                .leftJoin(saleEntity)
                .on(saleEntity.productEntity.eq(productEntity))
                .fetchOne();

        //count로직2
        JPAQuery<Long> countQuery = queryFactory
                .select(productEntity.count())
                .from(productEntity)
                .leftJoin(saleEntity)
                .on(saleEntity.productEntity.eq(productEntity));

        return PageableExecutionUtils.getPage(list, pageable, countQuery::fetchOne);
    }

    //querydsl 동적 정렬
//    public List<ProductDto> findAllOrderBy(OrderConditon orderConditon) {
//
//        return null;
//    }











}
