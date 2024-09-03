package com.lirui.lib_common.net.bean;

import java.io.Serializable;

/**
 * 数据基类，所有数据模型都采用此模式
 */

public class BaseBean<T> implements Serializable{

    private String msg;
    private String msgCode;
    private T data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
