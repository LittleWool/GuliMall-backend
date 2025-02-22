package com.angenao.gulimall.ware.vo;

import lombok.Data;

/**
 * @ClassName: MergeVo
 * @Description:
 * @Author: Little Wool
 * @Date: 2024/12/10 21:42
 * @Version: 1.0
 **/
@Data
public class MergeVo {
    Long purchaseId; //整单id
    Long[] items;//合并项集合
}
