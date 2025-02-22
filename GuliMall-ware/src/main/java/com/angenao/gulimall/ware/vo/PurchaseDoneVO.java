package com.angenao.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName: PurchaseDoneVO
 * @Description:
 * @Author: Little Wool
 * @Date: 2024/12/11 10:11
 * @Version: 1.0
 **/
@Data
public class PurchaseDoneVO {
//{
//   id: 123,//采购单id
//   items: [{itemId:1,status:4,reason:""}]//完成/失败的需求详情
//}
    private Long id;

    private List<PurchaseDetailDoneVo> items;
}
