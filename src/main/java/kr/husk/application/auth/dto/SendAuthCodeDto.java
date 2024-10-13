package kr.husk.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import kr.husk.domain.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class SendAuthCodeDto {
    @Getter
    @AllArgsConstructor
    @Schema(name = "SendAuthCode.Request", description = "인증 코드 전송 요청 DTO")
    public static class Request {
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @Schema(description = "이메일", example = "team.dopamine.dev@gmail.com")
        private String email;

        public User toEntity() {
            return User.builder()
                    .email(email)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Schema(name = "SendAuthCode.Response", description = "인증 코드 전송 응답 DTO")
    public static class Response {
        @Setter
        @Schema(description = "응답 메시지", example = "인증 코드가 전송되었습니다.")
        private String message;

        public static Response of(String message) {
            return new Response(message);
        }
    }
}
