package com.angenao.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.angenao.common.utils.PageUtils;
import com.angenao.gulimall.member.entity.MemberStatisticsInfoEntity;

import java.util.Map;

/**
 * 会员统计信息
 *
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 15:11:09
 */
public interface MemberStatisticsInfoService extends IService<MemberStatisticsInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

