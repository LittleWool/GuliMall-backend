package com.angenao.gulimall.product.service.impl;

import com.angenao.gulimall.product.entity.AttrEntity;
import com.angenao.gulimall.product.service.AttrService;
import com.angenao.gulimall.product.vo.BaseAttrs;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.angenao.gulimall.product.dao.ProductAttrValueDao;
import com.angenao.gulimall.product.entity.ProductAttrValueEntity;
import com.angenao.gulimall.product.service.ProductAttrValueService;
import org.springframework.transaction.annotation.Transactional;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void save(Long spuId, List<BaseAttrs> baseAttrs) {
        if (baseAttrs == null || baseAttrs.size() == 0) {

        }else {
            List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
                Long attrId = attr.getAttrId();
                ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
                AttrEntity attrEntity = attrService.getById(attrId);
                productAttrValueEntity.setAttrId(attrId);
                productAttrValueEntity.setAttrValue(attr.getAttrValues());
                productAttrValueEntity.setAttrName(attrEntity.getAttrName());
                productAttrValueEntity.setQuickShow(attr.getShowDesc());
                productAttrValueEntity.setSpuId(spuId);
                return productAttrValueEntity;
            }).collect(Collectors.toList());
            this.saveBatch(collect);
        }
    }

    @Override
    public List<ProductAttrValueEntity> baseListForSpu(Long spuId) {
        List<ProductAttrValueEntity> list = this.baseMapper.selectList(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        return list;
    }

    @Transactional
    @Override
    public void update(Long spuId, List<ProductAttrValueEntity> list) {
        this.baseMapper.delete(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        List<ProductAttrValueEntity> collect = list.stream().map(entity -> {
            entity.setSpuId(spuId);
            return entity;
        }).collect(Collectors.toList());
        this.saveBatch(collect);
    }

}