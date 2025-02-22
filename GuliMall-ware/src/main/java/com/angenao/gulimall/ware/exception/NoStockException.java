package com.angenao.gulimall.ware.exception;

public class NoStockException extends RuntimeException {
    public NoStockException(String skuId) {
        System.out.println(skuId+"库存不足");

    }
}
