package com.angenao.gulimall.product.vo;

import com.angenao.gulimall.product.entity.AttrEntity;
import lombok.Data;

/**
 * @ClassName: AttrBaseListVo
 * @Description:
 * @Author: Little Wool
 * @Date: 2024/11/27 12:02
 * @Version: 1.0
 **/

@Data
public class AttrResVo extends AttrEntity {
    private String groupName;
    public String catelogName;
    private Long[] catelogPath;
}
