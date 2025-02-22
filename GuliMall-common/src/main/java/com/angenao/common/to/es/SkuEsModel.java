package com.angenao.common.to.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName: SkuEsModel
 * @Description:
 * @Author: Little Wool
 * @Date: 2024/12/13 21:13
 * @Version: 1.0
 **/

@Data
public class SkuEsModel {

    private Long skuId;

    private Long spuId;

    private String skuTitle;

    private BigDecimal skuPrice;

    private String skuImg;

    private Long saleCount;

    private Boolean hasStock;

    private Long hotScore;

    private Long brandId;

    private Long catalogId;

    private String brandName;

    private String brandImg;

    private String catalogName;

    private List<Attr> attrs;

    @Data
    public static class Attr{
        private Long attrId;
        private String attrName;
        private String attrValue;
    }

}
