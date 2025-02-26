package kr.husk.domain.keychain.exception;

import kr.husk.common.exception.ExceptionCode;
import org.springframework.http.HttpStatus;

public enum KeyChainExceptionCode implements ExceptionCode {

    KEY_CHAIN_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 키체인입니다.");

    HttpStatus httpStatus;
    String cause;

    KeyChainExceptionCode(HttpStatus httpStatus, String cause) {
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
