package com.angenao.gulimall.product.service.impl;

import com.angenao.gulimall.product.vo.Attr;
import com.angenao.gulimall.product.vo.SkuItemSaleAttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.Query;

import com.angenao.gulimall.product.dao.SkuSaleAttrValueDao;
import com.angenao.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.angenao.gulimall.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSaleAttr(Long skuId, List<Attr> attr) {
        List<SkuSaleAttrValueEntity> collect = attr.stream().map(skuAttr -> {
            SkuSaleAttrValueEntity saleAttrValueEntity = new SkuSaleAttrValueEntity();
            BeanUtils.copyProperties(skuAttr, saleAttrValueEntity);
            saleAttrValueEntity.setSkuId(skuId);
            return saleAttrValueEntity;
        }).collect(Collectors.toList());
        this.saveBatch(collect);
    }

    @Override
    public List<SkuItemSaleAttrVo> getSaleAttrBySpuId(Long spuId) {
        SkuSaleAttrValueDao skuSaleAttrValueDao = this.baseMapper;
        List<SkuItemSaleAttrVo> skuItemSaleAttrVos = skuSaleAttrValueDao.getSaleAttrBySpuId(spuId);
        return skuItemSaleAttrVos;
    }

    @Override
    public List<String> getSkuSaleAttrValueAsString(long skuId) {
        return baseMapper.getSkuSaleAttrValuesAsString(skuId);
    }

}