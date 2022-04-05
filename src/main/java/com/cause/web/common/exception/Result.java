package com.cause.web.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Result<T> {

    public static final int SUCCESS_CODE = 200;
    public static final int PARAM_ERROR_CODE = 400;
    public static final int DATA_NOT_FOUND_CODE = 404;
    public static final int INTERNAL_ERROR_CODE = 500;
    public static final int UNKNOWN_ERROR_CODE = 503;

    private int code;

    private String msg;

    private T data;

    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESS_CODE, "成功", data);
    }

    public static <T> Result<T> fail(String msg) {
        return new Result<>(INTERNAL_ERROR_CODE, msg, null);
    }

    public static <T> Result<T> fail(int code, String msg) {
        return new Result<>(code, msg, null);
    }

}
