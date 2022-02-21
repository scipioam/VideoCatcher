package com.sapher.youtubedl;

/**
 * 格式不支持异常
 * @author Alan Scipio
 * @since 2022/2/21
 */
public class FormatNotAvailableException extends RuntimeException{
    public FormatNotAvailableException(String message) {
        super(message);
    }
}
