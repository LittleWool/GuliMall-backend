package com.angenao.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.angenao.common.utils.R;
import com.angenao.gulimall.product.entity.SkuImagesEntity;
import com.angenao.gulimall.product.entity.SpuInfoDescEntity;
import com.angenao.gulimall.product.feign.SeckillFeignService;
import com.angenao.gulimall.product.service.*;
import com.angenao.gulimall.product.vo.SeckillSkuVo;
import com.angenao.gulimall.product.vo.SkuItemSaleAttrVo;
import com.angenao.gulimall.product.vo.SkuItemVo;
import com.angenao.gulimall.product.vo.SpuItemAttrGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.Query;
import com.angenao.gulimall.product.dao.SkuInfoDao;
import com.angenao.gulimall.product.entity.SkuInfoEntity;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private SkuInfoDao skuInfoDao;
    @Autowired
    private SeckillFeignService seckillFeignService;


    public SkuInfoServiceImpl(SkuImagesServiceImpl skuImagesService) {
        this.skuImagesService = skuImagesService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByConditions(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(w -> {
                w.like("sku_name", key).or().like("sku_title", key).or().like("sku_subtitle", key);
            });
        }
        String catelogId = (String) params.get("catalogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            wrapper.eq("catalog_id", catelogId);
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id", brandId);
        }
        String min = (String) params.get("min");
        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(max) && !StringUtils.isEmpty(min)) {
            try {
                BigDecimal bigDecimalMin = new BigDecimal(min);
                BigDecimal bigDecimalMax = new BigDecimal(max);
                if (bigDecimalMax.compareTo(bigDecimalMin) == 1) {
                    wrapper.ge("price", min);
                    if (bigDecimalMax.compareTo(new BigDecimal("0")) == 1) {
                        wrapper.lt("price", max);
                    }
                } else {
                    wrapper.ge("price", max);
                    if (bigDecimalMin.compareTo(new BigDecimal("0")) == 1) {
                        wrapper.lt("price", min);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {

        List<SkuInfoEntity> list = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return list;
    }


    @Override
    public SkuItemVo getItemById(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = new SkuItemVo();

        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            //1、sku基本信息的获取  pms_sku_info
            SkuInfoEntity info = this.getById(skuId);
            skuItemVo.setInfo(info);
            return info;
        }, executor);


        CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync((res) -> {
            //3、获取spu的销售属性组合
            List<SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrBySpuId(res.getSpuId());
            skuItemVo.setSaleAttr(saleAttrVos);
        }, executor);


        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((res) -> {
            //4、获取spu的介绍    pms_spu_info_desc
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setDesc(spuInfoDescEntity);
        }, executor);


        CompletableFuture<Void> baseAttrFuture = infoFuture.thenAcceptAsync((res) -> {
            //5、获取spu的规格参数信息
            List<SpuItemAttrGroupVo> attrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId(), res.getCatalogId());
            skuItemVo.setGroupAttrs(attrGroupVos);
        }, executor);


        //2、sku的图片信息    pms_sku_images
        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
            List<SkuImagesEntity> imagesEntities = skuImagesService.getImagesBySkuId(skuId);
            skuItemVo.setImages(imagesEntities);
        }, executor);

        //保存秒杀信息

        CompletableFuture<Void> seckFuture = CompletableFuture.runAsync(() -> {
            R r = seckillFeignService.getSecKillInfo(skuId);
            if (r.getCode() == 0) {
                SeckillSkuVo seckillSkuVo = r.getData(new TypeReference<SeckillSkuVo>() {
                });
                long current = System.currentTimeMillis();
                //如果返回结果不为空且活动未过期，设置秒杀信息
                if (seckillSkuVo != null&&current<seckillSkuVo.getEndTime()) {
                    skuItemVo.setSeckillSkuVo(seckillSkuVo);
                }
            }
        }, executor);


        //等到所有任务都完成
        // CompletableFuture.allOf(saleAttrFuture, descFuture, baseAttrFuture, imageFuture, seckillFuture).get();

        CompletableFuture.allOf(saleAttrFuture, descFuture, baseAttrFuture, imageFuture,seckFuture).get();


        return skuItemVo;
    }

    @Override
    public Map<Long, BigDecimal> getCuttentPrice(List<Long> ids) {
        Collection<SkuInfoEntity> listedByIds = this.listByIds(ids);
        Map<Long, BigDecimal> collect = listedByIds.stream().collect(Collectors.toMap(SkuInfoEntity::getSkuId, SkuInfoEntity::getPrice));
        return collect;
    }

    @Override
    public Map<Long,SkuInfoEntity> getSkusToMap(Set<Long> skuIds) {
        Set<Long> set=new HashSet<>(skuIds);
        Collection<SkuInfoEntity> skuInfoEntities = listByIds(set);
        Map<Long, SkuInfoEntity> res = skuInfoEntities.stream().collect(Collectors.toMap(SkuInfoEntity::getSkuId, skuInfoEntity -> skuInfoEntity));

        return res;
    }


}