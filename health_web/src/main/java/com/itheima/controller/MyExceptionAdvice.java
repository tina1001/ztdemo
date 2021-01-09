package com.itheima.controller;

import com.itheima.entity.Result;
import com.itheima.exception.MyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * log日志：
 * info:  记录流程性的内容， 对重要业务时。
 * debug: 记录重要数据id,key
 * error: 记录异常信息 代替system.out, e.printStackTrace();
 */
@RestControllerAdvice
public class MyExceptionAdvice {

    private static final Logger log = LoggerFactory.getLogger(MyExceptionAdvice.class);

    /**
     * 业务异常的处理
     */
    @ExceptionHandler(MyException.class)
    public Result handleMyException(MyException e) {
        return new Result(false, e.getMessage());
    }

    /**
     * 未知异常处理
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        // 记录异常信息
        log.error("发生未知异常", e);
        return new Result(false, "发生未知异常，请联系管理员");
    }
}