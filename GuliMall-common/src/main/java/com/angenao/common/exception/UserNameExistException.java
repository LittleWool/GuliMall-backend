package com.angenao.common.exception;

/**
 * @ClassName: UserNameExistException
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/11 13:24
 * @Version: 1.0
 **/

public class UserNameExistException extends RuntimeException {
    public UserNameExistException() {
        super("该用户名已被使用");
    }
}
