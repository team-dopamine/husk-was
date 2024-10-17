package kr.husk.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class VerifyAuthCodeDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "VerifyAuthCode.Request", description = "인증 코드 검증 요청 DTO")
    public static class Request {
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @Schema(description = "이메일", example = "team.dopamine.dev@gmail.com")
        private String email;

        @NotBlank(message = "인증 코드는 필수 입력값입니다.")
        @Schema(description = "인증 코드", example = "123456")
        private String authCode;
    }

    @Getter
    @AllArgsConstructor
    @Schema(name = "VerifyAuthCode.Response", description = "인증 코드 검증 응답 DTO")
    public static class Response {
        private String message;

        public static Response of(boolean isVerified) {
            return new Response(isVerified ? "인증에 성공했습니다." : "인증에 실패했습니다.");
        }
    }
}
