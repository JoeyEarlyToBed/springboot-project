package com.midea.emall.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 打印请求与响应的信息
 */
@Aspect
@Component
public class WebLogAspect {

    final Logger log = LoggerFactory.getLogger(WebLogAspect.class);

    @Pointcut("execution(public * com.midea.emall.controller.*.*(..))")
    public void webLog() {

    }

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        //收到请求，记录请求内容
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        log.info("URL:" + request.getRequestURL().toString());  //URL
        log.info("HTTP_METHOD:" + request.getMethod().toString()); //请求方式
        log.info("IP:" + request.getRemoteAddr().toString());   //IP信息
        log.info("CLASS_METHOD:" + joinPoint.getSignature().getDeclaringTypeName() + "."
                + joinPoint.getSignature().getName());          //类信息
        log.info("ARGS:" + Arrays.toString(joinPoint.getArgs()));   //参数信息

    }


    @AfterReturning(returning = "res" , pointcut = "webLog()")
    public void doAfterReturning(Object res) throws JsonProcessingException {
        //处理完请求，返回内容
        log.info("RESPONSE:" + new ObjectMapper().writeValueAsString(res));
    }
}
