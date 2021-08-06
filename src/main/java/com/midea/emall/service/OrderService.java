package com.midea.emall.service;

import com.github.pagehelper.PageInfo;
import com.midea.emall.model.request.CreateOrderReq;
import com.midea.emall.model.vo.OrderVO;

public interface OrderService {
    String create(CreateOrderReq createOrderReq);

    OrderVO detail(String orderNo);

    PageInfo listForCustomer(Integer pageNum, Integer pageSize);

    void cancel(String orderNo);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    String qrcode(String orderNo);

    void pay(String orderNo);

    void deliver(String orderNo);

    void finish(String orderNo);
}
