package kr.husk.common.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ExceptionResponse {
    private LocalDateTime timeStamp;
    private String name;
    private String cause;

    public static ExceptionResponse of(ExceptionCode exceptionCode) {
        return ExceptionResponse.builder()
                .timeStamp(LocalDateTime.now())
                .name(exceptionCode.getName())
                .cause(exceptionCode.getCause())
                .build();
    }
}
