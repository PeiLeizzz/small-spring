package com.peilei.springframework.beans.exception;

/**
 * Bean 相关异常
 */
public class BeansException extends Exception {
    public BeansException(String msg) {
        super(msg);
    }

    public BeansException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
