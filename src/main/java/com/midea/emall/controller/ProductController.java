package com.midea.emall.controller;

import com.github.pagehelper.PageInfo;
import com.midea.emall.common.ApiRestResponse;
import com.midea.emall.model.pojo.Product;
import com.midea.emall.model.request.ProductListReq;
import com.midea.emall.service.ProductService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping("product/detail")
    @ApiOperation("前台商品详情")
    public ApiRestResponse detail(@RequestParam Integer id) {
        Product product = productService.detail(id);
        return ApiRestResponse.success(product);
    }

    @GetMapping("product/list")
    @ApiOperation("前台商品列表")
    public ApiRestResponse list(ProductListReq productListReq) {
        PageInfo list = productService.list(productListReq);
        return ApiRestResponse.success(list);
    }

}
