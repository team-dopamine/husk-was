package kr.husk.domain.auth.exception;

import kr.husk.common.exception.ExceptionCode;
import org.springframework.http.HttpStatus;

public enum AuthExceptionCode implements ExceptionCode {
    VERIFICATION_CODE_NOT_MATCH(HttpStatus.BAD_REQUEST, "인증 코드가 일치하지 않습니다."),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "인증 메일 발송에 실패했습니다."),
    PASSWORD_MISMATCHED(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    GOOGLE_USERINFO_NOTFOUND(HttpStatus.NOT_FOUND, "Google 사용자 정보 요청에 실패했습니다."),
    ACCESSTOKEN_REQUEST_FAILED(HttpStatus.UNAUTHORIZED, "Google OAuth Access Token 요청에 실패했습니다."),
    NOT_ALLOWED_TYPE(HttpStatus.INTERNAL_SERVER_ERROR, "허용되지 않은 OAuth 타입 요청입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 액세스 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리프레시 토큰입니다."),
    CONFIRM_PASSWORD_NOT_EQUAL(HttpStatus.BAD_REQUEST, "비밀번호 확인이 일치하지 않습니다."),
    OAUTH_UNLINK_FAILED(HttpStatus.BAD_REQUEST, "계정 연결 해제에 실패하였습니다.)");

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
