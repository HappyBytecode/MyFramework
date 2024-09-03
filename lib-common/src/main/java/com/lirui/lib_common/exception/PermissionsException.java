package com.lirui.lib_common.exception;

/**
 * 权限异常
 */

public class PermissionsException extends Exception {
    private final String permission;//权限
    private final String msg;//提醒

    public PermissionsException(String permission, String msg) {
        this.permission = permission;
        this.msg = msg;
    }
}
