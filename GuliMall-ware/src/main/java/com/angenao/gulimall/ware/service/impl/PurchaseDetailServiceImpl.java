package com.angenao.gulimall.ware.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.Query;

import com.angenao.gulimall.ware.dao.PurchaseDetailDao;
import com.angenao.gulimall.ware.entity.PurchaseDetailEntity;
import com.angenao.gulimall.ware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         * key:
         * status:
         * wareId:
         */
        QueryWrapper<PurchaseDetailEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        //sku_id purchase_id
        if (StringUtils.isNotBlank(key)) {
            wrapper.and(w->{
                w.eq("sku_id",key).or().eq("purchase_id",key);
            });
        }
        String status = (String) params.get("status");
        if (StringUtils.isNotBlank(status)) {
            wrapper.and(w->{
                w.eq("status",status);
            });
        }

        String wareId = (String) params.get("wareId");
        if (StringUtils.isNotBlank(wareId)) {
            wrapper.and(w->{
                w.eq("ware_id",wareId);
            });
        }

        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<PurchaseDetailEntity> listDetailByPurchaseId(Long id) {
        List<PurchaseDetailEntity> list = this.list(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", id));
        return list;
    }

}