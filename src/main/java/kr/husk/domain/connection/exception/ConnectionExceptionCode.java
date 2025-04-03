package kr.husk.domain.connection.exception;

import kr.husk.common.exception.ExceptionCode;
import org.springframework.http.HttpStatus;

public enum ConnectionExceptionCode implements ExceptionCode {

    CONNECTION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 커넥션입니다.");

    HttpStatus httpStatus;
    String cause;

    ConnectionExceptionCode(HttpStatus httpStatus, String cause) {
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
