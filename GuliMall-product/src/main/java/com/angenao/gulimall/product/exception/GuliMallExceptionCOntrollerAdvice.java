package com.angenao.gulimall.product.exception;

import com.angenao.common.exception.BizCode;
import com.angenao.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: GuliMallExceptionCOntrollerAdvice
 * @Description:
 * @Author: Little Wool
 * @Date: 2024/11/25 19:08
 * @Version: 1.0
 **/
@Slf4j
@RestControllerAdvice(basePackages = "com.angenao.gulimall.product.controller")
public class GuliMallExceptionCOntrollerAdvice {

    /**
     * 数据校验异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> map = new HashMap<>();
        bindingResult.getFieldErrors().forEach((fieldError) -> {
            map.put(fieldError.getField(), fieldError.getDefaultMessage());
        });
        return R.error(BizCode.VALID_EXCEPTION.getCode(),BizCode.VALID_EXCEPTION.getMsg()).put("data",map);
    }


//    @ExceptionHandler(value = Exception.class)
//    public R handleValidException(Exception e) {
//        log.error("错误类型{},\t错误信息{}",BizCode.UNKNOWN_EXCEPTION.getCode(),BizCode.UNKNOWN_EXCEPTION.getMsg());
//        return R.error();
//    }

}
