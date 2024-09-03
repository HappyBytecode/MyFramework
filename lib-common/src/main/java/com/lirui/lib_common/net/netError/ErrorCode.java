package com.lirui.lib_common.net.netError;

/**
 * 异常的分类
 */
public class ErrorCode {
    /**
     * 未知错误
     */
    public static final int UNKNOWN = 1000;
    /**
     * 解析错误
     */
    public static final int PARSE_ERROR = 1001;
    /**
     * 网络错误
     */
    public static final int NETWORD_ERROR = 1002;
    /**
     * 协议出错
     */
    public static final int HTTP_ERROR = 1003;
    /**
     * 网络未连接错误
     */
    public static final int NO_NET_ERROR = 1004;
    /**
     * Token 失效
     */
    public static final int TOKEN_INVALID = 1006;
    /**
     * 连接超时
     */
    public static final int TIMEOUT_ERROR = 1007;
    /**
     * 安全策略异常
     */
    public static final int SSL_ERROR = 1008;
}
