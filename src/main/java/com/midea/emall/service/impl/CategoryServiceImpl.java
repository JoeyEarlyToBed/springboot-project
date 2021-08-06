package com.midea.emall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.midea.emall.exception.MideaMallException;
import com.midea.emall.exception.MideaMallExceptionEnum;
import com.midea.emall.model.dao.CategoryMapper;
import com.midea.emall.model.pojo.Category;
import com.midea.emall.model.request.AddCategoryReq;
import com.midea.emall.model.vo.CategoryVO;
import com.midea.emall.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public void add(AddCategoryReq addCategoryReq) {
        Category category = new Category();
        BeanUtils.copyProperties(addCategoryReq, category);
        Category categoryOld = categoryMapper.selectByName(addCategoryReq.getName());
        if (categoryOld != null) {
            throw new MideaMallException(MideaMallExceptionEnum.NAME_EXIT);
        }

        int count = categoryMapper.insertSelective(category);
        if (count == 0) {
            throw new MideaMallException(MideaMallExceptionEnum.CREATE_FAILED);
        }
    }

    @Override
    public void update(Category updateCategory) {
        //如果更新的目录不为空
        if (updateCategory.getName() != null) {
            Category oldCategory = categoryMapper.selectByName(updateCategory.getName());
            //如果该目录存在，并且，id值不相同
            if (oldCategory != null && !oldCategory.getId().equals(updateCategory.getId())) {
                throw new MideaMallException(MideaMallExceptionEnum.NAME_EXIT);
            }
        }
        int count = categoryMapper.updateByPrimaryKeySelective(updateCategory);
        if (count == 0) {
            throw new MideaMallException(MideaMallExceptionEnum.UPDATE_FAIL);
        }

    }

    @Override
    public void delete(Integer id) {
        Category oldCategory = categoryMapper.selectByPrimaryKey(id);
        if (oldCategory == null) {
            throw new MideaMallException(MideaMallExceptionEnum.DELETE_FAILED);
        }
        int count = categoryMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            throw new MideaMallException(MideaMallExceptionEnum.DELETE_FAILED);
        }
    }


    @Override
    public PageInfo listForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "type,order_num");
        List<Category> categoryList = categoryMapper.selectList();
        PageInfo pageInfo = new PageInfo<>(categoryList);
        return pageInfo;
    }


    @Override
    @Cacheable(value = "listCategoryForCustomer")
    public List<CategoryVO> listCategoryForCustomer() {
        List<CategoryVO> categoryVOList = new ArrayList<>();
        recursivelyFindCategories(categoryVOList, 0);
        return categoryVOList;
    }

    @Override
    public List<CategoryVO> listCategoryForCustomer(Integer categoryId) {
        List<CategoryVO> categoryVOList = new ArrayList<>();
        recursivelyFindCategories(categoryVOList, categoryId);
        return categoryVOList;
    }


    private void recursivelyFindCategories(List<CategoryVO> categoryVOList, Integer parentId) {

        //递归获取所有子类别，并组合成一个“目录树”
        List<Category> categoryList = categoryMapper.selectCategoriesByParentId(parentId);

        if (!CollectionUtils.isEmpty(categoryList)) {
            for (Category category : categoryList) {
                CategoryVO categoryVO = new CategoryVO();
                BeanUtils.copyProperties(category, categoryVO);
                categoryVOList.add(categoryVO);
                recursivelyFindCategories(categoryVO.getChildCategory(), categoryVO.getId());
            }
        }
    }
}
