package com.midea.emall.controller;

import com.github.pagehelper.PageInfo;
import com.midea.emall.common.ApiRestResponse;
import com.midea.emall.common.Constant;
import com.midea.emall.exception.MideaMallExceptionEnum;
import com.midea.emall.model.pojo.Category;
import com.midea.emall.model.pojo.User;
import com.midea.emall.model.request.AddCategoryReq;
import com.midea.emall.model.request.UpdateCategoryReq;
import com.midea.emall.model.vo.CategoryVO;
import com.midea.emall.service.CategoryService;
import com.midea.emall.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
public class CategoryController {

    @Autowired
    UserService userService;

    @Autowired
    CategoryService categoryService;

    @ApiOperation("后台添加目录")
    @ResponseBody
    @PostMapping("admin/category/add")
    public ApiRestResponse addCategory(HttpSession session, @Valid @RequestBody AddCategoryReq addCategoryReq) {

        if (addCategoryReq.getName() == null) {
            return ApiRestResponse.error(MideaMallExceptionEnum.NAME_NOT_NULL);
        }

        User currentUser = (User) session.getAttribute(Constant.MIDEA_MALL_USER);
        if (currentUser == null) {
            return ApiRestResponse.error(MideaMallExceptionEnum.NEED_LOGIN);
        }

        //校验管理员身份
        boolean adminRole = userService.checkAdminRole(currentUser);
        if (adminRole) {
            categoryService.add(addCategoryReq);
            return ApiRestResponse.success();
        } else {
            return ApiRestResponse.error(MideaMallExceptionEnum.NEED_USER_NAME.NEED_ADMIN);
        }
    }


    @ApiOperation("后台更新目录")
    @ResponseBody
    @PostMapping("/admin/category/update")
    public ApiRestResponse update(@Valid @RequestBody UpdateCategoryReq updateCategoryReq, HttpSession session) {
//        session.getAttribute(Constant.MIDEA_MALL_USER)
        Category category = new Category();
        BeanUtils.copyProperties(updateCategoryReq, category);
        categoryService.update(category);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台删除目录")
    @ResponseBody
    @PostMapping("/admin/category/delete")
    public ApiRestResponse deleteCategory(@RequestParam Integer id) {
        categoryService.delete(id);
        return ApiRestResponse.success();
    }

    @ApiOperation("查询后台商品分类列表")
    @ResponseBody
    @PostMapping("/admin/category/list")
    public ApiRestResponse listCategoryForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo pageInfo = categoryService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("查询前台商品分类列表")
    @ResponseBody
    @PostMapping("/category/list")
    public ApiRestResponse listCategoryForCustomer() {
        List<CategoryVO> categoryVOs = categoryService.listCategoryForCustomer();
        return ApiRestResponse.success(categoryVOs);
    }

}
