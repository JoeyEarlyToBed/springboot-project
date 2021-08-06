package com.midea.emall.common;

import com.google.common.collect.Sets;
import com.midea.emall.exception.MideaMallException;
import com.midea.emall.exception.MideaMallExceptionEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component
public class Constant {
    public static final String MIDEA_MALL_USER = "midea_mall_user";
    public static final String SALT = "8svbsvjkweDF,.03[";

    public static String FILE_UPLOAD_DIR;

    @Value("${file.upload.dir}")
    public void setFileUploadDir(String fileUploadDir) {
        FILE_UPLOAD_DIR = fileUploadDir;
    }

    //排序
    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price desc", "price asc");
    }

    public interface SaleStatus{
        int NOT_SALE = 0;
        int SALE = 1;
    }

    public interface Cart{
        int CHECKED = 1;
        int UNCHECKED = 0;
    }


    public enum OrderStatusEnum{
        CANCELED(0, "用户已取消"),
        NOT_PAID(10, "未付款"),
        PAID(20, "已付款"),
        DELIVERED(30, "已发货"),
        FINISHED(40, "交易完成");

        private int code;
        private  String value;

        OrderStatusEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        /*根据code值获取状态方法*/
        public static OrderStatusEnum codeOf(int code) {
            for (OrderStatusEnum orderStatusEnum : values()) {
                if (orderStatusEnum.getCode() == code) {
                    return orderStatusEnum;
                }
            }
            throw new MideaMallException(MideaMallExceptionEnum.NO_ENUM);
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
