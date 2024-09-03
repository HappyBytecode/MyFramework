package com.lirui.lib_common.net.netError;

/**
 * APP异常
 */
public class ApiException extends Exception {
    private int code;
    private String message;
    private Throwable throwable;

    public ApiException(Throwable throwable, int code) {
        super(throwable);
        this.throwable = throwable;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public String toString() {
        return code + "," + message + "," + throwable.toString();
    }
}