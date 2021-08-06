package com.midea.emall.exception;

public class MideaMallException extends RuntimeException {
    private final Integer code;
    private final String message;

    public MideaMallException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 传入异常类型的枚举 来 创建异常
     * @param exceptionEnum
     */
    public MideaMallException(MideaMallExceptionEnum exceptionEnum) {
        this(exceptionEnum.getCode(), exceptionEnum.getMsg());
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
