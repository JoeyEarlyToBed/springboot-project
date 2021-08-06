package com.midea.emall.exception;

/**
 * 描述：收拢异常的枚举类
 */
public enum MideaMallExceptionEnum {
    /**
     * 第一行罗列所有异常枚举描述,用逗号隔开
     */
    NEED_USER_NAME(100001, "用户名不能为空"),
    NEED_PASSWORD(10002, "密码不能为空"),
    PASSWORD_TOO_SHORT(10003, "密码长度不能小于8位"),
    NAME_EXIT(10004, "不允许重名！"),
    INSERT_FAIL(10005, "插入失败！"),
    SYSTEM_ERROR(20000, "系统异常！"),
    WRONG_PASSWORD(10006, "用户密码错误！"),
    NEED_LOGIN(10007, "用户未登录！"),
    UPDATE_FAIL(10008, "更新异常！"),
    NEED_ADMIN(10009, "无管理员权限！"),
    NAME_NOT_NULL(10010, "名字不能为空！"),
    CREATE_FAILED(10011, "新增失败！"),
    REQUEST_PARAM_ERROR(10012, "参数不能为空"),
    DELETE_FAILED(10013, "删除失败！"),
    MKDIR_FAILED(10014, "文件夹创建失败！"),
    UPLOAD_FAILED(10015, "图片上传失败！"),
    NOT_SALE(10016, "商品状态不可售"),
    NOT_ENOUGH(10017, "商品库存不足"),
    CART_EMPTY(10018, "购物车已勾选商品为空！"),
    NO_ENUM(10019, "未找到对应枚举"),
    NO_ORDER(10020, "订单不存在"),
    NOT_YOUR_ORDER(10021, "订单不属于你"),
    WRONG_ORDER_STATUS(10022, "订单状态不符！"),;

    /**
     * 异常码
     */
    Integer code;
    /**
     * 异常信息
     */
    String msg;


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    MideaMallExceptionEnum(Integer code, String msg) {
        this.msg = msg;
        this.code = code;
    }

}
