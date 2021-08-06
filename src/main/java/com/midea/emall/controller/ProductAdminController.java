package com.midea.emall.controller;


import com.github.pagehelper.PageInfo;
import com.midea.emall.common.ApiRestResponse;
import com.midea.emall.common.Constant;
import com.midea.emall.exception.MideaMallException;
import com.midea.emall.exception.MideaMallExceptionEnum;
import com.midea.emall.model.pojo.Product;
import com.midea.emall.model.request.AddProductReq;
import com.midea.emall.model.request.UpdateProductReq;
import com.midea.emall.service.ProductService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@RestController
public class ProductAdminController {

    @Autowired
    ProductService productService;

    @PostMapping("admin/product/add")
    public ApiRestResponse addProduct(@Valid@RequestBody AddProductReq addProductReq) {
        productService.add(addProductReq);
        return ApiRestResponse.success();
    }

    @PostMapping("admin/upload/file")
    public ApiRestResponse upload(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        String filename = file.getOriginalFilename();
        String suffixName = filename.substring(filename.lastIndexOf('.'));
        //生成文件名称UUID
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid.toString() + suffixName;
        //创建文件
        File fileDirectory = new File(Constant.FILE_UPLOAD_DIR);
        File destFile = new File(Constant.FILE_UPLOAD_DIR + newFileName);

        if (!fileDirectory.exists()) {
            if (!fileDirectory.mkdir()) {
                throw new MideaMallException(MideaMallExceptionEnum.MKDIR_FAILED);
            }
        }
        //通过两层校验，此时文件夹存在，写入文件
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            throw new MideaMallException(MideaMallExceptionEnum.UPLOAD_FAILED);
        }
        //返回图片上传地址
        try {
            return ApiRestResponse
                    .success(getHost(new URI(request.getRequestURI()+ "" ))
                    + "/images/" + newFileName);
        } catch (URISyntaxException e) {
            return ApiRestResponse.error(MideaMallExceptionEnum.UPLOAD_FAILED);
        }


    }

    /**
     * 获得URI中想包含的部分，剔除无用部分
     * @param uri
     * @return
     */
    private URI getHost(URI uri) {
        URI effectiveURI;
        try {
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(),
                    null, null, null);
        } catch (URISyntaxException e) {
            effectiveURI = null;
        }
        return effectiveURI;
    }


    @PostMapping("admin/product/update")
    @ApiOperation("后台更新商品")
    public ApiRestResponse updateProduct(@RequestBody @Valid UpdateProductReq updateProductReq) {
        Product product = new Product();
        BeanUtils.copyProperties(updateProductReq, product);
        productService.update(product);
        return ApiRestResponse.success();
    }


    @PostMapping("admin/product/delete")
    @ApiOperation("后台删除商品")
    public ApiRestResponse deleteProduct(@RequestParam Integer id) {
        productService.delete(id);
        return ApiRestResponse.success();
    }

    @PostMapping("admin/product/batchUpdateSellStatus")
    @ApiOperation("后台批量上下架商品")
    public ApiRestResponse batchUpdateSellStatus(@RequestParam Integer[] ids,
                                                 @RequestParam Integer sellStatus) {
        productService.batchUpdateSellStatus(ids, sellStatus);
        return ApiRestResponse.success();
    }


    @ApiOperation("后台商品列表接口")
    @PostMapping("admin/product/list")
    public ApiRestResponse list(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo pageInfo = productService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }



}
