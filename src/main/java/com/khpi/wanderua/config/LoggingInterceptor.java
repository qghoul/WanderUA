package com.khpi.wanderua.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String userAgent = request.getHeader("User-Agent");

        // dont logg static sources
        if (!requestURI.startsWith("/css") && !requestURI.startsWith("/js") &&
                !requestURI.startsWith("/images") && !requestURI.startsWith("/static")) {
            log.debug("{} {} - User-Agent: {}", method, requestURI, userAgent);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        if (ex != null) {
            log.error("Request {} {} failed with exception: ",
                    request.getMethod(), request.getRequestURI(), ex);
        }
    }
}
