package com.cause.web.common.exception;

import lombok.extern.slf4j.Slf4j;

import java.util.NoSuchElementException;

@Slf4j
public class ExceptionWrapper {

    public static <T> Result<T> wrap(WrappedFunction<T> callable) {
        try {
            T data = callable.apply();
            return Result.success(data);
        } catch (IllegalArgumentException e) {// 客户端调用异常
            log.warn("参数异常:{}", e.getMessage(), e);
            return Result.fail(Result.PARAM_ERROR_CODE, e.getMessage());
        }  catch (NoSuchElementException e) {// 数据异常
            log.warn("数据不存在:{}", e.getMessage(), e);
            return Result.fail(Result.DATA_NOT_FOUND_CODE, e.getMessage());
        } catch (RuntimeException e) {
            log.error("运行时异常:{}", e.getMessage(), e);
            return Result.fail(Result.UNKNOWN_ERROR_CODE, e.getMessage());
        }
    }

    @FunctionalInterface
    public interface WrappedFunction<T> {
        T apply() throws RuntimeException;
    }
}
