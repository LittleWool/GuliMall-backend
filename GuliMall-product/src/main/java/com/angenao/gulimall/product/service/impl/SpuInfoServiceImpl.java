package com.angenao.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.angenao.common.constants.ProductConstant;
import com.angenao.common.to.SkuHasStockTo;
import com.angenao.common.to.SkuReductionTo;
import com.angenao.common.to.SpuBoundTo;
import com.angenao.common.to.es.SkuEsModel;
import com.angenao.common.utils.R;
import com.angenao.gulimall.product.entity.*;
import com.angenao.gulimall.product.feign.CouponServiceFeign;
import com.angenao.gulimall.product.feign.SearchServiceFeign;
import com.angenao.gulimall.product.feign.WareServiceFeign;
import com.angenao.gulimall.product.service.*;
import com.angenao.gulimall.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.Query;

import com.angenao.gulimall.product.dao.SpuInfoDao;
import org.springframework.util.StringUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {



    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    SpuImagesService spuImagesService;

    @Autowired
    private ProductAttrValueServiceImpl productAttrValueService;
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private SkuImagesServiceImpl skuImagesService;
    @Autowired
    private SkuSaleAttrValueServiceImpl skuSaleAttrValueService;
    @Autowired
    private CouponServiceFeign couponServiceFeign;
    @Autowired
    private BrandServiceImpl brandService;
    @Autowired
    private SpuInfoService spuInfoService;
    @Autowired
    private CategoryServiceImpl categoryService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private WareServiceFeign wareServiceFeign;
    @Autowired
    private SearchServiceFeign searchServiceFeign;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transient
    @Override
    public void save(SpuSaveVo vo) {
         //1.保存spu基本信息 pms_spu_info
        SpuInfoEntity baseInfo = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, baseInfo);
        baseInfo.setCreateTime(new Date());
        baseInfo.setUpdateTime(new Date());
        //this.save(spuInfoEntity);这样子事务会失效
        this.saveBaseSpuInfo(baseInfo);

        //2.保存spu描述图片   pms_spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(baseInfo.getId());
        spuInfoDescEntity.setDecript(String.join(",", decript));
        spuInfoDescService.save(spuInfoDescEntity);

        //3.保存spu图片       pms_spu_images
        List<String> images = vo.getImages();
        spuImagesService.saveImages(baseInfo.getId(),images);

        //4.保存spu规格参数   pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        productAttrValueService.save(baseInfo.getId(),baseAttrs);

        //5.保存spu-bouds信息   gulimall_sms->sms_spu_bounds
        Bounds bounds = vo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds,spuBoundTo);
        spuBoundTo.setSpuId(baseInfo.getId());
        R boudR = couponServiceFeign.save(spuBoundTo);
        if (boudR.getCode() != 0) {
            log.error("远程保存spu积分信息失败");
        }

        //5.保存spu相关的sku信息
        //5.1sku基本信息    pms_sku_info
        List<Skus> skus = vo.getSkus();
        for (Skus sku : skus) {
            String defaultImg = "";
            for (Images image : sku.getImages()) {
                if (image.getDefaultImg() == 1){
                    defaultImg=image.getImgUrl();
                }
            }
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(sku,skuInfoEntity);
            skuInfoEntity.setSpuId(baseInfo.getId());
            skuInfoEntity.setSaleCount(0L);
            skuInfoEntity.setBrandId(baseInfo.getBrandId());
            skuInfoEntity.setCatalogId(baseInfo.getCatalogId());
            skuInfoEntity.setSkuDefaultImg(defaultImg);
            skuInfoService.saveSkuInfo(skuInfoEntity);
            //5.2sku图片信息    pms_sku_images
            skuImagesService.save(skuInfoEntity.getSkuId(), sku.getImages());
            //5.3sku销售属性信息  pms_sku_sale_attr_value
            skuSaleAttrValueService.saveSaleAttr(skuInfoEntity.getSkuId(),sku.getAttr());
            //5.4sku的优惠信息 gulimall_sms->sms_sku_ladder,sms_sku_full_reduction,sms_member_price
            SkuReductionTo skuReductionTo = new SkuReductionTo();
            BeanUtils.copyProperties(sku,skuReductionTo);
            skuReductionTo.setSkuId(baseInfo.getId());
            if (skuReductionTo.getFullCount() > 0||skuReductionTo.getFullPrice().compareTo(new BigDecimal("0"))==1){
                R reducitionR = couponServiceFeign.saveSkuReduction(skuReductionTo);
                if (reducitionR.getCode() != 0) {
                    log.error("远程保存spu优惠信息失败");
                }
            }

        };


    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByConditions(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((w) -> {
                w.like("spu_name", key).or().like("spu_description", key);
            });
        }
        String catelogId = (String) params.get("catalogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            wrapper.eq("catelog_id", catelogId);
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id", brandId);
        }
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            wrapper.eq("publish_status", status);
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    //上架
    @Override
    public void up(Long spuId) {
        //查询spu下所有sku信息
       List<SkuInfoEntity>  list=skuInfoService.getSkusBySpuId(spuId);
       SpuInfoEntity spuInfo = spuInfoService.getById(spuId);
        CategoryEntity categoryEntity = categoryService.getById(spuInfo.getCatalogId());
        BrandEntity brandEntity = brandService.getById(spuInfo.getBrandId());
        List<Long> skuIds = list.stream().map((SkuInfoEntity::getSkuId)).collect(Collectors.toList());
        Map<Long,Boolean> stockMap = null;
        try {
            R r = wareServiceFeign.hasStock(skuIds);
            List<SkuHasStockTo> listHasStock = r.getData(new TypeReference<List<SkuHasStockTo>>() {});
            if (listHasStock != null && listHasStock.size() > 0) {
                stockMap = listHasStock.stream().collect(Collectors.toMap(SkuHasStockTo::getSkuId, SkuHasStockTo::getHasStock));
            }
        }catch (Exception e){
            log.error("库存服务异常：原因{}",e);
        }
        //查询当前sku所有可以被用来检索的规格属性
        //检索spu下所有属性
        List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.baseListForSpu(spuId);
        List<Long> ids = productAttrValueEntities.stream().map(entity -> {
            return entity.getAttrId();
        }).collect(Collectors.toList());
        List<Long> searchAttrId=attrService.getSearchAttrs(ids);
        Set<Long> set=new HashSet<>();
        set.addAll(ids);

        List<SkuEsModel.Attr> attrs= productAttrValueEntities.stream().filter(attr -> {
            return set.contains(attr.getAttrId());
        }).map(attr->{
            SkuEsModel.Attr attrEntity = new SkuEsModel.Attr();
            BeanUtils.copyProperties(attr,attrEntity);
            return attrEntity;
        }).collect(Collectors.toList());

        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> collect = list.stream().map(skuInfoEntity -> {
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(skuInfoEntity, esModel);
            esModel.setSkuImg(skuInfoEntity.getSkuDefaultImg());
            esModel.setSkuPrice(skuInfoEntity.getPrice());
            //TODO 1.远程调用查询库存
            if (finalStockMap ==null){
                esModel.setHasStock(true);
            }else {
                esModel.setHasStock(finalStockMap.get(skuInfoEntity.getSkuId()));
            }
            //TODO 2.热度评分
            esModel.setHotScore(0L);
            //设置品牌和分类信息
            esModel.setBrandName(brandEntity.getName());
            esModel.setBrandImg(brandEntity.getLogo());
            esModel.setCatalogName(categoryEntity.getName());
            esModel.setAttrs(attrs);
            return esModel;
        }).collect(Collectors.toList());
        //TODO 将收集好的商家数据发送给es保存
        R r = searchServiceFeign.productStatusUp(collect);
        if (r.getCode() == 0) {
            baseMapper.updateStatus(spuId, ProductConstant.ProductStatusEnum.SPU_UP.getCode());
        }

    }

    @Override
    public Map<Long,SpuInfoEntity> getSpuBySkuIds(List<Long> skuIds) {
        //查出所有sku对应的spuid并去重
        List<SkuInfoEntity> skus = skuInfoService.list(new QueryWrapper<SkuInfoEntity>().in("sku_id", skuIds));
        //查出所有的spu，
        Set<Long> spuIds = skus.stream().map(SkuInfoEntity::getSpuId).collect(Collectors.toSet());
        List<SpuInfoEntity> spuInfoEntities = this.list(new QueryWrapper<SpuInfoEntity>().in("id", spuIds));
        //查出所有brandname并赋值
        Set<Long> brandIds = spuInfoEntities.stream().map(SpuInfoEntity::getBrandId).collect(Collectors.toSet());
        List<BrandEntity> brandEntities = brandService.list(new QueryWrapper<BrandEntity>().in("brand_id", brandIds));

        //为spuEntity赋值
        Map<Long, String> idNameMap = brandEntities.stream().collect(Collectors.toMap(BrandEntity::getBrandId, BrandEntity::getName));
        for (SpuInfoEntity spuInfoEntity : spuInfoEntities) {
            spuInfoEntity.setSpuName(idNameMap.get(spuInfoEntity.getBrandId()));
        }
        //找到sku->spu
        Map<Long, SpuInfoEntity> collect = spuInfoEntities.stream().collect(Collectors.toMap(SpuInfoEntity::getId, (spu) -> {return spu;}));
        Map<Long, SpuInfoEntity> skuMapSpu = skus.stream().collect(Collectors.toMap(SkuInfoEntity::getSkuId, (sku) -> {
            return collect.get(sku.getSpuId());}));
        return skuMapSpu;
    }


}