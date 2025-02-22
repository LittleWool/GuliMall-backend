package com.angenao.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName: Catalog2Vo
 * @Description:
 * @Author: Little Wool
 * @Date: 2024/12/16 13:24
 * @Version: 1.0
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catalog2Vo {
    //父分类id
    private String catalog1Id;

    private String id;

    private String name;

    private List<Catalog3Vo> catalog3List;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catalog3Vo{
        private String catalog2Id;
        private String id;
        private String name;
    }
}
