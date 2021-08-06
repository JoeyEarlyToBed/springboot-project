package com.midea.emall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.midea.emall.common.Constant;
import com.midea.emall.exception.MideaMallException;
import com.midea.emall.exception.MideaMallExceptionEnum;
import com.midea.emall.model.dao.ProductMapper;
import com.midea.emall.model.pojo.Product;
import com.midea.emall.model.query.ProductListQuery;
import com.midea.emall.model.request.AddProductReq;
import com.midea.emall.model.request.ProductListReq;
import com.midea.emall.model.vo.CategoryVO;
import com.midea.emall.service.CategoryService;
import com.midea.emall.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductMapper productMapper;

    @Autowired
    CategoryService categoryService;

    @Override
    public void add(@Valid @RequestBody AddProductReq addProductReq) {
        Product product = new Product();
        BeanUtils.copyProperties(addProductReq, product);
        Product oldProduct = productMapper.selectByName(addProductReq.getName());
        if (oldProduct != null) {
            throw new MideaMallException(MideaMallExceptionEnum.NAME_EXIT);
        }
        int count = productMapper.insertSelective(product);
        if (count == 0) {
            throw new MideaMallException(MideaMallExceptionEnum.CREATE_FAILED);
        }

    }

    @Override
    public void update(@Valid@RequestParam Product updateProduct) {
        Product oldProduct = productMapper.selectByName(updateProduct.getName());
        //同名且不同Id不能修改
        if (oldProduct != null && !oldProduct.equals(updateProduct.getId())) {
            throw new MideaMallException(MideaMallExceptionEnum.NAME_EXIT);
        }
        int count = productMapper.updateByPrimaryKeySelective(updateProduct);
        if (count == 0) {
            throw new MideaMallException(MideaMallExceptionEnum.UPDATE_FAIL);
        }
    }

    @Override
    public void delete(Integer id) {
        Product oldProduct = productMapper.selectByPrimaryKey(id);
        if (oldProduct == null){
            throw new MideaMallException(MideaMallExceptionEnum.DELETE_FAILED);
        }
        int count = productMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            throw new MideaMallException(MideaMallExceptionEnum.DELETE_FAILED);
        }

    }

    @Override
    public void batchUpdateSellStatus( Integer[] ids,  Integer sellStatus) {
        int count = productMapper.batchUpdateSellStatus(ids, sellStatus);
        if (count != ids.length) {
            throw new MideaMallException(MideaMallExceptionEnum.UPDATE_FAIL);
        }
    }

    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> list = productMapper.selectListForAdmin();
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    @Override
    public Product detail(Integer id) {
        Product product = productMapper.selectByPrimaryKey(id);
        return product;
    }

    @Override
    public PageInfo list(ProductListReq productListReq) {

        //复杂查询通常需要一个query对象
        ProductListQuery productListQuery = new ProductListQuery();
        //搜索
        if (!StringUtils.isEmpty(productListReq.getKeyword())) {
            String keyword = new StringBuilder().append("%").append(productListReq.getKeyword())
                    .append("%").toString();
            productListQuery.setKeyword(keyword);
        }
        //目录搜索：如果查询某个目录下的商品，不仅是需要查出该目录下的商品
        //          对应所有子目录的所有商品都应查出来，故需要一个目录id的list
        if (productListReq.getCategoryId() != null) {
           List<CategoryVO> categoryVOList = categoryService.listCategoryForCustomer(productListReq.getCategoryId());
           ArrayList<Integer> categoryIds = new ArrayList<>();
           categoryIds.add(productListReq.getCategoryId());//请求中的目录id
           getCategoryIds(categoryVOList, categoryIds);//请求id的所有子id
           productListQuery.setCategoryIds(categoryIds);
        }
        String orderBy = productListReq.getOrderBy();
        if (Constant.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {//支持此排序
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize(), orderBy);
        }else {//不支持此排序
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize());
        }
        List<Product> productList = productMapper.selectList(productListQuery);
        PageInfo pageInfo = new PageInfo(productList);
        return pageInfo;
    }


    /**
     *
     * @param categoryVOList
     * @param categoryIds
     */
    private void getCategoryIds(List<CategoryVO> categoryVOList, ArrayList<Integer> categoryIds) {
        for (int i = 0; i < categoryVOList.size(); i++) {
            CategoryVO categoryVO = categoryVOList.get(i);
            if (categoryVO != null) {
                categoryIds.add(categoryVO.getId());
                getCategoryIds(categoryVO.getChildCategory(), categoryIds);
            }
        }
    }

}
