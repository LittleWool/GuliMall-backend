package com.angenao.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.angenao.common.utils.PageUtils;
import com.angenao.gulimall.coupon.entity.MemberPriceEntity;

import java.util.Map;

/**
 * 商品会员价格
 *
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 14:43:14
 */
public interface MemberPriceService extends IService<MemberPriceEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

