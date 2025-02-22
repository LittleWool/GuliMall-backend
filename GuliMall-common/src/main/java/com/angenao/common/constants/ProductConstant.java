package com.angenao.common.constants;

/**
 * @ClassName: ProductCOnstant
 * @Description:
 * @Author: Little Wool
 * @Date: 2024/11/27 15:20
 * @Version: 1.0
 **/

public class ProductConstant {

    public enum AttrTypeEnum{
        ATTR_TYPE_BASE(1,"基本属性"),ATTR_TYPE_SALE(0,"销售属性");
        private int code;
        private String msg;
        private AttrTypeEnum(int code, String msg){
            this.code = code;
            this.msg = msg;
        }
        public int getCode() {
            return code;
        }
    }

    public enum ProductStatusEnum{
        NEW_SPU(0,"新建"),SPU_UP(1,"商品上架"),SPU_DOWN(2,"商品下架");
        private int code;
        private String msg;
        private ProductStatusEnum(int code, String msg){
            this.code = code;
            this.msg = msg;
        }
        public int getCode() {
            return code;
        }
    }
}
