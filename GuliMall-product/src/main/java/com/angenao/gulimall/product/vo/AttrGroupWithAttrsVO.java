package com.angenao.gulimall.product.vo;

import com.angenao.gulimall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: AttrGroupWithAttrsVO
 * @Description:
 * @Author: Little Wool
 * @Date: 2024/11/29 22:06
 * @Version: 1.0
 **/
@Data
public class AttrGroupWithAttrsVO {
    /**
     * 分组id
     */
    @TableId
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    List<AttrEntity> attrs;
}
