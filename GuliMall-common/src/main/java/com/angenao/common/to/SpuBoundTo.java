package com.angenao.common.to;

import com.angenao.common.constants.ProductConstant;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @ClassName: SpuBoundTo
 * @Description:
 * @Author: Little Wool
 * @Date: 2024/12/9 20:33
 * @Version: 1.0
 **/
@Data
public class SpuBoundTo {

    private Long spuId;

    private BigDecimal buyBounds;
    private BigDecimal growBounds;


}

