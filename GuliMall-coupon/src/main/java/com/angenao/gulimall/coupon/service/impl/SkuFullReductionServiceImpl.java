package com.angenao.gulimall.coupon.service.impl;

import com.angenao.common.to.MemberPrice;
import com.angenao.common.to.SkuReductionTo;
import com.angenao.gulimall.coupon.entity.MemberPriceEntity;
import com.angenao.gulimall.coupon.entity.SkuLadderEntity;
import com.angenao.gulimall.coupon.service.MemberPriceService;
import com.angenao.gulimall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.Query;

import com.angenao.gulimall.coupon.dao.SkuFullReductionDao;
import com.angenao.gulimall.coupon.entity.SkuFullReductionEntity;
import com.angenao.gulimall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    SkuLadderService skuLadderService;

    @Autowired
    MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void save(SkuReductionTo skuReductionTo) {
        //5.4sku的优惠信息 gulimall_sms->sms_sku_ladder,sms_sku_full_reduction,sms_member_price
       //1.阶梯价格
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(skuReductionTo, skuLadderEntity);
        skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
        if (skuReductionTo.getFullCount()>0){
            skuLadderService.save(skuLadderEntity);
        }
        //2.满减
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);
        skuFullReductionEntity.setAddOther(skuReductionTo.getCountStatus());
        if (skuReductionTo.getFullPrice().compareTo(new BigDecimal("0"))==1){
            this.save(skuFullReductionEntity);
        }
        //3.会员价

        List<MemberPrice> memberPrices = skuReductionTo.getMemberPrice();
        if (memberPrices != null && memberPrices.size() > 0) {
            List<MemberPriceEntity> collect = skuReductionTo.getMemberPrice().stream().map(memberPrice -> {
                MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
                memberPriceEntity.setAddOther(1);
                memberPriceEntity.setMemberLevelId(memberPrice.getId());
                memberPriceEntity.setMemberLevelName(memberPrice.getName());
                memberPriceEntity.setMemberPrice(memberPrice.getPrice());
                return memberPriceEntity;
            }).filter(priceEntity->{
                return priceEntity.getMemberPrice().compareTo(new BigDecimal("0"))==0;
            }).collect(Collectors.toList());
            memberPriceService.saveBatch(collect);
        }


    }

}