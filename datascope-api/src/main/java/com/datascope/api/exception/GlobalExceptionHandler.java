package com.datascope.api.exception;

import com.datascope.common.exception.BizException;
import com.datascope.common.exception.ErrorCode;
import com.datascope.common.model.Response;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BizException.class)
    public Response<?> handleBizException(BizException e) {
        log.warn("[业务异常] code: {}, message: {}, args: {}", 
            e.getCode(), e.getMessage(), e.getArgs());
        return Response.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常（@Valid注解抛出）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.toList());
        String message = String.join("; ", errors);
        log.warn("[参数校验异常] message: {}", message);
        return Response.error(ErrorCode.INVALID_PARAMETER.getCode(), message);
    }

    /**
     * 处理参数校验异常（@Validated注解抛出）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<?> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String message = violations.stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining("; "));
        log.warn("[参数校验异常] message: {}", message);
        return Response.error(ErrorCode.INVALID_PARAMETER.getCode(), message);
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<?> handleBindException(BindException e) {
        List<String> errors = e.getBindingResult().getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.toList());
        String message = String.join("; ", errors);
        log.warn("[参数绑定异常] message: {}", message);
        return Response.error(ErrorCode.INVALID_PARAMETER.getCode(), message);
    }

    /**
     * 处理请求参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String message = String.format("参数类型不匹配，参数[%s]应为%s类型", 
            e.getName(), e.getRequiredType().getSimpleName());
        log.warn("[参数类型不匹配] message: {}", message);
        return Response.error(ErrorCode.INVALID_PARAMETER.getCode(), message);
    }

    /**
     * 处理请求体解析异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("[请求体解析异常] message: {}", e.getMessage());
        return Response.error(ErrorCode.INVALID_PARAMETER.getCode(), "请求体格式错误");
    }

    /**
     * 处理请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Response<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("[请求方法不支持] method: {}, supportedMethods: {}", 
            e.getMethod(), e.getSupportedHttpMethods());
        return Response.error(405000, "不支持的请求方法");
    }

    /**
     * 处理必需参数缺失异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String message = String.format("缺少必需参数[%s]", e.getParameterName());
        log.warn("[参数缺失] message: {}", message);
        return Response.error(ErrorCode.INVALID_PARAMETER.getCode(), message);
    }

    /**
     * 处理数据库唯一约束异常
     */
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response<?> handleDuplicateKeyException(DuplicateKeyException e) {
        log.warn("[数据重复] message: {}", e.getMessage());
        return Response.error(ErrorCode.RESOURCE_ALREADY_EXISTS.getCode(), "数据已存在");
    }

    /**
     * 处理数据库完整性约束异常
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<?> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.warn("[数据完整性约束异常] message: {}", e.getMessage());
        return Response.error(ErrorCode.INVALID_PARAMETER.getCode(), "数据完整性校验失败");
    }

    /**
     * 处理SQL异常
     */
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response<?> handleSQLException(SQLException e) {
        log.error("[SQL异常] state: {}, code: {}, message: {}", 
            e.getSQLState(), e.getErrorCode(), e.getMessage());
        return Response.error(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), "数据库操作失败");
    }

    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response<?> handleException(Exception e) {
        log.error("[系统异常]", e);
        return Response.error(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), "系统内部错误");
    }
} 