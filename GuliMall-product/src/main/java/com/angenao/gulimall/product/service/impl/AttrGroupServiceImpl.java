package com.angenao.gulimall.product.service.impl;

import com.angenao.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.angenao.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.angenao.gulimall.product.entity.AttrEntity;
import com.angenao.gulimall.product.service.AttrAttrgroupRelationService;
import com.angenao.gulimall.product.service.AttrService;
import com.angenao.gulimall.product.vo.AttrGroupRelationVo;
import com.angenao.gulimall.product.vo.AttrGroupWithAttrsVO;
import com.angenao.gulimall.product.vo.SpuItemAttrGroupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.Query;

import com.angenao.gulimall.product.dao.AttrGroupDao;
import com.angenao.gulimall.product.entity.AttrGroupEntity;
import com.angenao.gulimall.product.service.AttrGroupService;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), new QueryWrapper<AttrGroupEntity>());

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<AttrGroupEntity>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(obj -> obj.eq("attr_group_id", key).or().like("attr_group_name", key));
        }

        if (catelogId == 0) {
            IPage page=this.page(new Query<AttrGroupEntity>().getPage(params), queryWrapper);
            return new PageUtils(page);
        } else {
            queryWrapper.eq("catelog_id", catelogId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), queryWrapper);
            return new PageUtils(page);
        }

    }


    @Override
    public void deleteAttrGroupRealtions(AttrGroupRelationVo[] attrGroupRelationVos) {
        List<AttrAttrgroupRelationEntity> relations = Arrays.stream(attrGroupRelationVos).map((item) -> {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, attrAttrgroupRelationEntity);
            return attrAttrgroupRelationEntity;
        }).collect(Collectors.toList());

       attrAttrgroupRelationDao.deleteRealtionBatch(relations);
    }

    @Override
    public List<AttrGroupWithAttrsVO> getAttrGroupWithAttrs(Long catelogId) {
        //1.查询分类下分组
        List<AttrGroupEntity> groups = attrGroupService.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        //2.查询分组下属性
        List<AttrGroupWithAttrsVO> collect = groups.stream().map(group -> {
            AttrGroupWithAttrsVO attrGroupWithAttrsVO = new AttrGroupWithAttrsVO();
            BeanUtils.copyProperties(group, attrGroupWithAttrsVO);
            List<AttrEntity> attrGroupAttrs = attrService.getAttrGroupAttrs(group.getAttrGroupId());
            attrGroupWithAttrsVO.setAttrs(attrGroupAttrs);
            return attrGroupWithAttrsVO;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
        //1、查出当前spu对应的所有属性的分组信息以及当前分组下的所有属性对应的值
        AttrGroupDao baseMapper = this.getBaseMapper();
        return baseMapper.getAttrGroupWithAttrsBySpuId(spuId,catalogId);
    }


}