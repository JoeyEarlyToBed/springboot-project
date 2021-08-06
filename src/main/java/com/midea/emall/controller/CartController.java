package com.midea.emall.controller;

import com.midea.emall.common.ApiRestResponse;
import com.midea.emall.filter.UserFilter;
import com.midea.emall.model.vo.CartVO;
import com.midea.emall.service.CartService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @PostMapping("/add")
    @ApiOperation("向购物车添加商品")
    public ApiRestResponse add(@RequestParam Integer productId, @RequestParam Integer count) {
        List<CartVO> cartVOList = cartService.add(UserFilter.currentUser.getId(), productId, count);
        return ApiRestResponse.success(cartVOList);
    }

    @GetMapping("/list")
    @ApiOperation(("购物车列表"))
    public ApiRestResponse list() {
        List<CartVO> cartVOList = cartService.list(UserFilter.currentUser.getId());
        return ApiRestResponse.success(cartVOList);
    }

    @PostMapping("/update")
    @ApiOperation("更新购物车")
    public ApiRestResponse update(@RequestParam Integer productId, @RequestParam Integer count) {
        List<CartVO> cartVOList = cartService.update(UserFilter.currentUser.getId(), productId, count);
        return ApiRestResponse.success(cartVOList);
    }

    @PostMapping("/delete")
    @ApiOperation("删除购物车")
    public ApiRestResponse delete(Integer productId) {

        List<CartVO> cartVOS = cartService.delete(UserFilter.currentUser.getId(), productId);
        return ApiRestResponse.success(cartVOS);
    }


    @PostMapping("/select")
    @ApiOperation("选中/选不中购物车的某件商品")
    public ApiRestResponse select(@RequestParam Integer productId, @RequestParam Integer selected) {
        List<CartVO> cartVOList = cartService.selectOrNot(UserFilter.currentUser.getId(), productId, selected);
        return ApiRestResponse.success(cartVOList);
    }

    @PostMapping("/selectAll")
    @ApiOperation("全选中/全选不中购物车")
    public ApiRestResponse selectAll( @RequestParam Integer selected) {
        List<CartVO> cartVOList = cartService.selectOrNot(UserFilter.currentUser.getId(),  selected);
        return ApiRestResponse.success(cartVOList);
    }

}
