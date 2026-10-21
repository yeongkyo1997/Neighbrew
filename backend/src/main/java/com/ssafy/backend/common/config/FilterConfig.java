package com.ssafy.backend.common.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ssafy.backend.common.filter.TokenFilter;
import com.ssafy.backend.common.util.SseTicketStore;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<TokenFilter> tokenFilterRegistrationBean(SseTicketStore sseTicketStore) {
        FilterRegistrationBean<TokenFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new TokenFilter(sseTicketStore));
        registrationBean.addUrlPatterns("/api/*"); // 필터가 적용될 URL 패턴을 설정합니다. 필요에 따라 변경할 수 있습니다.

        return registrationBean;
    }
}
