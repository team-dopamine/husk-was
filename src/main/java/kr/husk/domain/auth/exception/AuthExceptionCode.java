package kr.husk.domain.auth.exception;

import kr.husk.common.exception.ExceptionCode;
import org.springframework.http.HttpStatus;

public enum AuthExceptionCode implements ExceptionCode {
    VERIFICATION_CODE_NOT_MATCH(HttpStatus.BAD_REQUEST, "인증 코드가 일치하지 않습니다."),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "인증 메일 발송에 실패했습니다.");

    HttpStatus httpStatus;
    String cause;

    AuthExceptionCode(HttpStatus httpStatus, String cause) {
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
