package com.midea.emall.controller;

import com.github.pagehelper.PageInfo;
import com.midea.emall.common.ApiRestResponse;
import com.midea.emall.model.request.CreateOrderReq;
import com.midea.emall.model.vo.OrderVO;
import com.midea.emall.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class OrderController {

    @Autowired
    OrderService orderService;


    @PostMapping("order/create")
    @ApiOperation("创建订单")
    public ApiRestResponse create(@RequestBody @Valid CreateOrderReq createOrderReq) {
        String orderNo = orderService.create(createOrderReq);
        return ApiRestResponse.success(orderNo);
    }

    @GetMapping("order/detail")
    @ApiOperation("订单详情查询")
    public ApiRestResponse detail(@RequestParam String orderNo) {
        OrderVO orderVO =orderService.detail(orderNo);
        return ApiRestResponse.success(orderVO);
    }

    @PostMapping("order/qrcode")
    @ApiOperation("生成支付二维码")
    public ApiRestResponse qrcode(@RequestParam String orderNo) {
        String pngAdress = orderService.qrcode(orderNo);
        return ApiRestResponse.success(pngAdress);
    }



    @GetMapping("order/list")
    @ApiOperation("前台订单列表")
    public ApiRestResponse list(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo pageInfo = orderService.listForCustomer(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }


    @PostMapping("order/cancel")
    @ApiOperation("取消订单")
    public ApiRestResponse cancel(@RequestParam String orderNo) {
        orderService.cancel(orderNo);
        return ApiRestResponse.success();
    }

    @GetMapping("pay")
    @ApiOperation("支付订单")
    public ApiRestResponse pay(@RequestParam String orderNo){
        orderService.pay(orderNo);
        return ApiRestResponse.success();
    }

}
