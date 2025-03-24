package com.datascope.infrastructure.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import java.io.IOException;

/**
 * JPA JSON类型转换器
 * 用于将Java对象转换为JSON字符串存储到数据库，以及从数据库读取JSON字符串转换为Java对象
 */
@Slf4j
public class JpaConverterJson implements AttributeConverter<Object, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Override
    public String convertToDatabaseColumn(Object attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("Error converting object to JSON string", e);
            return null;
        }
    }

    @Override
    public Object convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            // 尝试保留原始JSON字符串格式，交由具体的实体类字段处理具体类型转换
            return objectMapper.readTree(dbData);
        } catch (IOException e) {
            log.error("Error converting JSON string to object", e);
            return null;
        }
    }
    
    /**
     * 将JSON字符串转换为指定类型的对象
     * @param json JSON字符串
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 转换后的对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            log.error("Error converting JSON string to object of type " + clazz.getName(), e);
            return null;
        }
    }
    
    /**
     * 将JSON字符串转换为指定类型引用的对象，用于处理泛型
     * @param json JSON字符串
     * @param typeReference 类型引用
     * @param <T> 泛型类型
     * @return 转换后的对象
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (IOException e) {
            log.error("Error converting JSON string to object with TypeReference", e);
            return null;
        }
    }
    
    /**
     * 将JSON字符串转换为指定JavaType的对象
     * @param json JSON字符串
     * @param javaType JavaType
     * @param <T> 泛型类型
     * @return 转换后的对象
     */
    public static <T> T fromJson(String json, JavaType javaType) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            log.error("Error converting JSON string to object with JavaType", e);
            return null;
        }
    }
    
    /**
     * 将对象转换为JSON字符串
     * @param object 要转换的对象
     * @return JSON字符串
     */
    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Error converting object to JSON string", e);
            return null;
        }
    }
}