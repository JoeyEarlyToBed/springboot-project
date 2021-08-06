package com.midea.emall.model.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 描述： AddCategoryReq，请求类
 */
public class AddCategoryReq {

    @Size(min = 2, max = 5)
    private String name;
    @NotNull
    @Max(3)
    private Integer type;
    @NotNull(message = "parentId不能为null")
    private Integer parentId;
    @NotNull
    private Integer orderNum;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public AddCategoryReq(String name, Integer type, Integer parentId, Integer orderNum) {
        this.name = name;
        this.type = type;
        this.parentId = parentId;
        this.orderNum = orderNum;
    }

    @Override
    public String toString() {
        return "AddCategoryReq{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", parentId=" + parentId +
                ", orderNum=" + orderNum +
                '}';
    }
}
