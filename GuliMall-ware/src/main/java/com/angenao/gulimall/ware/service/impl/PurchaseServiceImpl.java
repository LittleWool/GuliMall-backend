package com.angenao.gulimall.ware.service.impl;

import com.alibaba.nacos.api.config.filter.IFilterConfig;
import com.angenao.common.constants.WareConstant;
import com.angenao.gulimall.ware.entity.PurchaseDetailEntity;
import com.angenao.gulimall.ware.service.PurchaseDetailService;
import com.angenao.gulimall.ware.vo.MergeVo;
import com.angenao.gulimall.ware.vo.PurchaseDetailDoneVo;
import com.angenao.gulimall.ware.vo.PurchaseDoneVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.Query;

import com.angenao.gulimall.ware.dao.PurchaseDao;
import com.angenao.gulimall.ware.entity.PurchaseEntity;
import com.angenao.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.ws.Action;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    PurchaseDetailService purchaseDetailService;
    @Autowired
    private WareSkuServiceImpl wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceiveList(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status",WareConstant.PurchaseStatusEnum.CREATED.getCode() )
        );
        return new PageUtils(page);
    }

    @Override
    public void mergePurchaseList(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        Long[] items = mergeVo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect = Arrays.stream(items).map(item -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(item);
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());

        purchaseDetailService.updateBatchById(collect);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

    @Override
    public void receive(List<Long> ids) {
        //1.查询采购单状态
        List<PurchaseEntity> collect = ids.stream().map(id -> {
            PurchaseEntity byId = this.getById(id);
            return byId;
        }).filter(purchaseEntity -> purchaseEntity.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
                purchaseEntity.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode())
                .map(purchaseEntity -> {
                    purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.RECEIVED.getCode());
                    purchaseEntity.setUpdateTime(new Date());
                    return purchaseEntity;
                }).collect(Collectors.toList());

        //2.更新采购单状态
        this.updateBatchById(collect);
        //3.更新采购项状态
        if (collect != null && collect.size() > 0) {
            collect.forEach(purchaseEntity -> {
                List<PurchaseDetailEntity> detailEntities = purchaseDetailService.listDetailByPurchaseId(purchaseEntity.getId());
                List<PurchaseDetailEntity> entities = detailEntities.stream().map(entity -> {
                    PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                    detailEntity.setId(entity.getId());
                    detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                    return detailEntity;
                }).collect(Collectors.toList());
                purchaseDetailService.updateBatchById(entities);
            });
        }



    }

    @Override
    @Transactional
    public void done(PurchaseDoneVO purchaseDoneVO) {
        //1.更新采购项目
        boolean purchaseSuccess=true;
        List<PurchaseDetailEntity> updates=new ArrayList<>();
        for (PurchaseDetailDoneVo item : purchaseDoneVO.getItems()) {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            if (item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()) {
                purchaseSuccess=false;
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode());
            }else {
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISHED.getCode());
                //采购成功入库//stock_locked  sku_name    stock   ware_id sku_id
                PurchaseDetailEntity detailEntity = purchaseDetailService.getById(item.getItemId());
                if (detailEntity!=null){
                    wareSkuService.addStock(detailEntity.getSkuId(),detailEntity.getWareId(),detailEntity.getSkuNum());
                }
            }
            purchaseDetailEntity.setId(item.getItemId());
            updates.add(purchaseDetailEntity);
        }
        purchaseDetailService.updateBatchById(updates);

        //2.更新采购单

        //TODO 应该检查所有采购项目是否都完成
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        if (purchaseSuccess) {

            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.FINISHED.getCode());
        }else {
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        }
        purchaseEntity.setId(purchaseDoneVO.getId());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);

    }

}