package kr.husk.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import kr.husk.domain.auth.entity.User;
import kr.husk.domain.auth.type.OAuthProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class SignUpDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "SignUp.Request", description = "회원가입 요청 DTO")
    public static class Request {
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @Schema(description = "이메일", example = "team.dopamine.dev@gmail.com")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        private String password;

        public User toEntity() {
            return User.builder()
                    .email(email)
                    .password(password)
                    .oAuthProvider(OAuthProvider.NONE)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Schema(name = "SignUp.Response", description = "회원가입 응답 DTO")
    public static class Response {
        @Setter
        @Schema(description = "응답 메시지", example = "회원가입에 성공했습니다.")
        private String message;

        public static Response of(String message) {
            return new Response(message);
        }
    }
}
