package com.angenao.gulimall.product.service;

import com.angenao.gulimall.product.vo.AttrGroupRelationVo;
import com.angenao.gulimall.product.vo.AttrGroupWithAttrsVO;
import com.angenao.gulimall.product.vo.SpuItemAttrGroupVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.angenao.common.utils.PageUtils;
import com.angenao.gulimall.product.entity.AttrGroupEntity;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 12:26:45
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);


    void deleteAttrGroupRealtions(AttrGroupRelationVo[] attrGroupEntitys);

    List<AttrGroupWithAttrsVO> getAttrGroupWithAttrs(Long catelogId);

    List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId);
}

