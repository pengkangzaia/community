package com.nowcoder.community.controller.advice;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) {

        logger.error("服务器发生异常:" + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }


        String xRequestedWith = request.getHeader("x-requested-with");

        if ("XMLHttpRequest".equals(xRequestedWith)){
            // 这说明这是一个异步请求
            // 返回的是一个常规字符串
            // 如果写为json，那么在js文件中就不需要
            // data = $.parseJSON(data);这一步
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = null;
            try {
                writer = response.getWriter();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println(CommunityUtil.getJSONString(1,"服务器异常！"));
            writer.write(CommunityUtil.getJSONString(1,"服务器异常！"));
        } else {
            try {
                response.sendRedirect(request.getContextPath() + "/error");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }


    }


}
