package com.java1234vote.exception;

import com.java1234vote.entity.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalException {

    /**
     *
     * @param r
     * @return
     */
    @ExceptionHandler(value = RuntimeException.class)
    public R handle(RuntimeException  r){
        System.out.println("运行时异常------------："+r.getMessage());
        log.error("运行时异常------------："+r.getMessage());
        return R.error(r.getMessage());
    }

}
