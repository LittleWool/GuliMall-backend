package com.angenao.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName: SkuReductionTo
 * @Description:
 * @Author: Little Wool
 * @Date: 2024/12/9 20:40
 * @Version: 1.0
 **/
@Data
public class SkuReductionTo {

    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;

}
