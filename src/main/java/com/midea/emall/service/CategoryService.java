package com.midea.emall.service;

import com.github.pagehelper.PageInfo;
import com.midea.emall.model.pojo.Category;
import com.midea.emall.model.request.AddCategoryReq;
import com.midea.emall.model.vo.CategoryVO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CategoryService {

    void add(AddCategoryReq addCategoryReq);

    void update(Category category);

    void delete(Integer id);

    PageInfo listForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize);

    List<CategoryVO> listCategoryForCustomer();

    List<CategoryVO> listCategoryForCustomer(Integer categoryId);
}
