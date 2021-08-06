package com.midea.emall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import com.midea.emall.common.Constant;
import com.midea.emall.exception.MideaMallException;
import com.midea.emall.exception.MideaMallExceptionEnum;
import com.midea.emall.filter.UserFilter;
import com.midea.emall.model.dao.CartMapper;
import com.midea.emall.model.dao.OrderItemMapper;
import com.midea.emall.model.dao.OrderMapper;
import com.midea.emall.model.dao.ProductMapper;
import com.midea.emall.model.pojo.Order;
import com.midea.emall.model.pojo.OrderItem;
import com.midea.emall.model.pojo.Product;
import com.midea.emall.model.request.CreateOrderReq;
import com.midea.emall.model.vo.CartVO;
import com.midea.emall.model.vo.OrderItemVO;
import com.midea.emall.model.vo.OrderVO;
import com.midea.emall.service.CartService;
import com.midea.emall.service.OrderService;
import com.midea.emall.service.UserService;
import com.midea.emall.utils.OrderCodeFactory;
import com.midea.emall.utils.QRCodeGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
@Transactional(rollbackFor = Exception.class)
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    CartService cartService;
    @Autowired
    UserService userService;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    CartMapper cartMapper;
    @Autowired
    OrderItemMapper orderItemMapper;

    @Value("${file.upload.ip}")
    String ip;

    @Override
    public String create(CreateOrderReq createOrderReq) {
        //拿到用户ID
        Integer userId = UserFilter.currentUser.getId();
        //从购物车查找已勾选的商品
        ArrayList<CartVO> cartVOTempList = new ArrayList<>();
        List<CartVO> cartVOList = cartService.list(userId);

        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO = cartVOList.get(i);
            if (cartVO.getSelected().equals(Constant.Cart.CHECKED)) {
                cartVOTempList.add(cartVO);
            }
        }
        cartVOList = cartVOTempList;

        //如果没有勾选，则报错
        if (CollectionUtils.isEmpty(cartVOList)) {
            throw new MideaMallException(MideaMallExceptionEnum.CART_EMPTY);
        }
        //判断商品是否存在，上下架状态，库存
        validSaleStatusAndStock(cartVOList);
        //购物车对象 ===> 订单对象
        List<OrderItem> orderItemList = cartVOListToOrderItemList(cartVOList);
        //扣库存
        for (OrderItem orderItem : orderItemList) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            int stock = product.getStock() - orderItem.getQuantity();
            if (stock < 0) {
                throw new MideaMallException(MideaMallExceptionEnum.NOT_ENOUGH);
            }
            product.setStock(stock);
            productMapper.updateByPrimaryKeySelective(product);
        }

        //从购物车删除已勾选的商品
        cleanCart(cartVOList);

        //生成订单
        Order order = new Order();
        //生成订单号（有独立规则）
        String orderNo = OrderCodeFactory.getOrderCode(Long.valueOf(userId));
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice(orderItemList));
        order.setReceiverName(createOrderReq.getReceiverName());
        order.setReceiverMobile(createOrderReq.getReceiverMobile());
        order.setReceiverAddress(createOrderReq.getReceiverAddress());
        order.setOrderStatus(Constant.OrderStatusEnum.NOT_PAID.getCode());
        order.setPostage(0);
        order.setPaymentType(1);
        orderMapper.insertSelective(order);
        //循环保存每个商品至order_item表
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());   //添加订单编号
            orderItemMapper.insertSelective(orderItem);
        }
        //返回结果
        return orderNo;
    }

    @Override
    public OrderVO detail(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (orderNo == null) {
            throw new MideaMallException(MideaMallExceptionEnum.NO_ORDER);
        }
        Integer userId = UserFilter.currentUser.getId();
        if (!order.getUserId().equals(userId)) {
            throw new MideaMallException(MideaMallExceptionEnum.NOT_YOUR_ORDER);
        }
        //拼装OrderVO
        OrderVO orderVO = getOrderVO(order);
        return orderVO;
    }

    @Override
    public PageInfo listForCustomer(Integer pageNum, Integer pageSize) {
        Integer userId = UserFilter.currentUser.getId();
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectForCustomer(userId);
        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVOList);
        return pageInfo;
    }

    @Override
    public void cancel(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null){
            throw new MideaMallException(MideaMallExceptionEnum.NO_ORDER);
        }
        Integer userId = UserFilter.currentUser.getId();
        if (!order.getUserId().equals(userId)) {
            throw new MideaMallException(MideaMallExceptionEnum.NOT_YOUR_ORDER);
        }
        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAID.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.CANCELED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }else {
            throw new MideaMallException(MideaMallExceptionEnum.WRONG_ORDER_STATUS);
        }
        return;
    }

    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectAllForAdmin();
        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        PageInfo pageInfo = new PageInfo<>(orderList);
        pageInfo.setList(orderVOList);
        return pageInfo;
    }

    @Override
    public String qrcode(String orderNo) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

            String address = ip + ":" + request.getLocalPort();
            String payUrl = "http://" + address + "/pay?orderNo=" + orderNo;
        try{
            QRCodeGenerator.generateQRCodeImage(payUrl, 350, 350, Constant.FILE_UPLOAD_DIR + orderNo + ".png");
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String pngAddress = "http://" + address + "/images/" + orderNo + ".png";

        return pngAddress;
    }

    @Override
    public void pay(String orderNo) {

        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new MideaMallException(MideaMallExceptionEnum.NO_ORDER);
        }
        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAID.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.PAID.getCode());
            order.setPayTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new MideaMallException(MideaMallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    @Override
    public void deliver(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new MideaMallException(MideaMallExceptionEnum.NO_ORDER);
        }
        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.PAID.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.DELIVERED.getCode());
            order.setDeliveryTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }else{
            throw new MideaMallException(MideaMallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    @Override
    public void finish(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new MideaMallException(MideaMallExceptionEnum.NO_ORDER);
        }
        /**普通用户需要校验订单所属*/
        if (!userService.checkAdminRole(UserFilter.currentUser) &&
                !order.getUserId().equals(UserFilter.currentUser.getId())) {
            throw new MideaMallException(MideaMallExceptionEnum.NOT_YOUR_ORDER);
        }

        /**发货后才可以完结订单*/
        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.DELIVERED.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.FINISHED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }else{
            throw new MideaMallException(MideaMallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    private List<OrderVO> orderListToOrderVOList(List<Order> orderList) {
        List<OrderVO> orderVOList = new ArrayList<>();
        for (int i = 0; i < orderList.size(); i++) {
            Order order = orderList.get(i);
            OrderVO orderVO = getOrderVO(order);
            orderVOList.add(orderVO);
        }
        return orderVOList;
    }

    /**
     * 判断商品是否存在，上下架状态，库存
     */
    private void validSaleStatusAndStock(List<CartVO> cartVOList) {
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO = cartVOList.get(i);
            Product product = productMapper.selectByPrimaryKey(cartVO.getProductId());
            //是否上下架
            if (product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
                throw new MideaMallException(MideaMallExceptionEnum.NOT_SALE);
            }
            if (cartVO.getQuantity() > product.getStock()) {
                throw new MideaMallException(MideaMallExceptionEnum.NOT_ENOUGH);
            }
        }
    }

    /**
     * 购物车列表  转  订单列表
     */
    private List<OrderItem> cartVOListToOrderItemList(List<CartVO> cartVOList) {
        List<OrderItem> orderItemList = new ArrayList<>();
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO = cartVOList.get(i);
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartVO.getProductId());
            //记录商品快照信息
            orderItem.setProductName(cartVO.getProductName());
            orderItem.setProductImage(cartVO.getProductImg());
            orderItem.setUnitPrice(cartVO.getPrice());
            orderItem.setQuantity(cartVO.getQuantity());
            orderItem.setTotalPrice(cartVO.getTotalPrice());
            orderItemList.add(orderItem);
        }
        return orderItemList;
    }

    /**
     * 清除购物车中已下单的商品
     */
    private void cleanCart(List<CartVO> cartVOList) {
        for (CartVO cartVO : cartVOList) {
            cartMapper.deleteByPrimaryKey(cartVO.getId());
        }
    }


    /*计算订单总价*/
    private Integer totalPrice(List<OrderItem> orderItemList) {
        Integer totalPrice = 0;
        for (OrderItem orderItem : orderItemList) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    /**
     * 根据订单 ====> 订单视图
     */
    private OrderVO getOrderVO(Order order) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        //获取订单对应的OrderItemList
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
        List<OrderItemVO> orderItemVOList = new ArrayList<>();
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            OrderItemVO orderItemVO = new OrderItemVO();
            BeanUtils.copyProperties(orderItem, orderItemVO);
            orderItemVOList.add(orderItemVO);
        }
        //通过枚举类OrderStatusEnum中的codeOf(状态码.getValue())方法获取状态名称
        orderVO.setOrderItemVOList(orderItemVOList);
        orderVO.setOrderStatusName(Constant.OrderStatusEnum.codeOf(orderVO.getOrderStatus()).getValue());
        return orderVO;
    }

}
