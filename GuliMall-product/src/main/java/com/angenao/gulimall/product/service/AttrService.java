package com.angenao.gulimall.product.service;

import com.angenao.gulimall.product.vo.AttrResVo;
import com.angenao.gulimall.product.vo.AttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.angenao.common.utils.PageUtils;
import com.angenao.gulimall.product.entity.AttrEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 12:26:45
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttrVo(AttrVo attrVo);

    PageUtils queryBaseAttrPage(Map<String, Object> map, Long catelogId, String attrType);

    AttrResVo getAttrInfo(Long attrId);

    void updateCascade(AttrVo attrVo);

    List<AttrEntity> getAttrGroupAttrs(Long attrgroupId);

    PageUtils getNoRalations(Map<String, Object> params, Long attrgroupId);

    List<Long> getSearchAttrs(List<Long> ids);
}

