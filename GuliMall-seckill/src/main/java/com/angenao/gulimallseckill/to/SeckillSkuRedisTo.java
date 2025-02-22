package com.angenao.gulimallseckill.to;

import com.angenao.gulimallseckill.vo.SkuInfoVo;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 秒杀活动商品关联
 * 
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 14:43:14
 */
@Data
@TableName("sms_seckill_sku_relation")
public class SeckillSkuRedisTo {


	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * 活动id
	 */
	private Long promotionId;
	/**
	 * 活动场次id
	 */
	private Long promotionSessionId;
	/**
	 * 商品id
	 */
	private Long skuId;
	/**
	 * 秒杀价格
	 */
	private BigDecimal seckillPrice;
	/**
	 * 秒杀总量
	 */
	private Integer seckillCount;
	/**
	 * 每人限购数量
	 */
	private Integer seckillLimit;
	/**
	 * 排序
	 */
	private Integer seckillSort;

	private SkuInfoVo skuInfoVo;

	private Long startTime;
	private Long endTime;

	private String randomCode;
}
