package com.datascope.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 通用响应类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {
    /**
     * 响应码
     */
    private String code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 成功响应
     */
    public static <T> Response<T> success(T data) {
        return new Response<>("200", "success", data);
    }
    
    /**
     * 错误响应
     */
    public static <T> Response<T> error(String code, String message) {
        return new Response<>(code, message, null);
    }
}