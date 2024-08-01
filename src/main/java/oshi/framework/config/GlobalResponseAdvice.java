package oshi.framework.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import oshi.framework.rest.server.ServerResult;
import oshi.framework.util.JsonUtils;


@RestControllerAdvice(basePackages = "oshi")
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // 处理String类型，直接ServerResult.success(body)会报错ServerResult cannot be cast to java.lang.String
        if (body instanceof String) {
            return JsonUtils.serialObject(ServerResult.success(body));
        }
        /*String requestURI = ((ServletServerHttpRequest) request).getServletRequest().getRequestURI();
        if (requestURI.contains("swagger") || requestURI.contains("api-docs")) {
            return body;
        }*/
        // 若已经是统一返回类型，则不用再次封装
        if (body instanceof ServerResult) {
            return body;
        }
        return ServerResult.success(body);
    }
}
