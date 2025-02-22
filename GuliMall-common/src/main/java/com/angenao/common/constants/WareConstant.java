package com.angenao.common.constants;

/**
 * @ClassName: PurchaseListStatusConstant
 * @Description:
 * @Author: Little Wool
 * @Date: 2024/12/10 21:46
 * @Version: 1.0
 **/

public class WareConstant {
    public enum PurchaseStatusEnum{
        CREATED(0,"新建"),ASSIGNED(1,"已分配"),
        RECEIVED(2,"已领取"),FINISHED(3,"已完成"),
        HASERROR(4,"异常");
        private int code;
        private String msg;
        private PurchaseStatusEnum(int code, String msg){
            this.code = code;
            this.msg = msg;
        }
        public int getCode() {
            return code;
        }
    }

    public enum PurchaseDetailStatusEnum{
        CREATED(0,"新建"),ASSIGNED(1,"已分配"),
        BUYING(2,"正在采购"),FINISHED(3,"已完成"),
        HASERROR(4,"采购失败");
        private int code;
        private String msg;
        private PurchaseDetailStatusEnum(int code, String msg){
            this.code = code;
            this.msg = msg;
        }
        public int getCode() {
            return code;
        }
    }
}
