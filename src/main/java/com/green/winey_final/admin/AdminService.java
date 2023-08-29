package com.green.winey_final.admin;

import com.green.winey_final.admin.model.*;
import com.green.winey_final.common.entity.*;
import com.green.winey_final.repository.*;
//import com.green.winey_final.common.utils.MyFileUtils;
import com.green.winey_final.utils.MyFileUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

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
//    private final PagingAndSortRepository pagingAndSortRep;
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
        FeatureEntity featureEntity = new FeatureEntity();
        featureEntity.setAcidity(param.getAcidity());
        featureEntity.setSweety(param.getSweety());
        featureEntity.setBody(param.getBody());
        System.out.println("1111111111111");
        FeatureEntity featureResult = featureRep.save(featureEntity);

        System.out.println("피처리절트"+featureResult.toString());

        System.out.println("222222222222");
        //t_product
        ProductEntity productEntity = new ProductEntity();

        productEntity.setNmKor(param.getNmKor());
        productEntity.setNmEng(param.getNmEng());
        productEntity.setPrice(param.getPrice());
        productEntity.setPromotion(param.getPromotion());
        productEntity.setBeginner(param.getBeginner());
        productEntity.setAlcohol(param.getAlcohol());
        productEntity.setQuantity(param.getQuantity());
        //productEntity에 countryId set하는 부분
        CountryEntity countryResult = countryRep.getReferenceById(param.getCountry());
        System.out.println("33333333333");
        productEntity.setCountryEntity(countryResult);
        // featureId set하는 부분 //확인 필요
        productEntity.setFeatureEntity(featureResult);
        // categoryId set하는 부분 //확인 필요
        CategoryEntity categoryResult = categoryRep.getReferenceById(param.getCategory());
        productEntity.setCategoryEntity(categoryResult);

        System.out.println("컨츄리프로덕트"+productEntity.getCountryEntity().toString());
//        productEntity.getCountryEntity().toString();


        //t_sale
        SaleEntity saleEntity = new SaleEntity();

        saleEntity.setSale(param.getSale());
        saleEntity.setSalePrice(param.getSalePrice());
        saleEntity.setStartSale(param.getStartSale());
        saleEntity.setEndSale(param.getEndSale());

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
            //사진 파일 업로드 로직 2
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
//                SaleEntity saleResult = saleRep.save(saleEntity);
                saleRep.save(saleEntity);

                //t_aroma 인서트
                aromaEntity.setProductEntity(productResult);
                for (int i = 0; i < param.getAroma().size(); i++) {
                    AromaCategoryEntity aromaCategoryResult = aromaCategoryRep.getReferenceById(param.getAroma().get(i));
                    aromaEntity.setAromaCategoryEntity(aromaCategoryResult);
//                    AromaEntity aromaResult = aromaRep.save(aromaEntity);
                    aromaRep.save(aromaEntity);
                }
                //페어링음식 t_wine_pairing에 인서트
                winePairingEntity.setProductEntity(productResult);

                for (int i = 0; i < param.getSmallCategoryId().size(); i++) {
                    SmallCategoryEntity smallCategoryResult = smallCategoryRep.getReferenceById(param.getSmallCategoryId().get(i));
                    winePairingEntity.setSmallCategoryEntity(smallCategoryResult);
//                    WinePairingEntity winePairingResult = winePairingRep.save(winePairingEntity);
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
//                    AromaEntity aromaResult = aromaRep.save(aromaEntity);
            aromaRep.save(aromaEntity);
        }

        //페어링음식 t_wine_pairing에 인서트
        winePairingEntity.setProductEntity(productResult);
        for (int i = 0; i < param.getSmallCategoryId().size(); i++) {
            SmallCategoryEntity smallCategoryResult = smallCategoryRep.getReferenceById(param.getSmallCategoryId().get(i));
            winePairingEntity.setSmallCategoryEntity(smallCategoryResult);
//                    WinePairingEntity winePairingResult = winePairingRep.save(winePairingEntity);
            winePairingRep.save(winePairingEntity);
        }
        return productResult.getProductId();
    }

    public Long putProduct(MultipartFile pic, ProductUpdParam param) {


        return 1L;
    }

    //등록 상품 리스트 출력 (전체 상품)
    public List<ProductVo> getProduct(int page) {
//        queryFactory.selectFrom()
//        List<ProductVo> list =
        //페이징
        List<ProductEntity> productList1 = productRep.findAll();
        return productList1.stream().map(entity -> ProductVo.builder()
                .productId(entity.getProductId())
                .nmKor(entity.getNmKor())
                .price(entity.getPrice())
                .promotion(entity.getPromotion())
                .beginner(entity.getBeginner())
                .quantity(entity.getQuantity())
//                .sale()
//                .salePrice()
                .build()
        ).toList();

    }











}
