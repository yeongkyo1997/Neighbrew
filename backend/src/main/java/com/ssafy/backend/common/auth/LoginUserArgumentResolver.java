package com.ssafy.backend.common.auth;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import io.jsonwebtoken.JwtException;

// TokenFilter가 request attribute "userId"에 넣어 둔 값을 @LoginUser Long 파라미터로 변환한다.
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
    private static final String USER_ID_ATTRIBUTE = "userId";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class) && Long.class.equals(parameter.getParameterType());
    }

    @Override
    public Long resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Object userId = webRequest.getAttribute(USER_ID_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        if (userId == null) {
            throw new JwtException("인증 정보가 없습니다.");
        }
        return Long.valueOf(userId.toString());
    }
}
