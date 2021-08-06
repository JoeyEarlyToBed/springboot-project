package com.midea.emall.service;

import com.midea.emall.model.vo.CartVO;

import java.util.List;

public interface CartService {


    List<CartVO> add(Integer userId, Integer productId, Integer count);

    List<CartVO> list(Integer userId);

    List<CartVO> update(Integer id, Integer productId, Integer count);

    List<CartVO> delete(Integer userId, Integer productId);

    List<CartVO> selectOrNot(Integer id, Integer productId, Integer selected);

    List<CartVO> selectOrNot(Integer userId, Integer selected);
}
