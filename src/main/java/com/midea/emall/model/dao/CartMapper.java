package com.midea.emall.model.dao;

import com.midea.emall.model.pojo.Cart;
import com.midea.emall.model.vo.CartVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectCartByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    List<CartVO> selectList(Integer userId);

    Integer selectOrNot(@Param("userId") Integer userId,@Param("productId") Integer productId, @Param("selected") Integer selected);
}