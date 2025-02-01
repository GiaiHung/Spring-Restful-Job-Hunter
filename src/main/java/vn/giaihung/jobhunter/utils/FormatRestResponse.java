package vn.giaihung.jobhunter.utils;

import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import jakarta.servlet.http.HttpServletResponse;
import vn.giaihung.jobhunter.domain.RestResponse;
import vn.giaihung.jobhunter.utils.annotation.ApiMessage;

@RestControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @SuppressWarnings("null")
    @Override
    public Object beforeBodyWrite(
            @Nullable Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = servletResponse.getStatus();
        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatusCode(status);

        if (body instanceof String || body instanceof Resource) {
            return body;
        }

        String path = request.getURI().getPath();
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) {
            return body;
        }

        // Success (100-300)
        if (status >= 400) {
            return body;
        } else {
            ApiMessage apiMessage = returnType.getMethodAnnotation(ApiMessage.class);
            restResponse.setData(body);
            restResponse.setMessage(apiMessage != null ? apiMessage.value() : "CALL API SUCCESS");
        }

        // Error (400 up)
        return restResponse;
    }

}
