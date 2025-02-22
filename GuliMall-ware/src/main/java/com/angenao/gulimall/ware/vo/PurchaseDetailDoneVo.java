package com.angenao.gulimall.ware.vo;

import lombok.Data;

/**
 * @ClassName: PurchaseDetailDoneVo
 * @Description:
 * @Author: Little Wool
 * @Date: 2024/12/11 10:12
 * @Version: 1.0
 **/
@Data
public class PurchaseDetailDoneVo {

    //items: [{itemId:1,status:4,reason:""}]//完成/失败的需求详情
    private Long itemId;
    private int status;
    private String reason;
}
