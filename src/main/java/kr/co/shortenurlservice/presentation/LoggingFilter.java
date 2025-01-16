package kr.co.shortenurlservice.presentation;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpServletRequest) {
            // 요청 본문을 캐싱한 새로운 HttpServletRequest 로 교체
            CachedBodyHttpServletRequest cachedBodyRequest = new CachedBodyHttpServletRequest(httpServletRequest);

            String url = cachedBodyRequest.getRequestURI();
            String method = cachedBodyRequest.getMethod();
            String body = cachedBodyRequest.getReader().lines().reduce("", String::concat);

            log.trace("Incoming Request: URL = {}, Method = {}, Body = {}", url, method, body);

            // 래퍼된 요청 객체로 다음 필터나 서블릿 호출
            chain.doFilter(cachedBodyRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }

}
