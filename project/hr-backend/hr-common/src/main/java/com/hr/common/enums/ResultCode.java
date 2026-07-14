package com.hr.common.enums;

import lombok.Getter;

@Getter
public enum ResultCode {

    SUCCESS(200, "成功"),
    BAD_REQUEST(400, "参数校验失败"),
    UNAUTHORIZED(401, "未登录 / Token 过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    // 业务错误码（与 json设计.md 1.4 一致）
    USERNAME_PASSWORD_ERROR(1001, "用户名或密码错误"),
    USER_DISABLED(1002, "用户已禁用"),
    TOKEN_INVALID(1003, "Token 无效或已过期"),
    OLD_PASSWORD_ERROR(1004, "旧密码不正确"),
    DUPLICATE_RECORD(2001, "数据已存在（工号/用户名重复等）"),
    HAS_ASSOCIATED_DATA(2002, "存在关联数据，无法删除"),
    IMPORT_FILE_FORMAT_ERROR(2003, "导入文件格式错误"),
    ATTENDANCE_RECORD_NOT_FOUND(3001, "考勤记录不存在");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
