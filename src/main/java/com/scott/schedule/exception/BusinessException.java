package com.scott.schedule.exception;

import lombok.Getter;

/**
 * 业务异常
 *
 * @author meituan
 * @since 2024-01-01
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }
}

