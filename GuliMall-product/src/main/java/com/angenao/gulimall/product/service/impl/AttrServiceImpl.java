package com.angenao.gulimall.product.service.impl;

import com.angenao.common.constants.ProductConstant;
import com.angenao.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.angenao.gulimall.product.dao.AttrGroupDao;
import com.angenao.gulimall.product.dao.CategoryDao;
import com.angenao.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.angenao.gulimall.product.entity.AttrGroupEntity;
import com.angenao.gulimall.product.entity.CategoryEntity;
import com.angenao.gulimall.product.service.AttrGroupService;
import com.angenao.gulimall.product.vo.AttrResVo;
import com.angenao.gulimall.product.vo.AttrVo;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.Query;

import com.angenao.gulimall.product.dao.AttrDao;
import com.angenao.gulimall.product.entity.AttrEntity;
import com.angenao.gulimall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {
    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private CategoryServiceImpl categoryService;
    @Autowired
    private AttrGroupDao attrGroupDao;
    @Autowired
    private AttrService attrService;
    @Autowired
    private AttrDao attrDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttrVo(AttrVo attrVo) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        this.save(attrEntity);
        if (attrVo.getAttrType()==ProductConstant.AttrTypeEnum.ATTR_TYPE_BASE.getCode()&&attrVo.getAttrGroupId()!=null){
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrId(attrEntity.getAttrId());
            relationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            attrAttrgroupRelationDao.insert(relationEntity);
        }

    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> map, Long catelogId, String attrType) {

        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) map.get("key");
        queryWrapper.eq("attr_type", attrType.equalsIgnoreCase("base") ? ProductConstant.AttrTypeEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrTypeEnum.ATTR_TYPE_SALE.getCode());
        if (catelogId != 0) {
            queryWrapper.eq("catelog_id", catelogId);
        }

        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(q -> {
                q.eq("attr_id", key).or().like("attr_name", key);
            });
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(map),
                queryWrapper
        );

        List<AttrEntity> records = page.getRecords();
        List<AttrResVo> resVos=null;
        if (records!=null && records.size()>0) {
            resVos= records.stream().map(attrEntity -> {
                AttrResVo attrResVo = new AttrResVo();
                BeanUtils.copyProperties(attrEntity, attrResVo);

                CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
                if (categoryEntity != null) {
                    attrResVo.setCatelogName(categoryEntity.getName());
                }
                if (attrType.equalsIgnoreCase("base")) {
                    AttrAttrgroupRelationEntity attrgroupRelationEntity = attrAttrgroupRelationDao.
                            selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
//                    if (attrgroupRelationEntity != null && attrgroupRelationEntity.getAttrGroupId() != 0) {
                    if (attrgroupRelationEntity != null) {
                        AttrGroupEntity attrGroupEntity = attrGroupService.getById(attrgroupRelationEntity.getAttrGroupId());
                        if (attrGroupEntity != null) {
                            attrResVo.setGroupName(attrGroupEntity.getAttrGroupName());
                        }
                    }
                }
                return attrResVo;

            }).collect(Collectors.toList());
        }

        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(resVos);
        return pageUtils;
    }

    @Override
    public AttrResVo getAttrInfo(Long attrId) {
        AttrEntity attrEntity = this.getById(attrId);
        AttrResVo attrResVo = new AttrResVo();
        BeanUtils.copyProperties(attrEntity, attrResVo);
        CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
        if (categoryEntity != null) {
            attrResVo.setCatelogName(categoryEntity.getName());
        }
        if (attrEntity.getAttrType() == ProductConstant.AttrTypeEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity attrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if (attrgroupRelationEntity != null && attrgroupRelationEntity.getAttrGroupId() != 0) {
                AttrGroupEntity attrGroupEntity = attrGroupService.getById(attrgroupRelationEntity.getAttrGroupId());
                if (attrGroupEntity != null) {
                    attrResVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }

        if (attrEntity.getCatelogId() != null) {
            attrResVo.setCatelogPath(categoryService.findLongPath(attrEntity.getCatelogId()));
        }

        return attrResVo;
    }

    @Transactional
    @Override
    public void updateCascade(AttrVo attrVo) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        this.updateById(attrEntity);

        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrGroupId(attrVo.getAttrGroupId());
        relationEntity.setAttrId(attrVo.getAttrId());

        Integer count = attrAttrgroupRelationDao.selectCount(new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrVo.getAttrId()));
        if (count > 0) {
            attrAttrgroupRelationDao.update(relationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrVo.getAttrId()));
        }else {
            attrAttrgroupRelationDao.insert(relationEntity);
        }

    }

    @Override
    public List<AttrEntity> getAttrGroupAttrs(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> attrGroupRealtions = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
        List<Long> attrIds = attrGroupRealtions.stream().map((relation) -> {
            return relation.getAttrId();
        }).collect(Collectors.toList());
        Collection<AttrEntity> attrEntities= Collections.emptyList();
        if (attrIds!=null && attrIds.size()>0) {
            attrEntities = listByIds(attrIds);
        }
        return (List<AttrEntity>) attrEntities;
    }

    @Override
    public PageUtils getNoRalations(Map<String, Object> params, Long attrgroupId) {
        //获取分类下分组
        AttrGroupEntity attrGroupEntity = attrGroupService.getById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        List<AttrGroupEntity> attrGroups = attrGroupService.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<Long> groupIds = attrGroups.stream().map((ag) -> ag.getAttrGroupId()).collect(Collectors.toList());
        //查询分组下所有属性
        QueryWrapper<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntityQueryWrapper = new QueryWrapper<>();
        if (groupIds != null && groupIds.size()>0) {
            attrAttrgroupRelationEntityQueryWrapper.in("attr_group_id", groupIds);
        }
        List<AttrAttrgroupRelationEntity> relations = attrAttrgroupRelationDao.selectList(attrAttrgroupRelationEntityQueryWrapper);
        List<Long> attrs = relations.stream().map((e) -> e.getAttrId()).collect(Collectors.toList());
        //查询分类下属性

        QueryWrapper<AttrEntity> attrEntityQueryWrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId);
        if (attrs != null && attrs.size()>0) {
            attrEntityQueryWrapper.and((e) -> e.notIn("attr_id", attrs));
        }
        String key = (String)params.get("key");
        if (!StringUtils.isEmpty(key)){
            attrEntityQueryWrapper.and((w)->{
                w.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        attrEntityQueryWrapper.and(wrapper->{wrapper.eq("attr_type",ProductConstant.AttrTypeEnum.ATTR_TYPE_BASE.getCode());});
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), attrEntityQueryWrapper);
        PageUtils pageUtils = new PageUtils(page);
        return pageUtils;
    }

    @Override
    public List<Long> getSearchAttrs(List<Long> ids) {
        List<Long>   list=baseMapper.selectSearchAttrIds(ids);
        return list;
    }
}