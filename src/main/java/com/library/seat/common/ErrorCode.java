package com.library.seat.common;

public enum ErrorCode {

    SUCCESS(0, "success"),

    // 用户与登录 1000-1999
    LOGIN_FAILED(1001, "用户名或密码错误"),
    NOT_LOGIN(1002, "用户未登录"),
    USER_NOT_FOUND(1003, "用户不存在"),
    USER_BLOCKED(1004, "该用户已被拉黑，无法创建预约"),

    // 预约业务 2000-2999
    RESERVATION_CONFLICT(2001, "预约时间与已有预约冲突"),
    ACTIVE_RESERVATION_EXISTS(2009, "您当前已有有效预约，请先取消后再预约"),
    START_TIME_IN_PAST(2002, "预约开始时间不能早于当前时间"),
    DURATION_TOO_SHORT(2003, "单次预约时间不能少于1小时"),
    TIME_OUT_OF_RANGE(2004, "预约时间不在开放时间内"),
    RESERVATION_NOT_FOUND(2005, "预约记录不存在"),
    NOT_OWN_RESERVATION(2006, "只能取消自己的预约"),
    CHECK_IN_TIME_INVALID(2007, "当前时间不在预约时间范围内，无法签到"),
    RESERVATION_STATUS_NOT_ALLOWED(2008, "当前预约状态不允许此操作"),

    // 座位业务 3000-3999
    SEAT_NOT_FOUND(3001, "座位不存在"),
    SEAT_UNAVAILABLE(3002, "座位当前不可预约"),

    // 管理员权限 4000-4999
    NO_ADMIN_PERMISSION(4001, "无管理员权限"),

    // 系统通用 9000-9999
    PARAM_ERROR(9001, "请求参数错误"),
    SYSTEM_ERROR(9002, "系统异常");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
