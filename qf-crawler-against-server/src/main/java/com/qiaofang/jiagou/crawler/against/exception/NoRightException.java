package com.qiaofang.jiagou.crawler.against.exception;

/**
 * 无权限异常
 *
 * @author shihao.liu
 * @version 1.0
 * @date 2019-04-25 14:48
 */
public class NoRightException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NoRightException() {
        super();
    }

    public NoRightException(String message) {
        super(message);
    }

    public NoRightException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoRightException(Throwable cause) {
        super(cause);
    }

    protected NoRightException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
