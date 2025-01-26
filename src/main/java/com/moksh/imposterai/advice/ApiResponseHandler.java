package com.moksh.imposterai.advice;

import com.moksh.imposterai.dtos.response.ApiResponse;
import com.moksh.imposterai.dtos.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
@Slf4j
public class ApiResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return !returnType.getParameterType().equals(ErrorResponse.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {

        HttpStatus status = HttpStatus.OK;
        String message = determineMessage(response);
        if (response instanceof ServletServerHttpResponse) {
            status = HttpStatus.valueOf(((ServletServerHttpResponse) response).getServletResponse().getStatus());
        }

        if (body instanceof ApiResponse || body instanceof ErrorResponse) {
            return body;
        }

        return ApiResponse.success(body, message,status);
    }

    private String determineMessage(ServerHttpResponse response) {
        if (response instanceof ServletServerHttpResponse servletResponse) {
            int statusCode = servletResponse.getServletResponse().getStatus();
            return switch (statusCode) {
                case 201 -> "Created Successfully";
                case 204 -> "No Content";
                case 400 -> "Bad Request";
                case 401 -> "Unauthorized";
                case 403 -> "Forbidden";
                case 404 -> "Not Found";
                case 409 -> "Conflict";
                default -> "Success";
            };
        }
        return "Success";
    }
}
