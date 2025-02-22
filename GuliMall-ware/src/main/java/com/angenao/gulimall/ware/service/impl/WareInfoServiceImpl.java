package com.angenao.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.angenao.common.to.SkuHasStockTo;
import com.angenao.common.utils.R;
import com.angenao.gulimall.ware.feign.MemberFeignService;
import com.angenao.gulimall.ware.vo.FareVo;
import com.angenao.gulimall.ware.vo.MemberAddressVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.Query;

import com.angenao.gulimall.ware.dao.WareInfoDao;
import com.angenao.gulimall.ware.entity.WareInfoEntity;
import com.angenao.gulimall.ware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<WareInfoEntity> wareInfoEntityQueryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wareInfoEntityQueryWrapper.eq("id",key).or()
                    .like("name",key)
                    .or().like("address",key)
                    .or().like("areacode",key);
        }

        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wareInfoEntityQueryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public FareVo getFare(long addrId) {
        R receiveAddresses = memberFeignService.getReceiveAddressByAddrId(addrId);
        MemberAddressVo data=null;
        if (receiveAddresses.getCode() == 0) {
            data = receiveAddresses.getData(new TypeReference<MemberAddressVo>() {
            });
        }
        FareVo fareVo = new FareVo();

        if (data != null ) {
            fareVo.setAddress(data);
            String phone = data.getPhone();
          fareVo.setFare(new BigDecimal(phone.substring(phone.length() - 1)));
            return fareVo;
        }
        fareVo.setFare(new BigDecimal(0));
        fareVo.setAddress(new MemberAddressVo());
        return fareVo;
    }


}