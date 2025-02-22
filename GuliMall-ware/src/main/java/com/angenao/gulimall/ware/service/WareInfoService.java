package com.angenao.gulimall.ware.service;

import com.angenao.common.to.SkuHasStockTo;
import com.angenao.gulimall.ware.vo.FareVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.angenao.common.utils.PageUtils;
import com.angenao.gulimall.ware.entity.WareInfoEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 仓库信息
 *
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 15:25:46
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);


    FareVo getFare(long addrId);
}

