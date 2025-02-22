package com.angenao.gulimall.product.service.impl;

import com.angenao.gulimall.product.vo.AttrGroupRelationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.Query;

import com.angenao.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.angenao.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.angenao.gulimall.product.service.AttrAttrgroupRelationService;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 批量保存关联关系
     * @param attrGroupRelationVos
     */
    @Override
    public void saveBathRelations(AttrGroupRelationVo... attrGroupRelationVos) {
        List<AttrAttrgroupRelationEntity> relationEntities = Arrays.stream(attrGroupRelationVos).map((relationVo) -> {
            AttrAttrgroupRelationEntity attrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(relationVo, attrgroupRelationEntity);
            return attrgroupRelationEntity;
        }).collect(Collectors.toList());
        this.saveBatch(relationEntities);
    }

}