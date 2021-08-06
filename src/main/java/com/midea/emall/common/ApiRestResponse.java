package com.midea.emall.common;

import com.midea.emall.exception.MideaMallExceptionEnum;

public class ApiRestResponse<T> {
    private Integer status;
    private String msg;
    private T data;

    private static final int OK_CODE = 100000;
    private static final String  OK_MSG = "SUCCESS";


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static int getOkCode() {
        return OK_CODE;
    }

    public static String getOkMsg() {
        return OK_MSG;
    }

    public static <T> ApiRestResponse<T> success() {
        return new ApiRestResponse<T>();
    }

    public static <T> ApiRestResponse<T> success(T result) {
        ApiRestResponse response = new ApiRestResponse<>();
        response.setData(result);
        return response;
    }

    //通过异常信息code，msg，创建异常处理对象
    public static <T> ApiRestResponse<T> error(Integer code, String msg) {
        return new ApiRestResponse<>(code, msg);
    }
    //通过异常枚举，创建异常处理对象
    public static <T> ApiRestResponse<T> error(MideaMallExceptionEnum ex) {
        return new ApiRestResponse<T>(ex.getCode(), ex.getMsg());
    }



    public ApiRestResponse() {
        this(OK_CODE, OK_MSG);
    }

    public ApiRestResponse(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public ApiRestResponse(Integer status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
}
