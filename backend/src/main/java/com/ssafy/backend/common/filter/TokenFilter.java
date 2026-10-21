package com.ssafy.backend.common.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.ssafy.backend.common.util.JwtUtil;
import com.ssafy.backend.common.util.SseTicketStore;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TokenFilter implements Filter {
    private final SseTicketStore sseTicketStore;

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 원본 URI(getRequestURI)는 "/api/auth/../user" 처럼 정규화되지 않아 우회될 수 있으므로
        // 컨테이너가 정규화한 경로(servletPath + pathInfo)로 판단합니다.
        String path = request.getServletPath() + (request.getPathInfo() == null ? "" : request.getPathInfo());

        // CORS preflight와 토큰 없이 호출되는 인증 API는 필터를 건너뜁니다.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod()) || isPublicPath(path)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // EventSource는 헤더를 보낼 수 없으므로 SSE 연결은 단기·일회용 티켓으로 인증합니다.
        // (수명이 긴 access 토큰을 URL에 싣지 않기 위함)
        if (isSseConnectPath(path)) {
            String ticketUserId = sseTicketStore.consume(request.getParameter("ticket"));
            if (ticketUserId == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 SSE 티켓입니다");
                return;
            }
            if (!path.substring(path.lastIndexOf('/') + 1).equals(ticketUserId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "본인 알림만 구독할 수 있습니다");
                return;
            }
            request.setAttribute("userId", ticketUserId);
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰 형식이 잘못됐습니다");
            return;
        }

        String token = header.substring(7);
        Claims claims;
        try {
            claims = JwtUtil.getClaims(token);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "잘못된 토큰입니다");
            return;
        }

        String tokenUserId = claims.getSubject();

        request.setAttribute("userId", tokenUserId);
        filterChain.doFilter(request, response);
    }

    private static final String[] SSE_CONNECT_PREFIXES = {"/api/auth/connect/", "/api/v2/auth/connect/", "/api/push/connect/"};

    private boolean isSseConnectPath(String path) {
        for (String prefix : SSE_CONNECT_PREFIXES) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    // 로그인/토큰 재발급처럼 토큰 없이 호출되는 경로 (SSE 연결은 제외)
    private boolean isPublicPath(String path) {
        if (isSseConnectPath(path)) {
            return false;
        }
        return path.startsWith("/api/auth/") || path.startsWith("/api/v2/auth/")
               || path.equals("/api/user/refresh-token");
    }
}
