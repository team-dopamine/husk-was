package kr.husk.domain.auth.exception;

import kr.husk.common.exception.ExceptionCode;
import org.springframework.http.HttpStatus;

public enum UserExceptionCode implements ExceptionCode {

    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    EMAIL_IS_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 이메일입니다.");

    HttpStatus httpStatus;
    String cause;

    UserExceptionCode(org.springframework.http.HttpStatus httpStatus, String cause) {
        this.httpStatus = httpStatus;
        this.cause = cause;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getCause() {
        return cause;
    }

    @Override
    public String getName() {
        return name();
    }
}
