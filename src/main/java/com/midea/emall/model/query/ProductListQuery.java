package com.midea.emall.model.query;

import java.util.List;

/**
 * 查询商品列表的类
 */
public class ProductListQuery {

    private String keyword;

    //查某目录商品，也应查出此目录下的所有子目录
    private List<Integer> categoryIds;

    public String getKeyword() {

        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Integer> categoryIds) {
        this.categoryIds = categoryIds;
    }

}
