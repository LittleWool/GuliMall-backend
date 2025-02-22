package com.angenao.gulimall.product.dao;

import com.angenao.common.constants.ProductConstant;
import com.angenao.gulimall.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 12:26:45
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {



    void updateStatus(@Param("spuId") Long spuId,@Param("StatusCode") int code);
}
