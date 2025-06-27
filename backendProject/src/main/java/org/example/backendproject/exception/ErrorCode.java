package org.example.backendproject.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_PASSWORD(401, "비밀번호가 맞지않습니다"),
    INTERNAL_ERROR(500, "서버 내부 오류가 발생했습니다-ErrorCode Enum");


    private final int code;
    private final String message;
}

