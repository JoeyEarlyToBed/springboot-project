package com.midea.emall.service.impl;

import com.midea.emall.common.Constant;
import com.midea.emall.exception.MideaMallException;
import com.midea.emall.exception.MideaMallExceptionEnum;
import com.midea.emall.model.dao.CartMapper;
import com.midea.emall.model.dao.ProductMapper;
import com.midea.emall.model.pojo.Cart;
import com.midea.emall.model.pojo.Product;
import com.midea.emall.model.vo.CartVO;
import com.midea.emall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {


    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;


    @Override
    public List<CartVO> add(Integer userId, Integer productId, Integer count) {
        validProduct(productId, count);
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //此商品不在购物车中
            cart = new Cart();
            cart.setProductId(productId);
            cart.setUserId(userId);
            cart.setQuantity(count);
            cart.setSelected(Constant.Cart.CHECKED);
            cartMapper.insertSelective(cart);
        }else{
            count = cart.getQuantity() + count;
            Cart newCart = new Cart();
            newCart.setQuantity(cart.getQuantity());
            newCart.setId(cart.getId());
            newCart.setProductId(cart.getProductId());
            newCart.setUserId(cart.getUserId());
            newCart.setSelected(Constant.Cart.CHECKED);
            cartMapper.updateByPrimaryKeySelective(newCart);
        }
        return this.list(userId);
    }

    @Override
    public List<CartVO> list(Integer userId) {
        List<CartVO> cartVOList = cartMapper.selectList(userId);
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO = cartVOList.get(i);
            cartVO.setTotalPrice(cartVO.getPrice() * cartVO.getQuantity());
        }
        return cartVOList;
    }

    @Override
    public List<CartVO> update(Integer userId, Integer productId, Integer count) {
        validProduct(productId, count);
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            throw new MideaMallException(MideaMallExceptionEnum.UPDATE_FAIL);
        }else{
            Cart cartNew = new Cart();
            cartNew.setQuantity(count);
            cartNew.setId(cart.getId());
            cartNew.setProductId(cart.getProductId());
            cartNew.setSelected(Constant.Cart.CHECKED);
            cartMapper.updateByPrimaryKeySelective(cartNew);
        }
        return this.list(userId);
    }

    @Override
    public List<CartVO> delete(Integer userId, Integer productId) {
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            throw new MideaMallException(MideaMallExceptionEnum.DELETE_FAILED);
        }else{
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
        return this.list(userId);
    }

    @Override
    public List<CartVO> selectOrNot(Integer userId, Integer productId, Integer selected) {
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            throw new MideaMallException(MideaMallExceptionEnum.UPDATE_FAIL);
        }else{
            cartMapper.selectOrNot(userId, productId, selected);
        }
        return this.list(userId);

    }
    @Override
    public List<CartVO> selectOrNot(Integer userId, Integer selected) {

        cartMapper.selectOrNot(userId, null, selected);
        return this.list(userId);

    }



    /**
     * 异常解决方案
     * @param productId
     * @param count
     */
    private void validProduct(Integer productId, Integer count) {
        Product product = productMapper.selectByPrimaryKey(productId);
        //判断商品是否存在 && 商品是否上架
        if (product == null || !product.getStatus().equals(Constant.SaleStatus.SALE)) {
            throw new MideaMallException(MideaMallExceptionEnum.NOT_SALE);
        }
        //判断库存
        if (count > product.getStock()) {
            throw new MideaMallException(MideaMallExceptionEnum.NOT_ENOUGH);
        }
    }
}
