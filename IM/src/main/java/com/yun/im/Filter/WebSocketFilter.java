package com.yun.im.Filter;

import com.yun.im.repository.UserRepository;
import com.yun.im.utilies.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class WebSocketFilter implements Filter {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        if (requestURI.contains("WebSocket")){
            String id = requestURI.replace("/WebSocket/", "");
            if (Constants.USERID.contains(id)) {
                if (userRepository.connect(id, httpRequest.getHeader("token")) != null) {
                    chain.doFilter(request, response);
                } else {
                    returnJson(response, "{\"code\":\"1001\",\"msg\":\"token失效\"}");
                }
            } else {
                returnJson(response, "{\"code\":\"1002\",\"msg\":\"验证消息失败\"}");
            }
        }else {
            chain.doFilter(request, response);
        }

    }

    @Override
    public void destroy() {
    }

    private void returnJson(ServletResponse response, String json) {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(json);
        } catch (IOException e) {
        } finally {
            if (writer != null) writer.close();
        }
    }

}

