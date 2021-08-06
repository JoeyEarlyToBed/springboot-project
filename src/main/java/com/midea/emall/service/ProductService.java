package com.midea.emall.service;

import com.github.pagehelper.PageInfo;
import com.midea.emall.model.pojo.Product;
import com.midea.emall.model.request.AddProductReq;
import com.midea.emall.model.request.ProductListReq;
import org.springframework.web.bind.annotation.RequestParam;

public interface ProductService {
    public void add(AddProductReq addProductReq);

    void update(Product product);

    void delete(Integer id);

    void batchUpdateSellStatus(@RequestParam Integer[] ids, @RequestParam Integer sellStatus);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    Product  detail(Integer id);

    PageInfo  list(ProductListReq productListReq);
}
