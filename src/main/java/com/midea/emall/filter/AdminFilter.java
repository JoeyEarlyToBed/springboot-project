package com.midea.emall.filter;

import com.midea.emall.common.Constant;
import com.midea.emall.model.pojo.User;
import com.midea.emall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class AdminFilter implements Filter {

    @Autowired
    UserService userService;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute(Constant.MIDEA_MALL_USER);
        if (currentUser == null) {
            //利用PrinterWriter对象，输出想要再JSON返回的内容
            PrintWriter out = new HttpServletResponseWrapper((HttpServletResponse) servletResponse).getWriter();
            out.write("{\n"
                    + "\"status\":10007,\n"
                    + "\"msg:\"NEED_LOGIN\",\n"
                    + "\"data\":null\n"
                    + "}");
            out.flush();
            out.close();
            return;
        }
        boolean adminRole = userService.checkAdminRole(currentUser);
        if (adminRole) {
            filterChain.doFilter(servletRequest, servletResponse);//放行
        }else {
            PrintWriter out = new HttpServletResponseWrapper((HttpServletResponse) servletResponse).getWriter();
            out.write("{\n"
                    + "\"status\":10009,\n"
                    + "\"msg:\"NEED_ADMIN\",\n"
                    + "\"data\":null\n"
                    + "}");
            out.flush();
            out.close();
        }
    }
}
