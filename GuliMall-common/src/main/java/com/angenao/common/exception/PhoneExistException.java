package com.angenao.common.exception;

/**
 * @ClassName: PhoneExistException
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/11 13:22
 * @Version: 1.0
 **/

public class PhoneExistException extends RuntimeException {

    public PhoneExistException() {
        super("该手机号已经注册");
    }

}
