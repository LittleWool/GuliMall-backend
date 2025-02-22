package com.angenao.common.to;

import lombok.Data;

/**
 * @ClassName: SkuHasStockTo
 * @Description:
 * @Author: Little Wool
 * @Date: 2024/12/14 14:40
 * @Version: 1.0
 **/
@Data
public class SkuHasStockTo {

    private Long skuId;
    private Boolean hasStock;

}
