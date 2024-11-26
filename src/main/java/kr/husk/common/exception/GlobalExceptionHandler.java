package kr.husk.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ExceptionResponse> handler(GlobalException e) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        ExceptionResponse response = ExceptionResponse.of(exceptionCode);
        log.error("[GlobalException] name:" + e.getExceptionCode().getName() + ", cause: " + e.getExceptionCode().getCause());
        return ResponseEntity.status(exceptionCode.getHttpStatus()).body(response);
    }

}
