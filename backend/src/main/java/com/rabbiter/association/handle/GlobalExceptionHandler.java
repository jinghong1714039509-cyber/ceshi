package com.rabbiter.association.handle;

import com.alibaba.druid.pool.DataSourceNotAvailableException;
import com.rabbiter.association.msg.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public R handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldError() == null
                ? "请求参数校验失败"
                : exception.getBindingResult().getFieldError().getDefaultMessage();
        return R.warn(message);
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public R handleBindException(BindException exception) {
        String message = exception.getBindingResult().getFieldError() == null
                ? "请求参数绑定失败"
                : exception.getBindingResult().getFieldError().getDefaultMessage();
        return R.warn(message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public R handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        log.warn("Request body parse failed", exception);
        return R.warn("请求体格式错误");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public R handleIllegalArgument(IllegalArgumentException exception) {
        return R.warn(exception.getMessage());
    }

    @ExceptionHandler({
            CannotGetJdbcConnectionException.class,
            CannotCreateTransactionException.class,
            DataSourceNotAvailableException.class,
            DataAccessException.class
    })
    @ResponseBody
    public R handleDatabaseException(HttpServletRequest request, Exception exception) {
        log.error("Database exception on {} {}", request.getMethod(), request.getRequestURI(), exception);
        return R.error("数据库连接失败，请检查 application.yml 或环境变量中的 MySQL 账号密码配置");
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R handleException(HttpServletRequest request, Exception exception) {
        log.error("Unhandled exception on {} {}", request.getMethod(), request.getRequestURI(), exception);
        return R.error("系统异常，请联系管理员");
    }
}
