package com.angenao.gulimall.ware.service;

import com.angenao.gulimall.ware.vo.MergeVo;
import com.angenao.gulimall.ware.vo.PurchaseDoneVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.angenao.common.utils.PageUtils;
import com.angenao.gulimall.ware.entity.PurchaseEntity;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 15:25:46
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceiveList(Map<String, Object> params);

    void mergePurchaseList(MergeVo mergeVo);

    void receive(List<Long> ids);

    void done(PurchaseDoneVO purchaseDoneVO);
}

