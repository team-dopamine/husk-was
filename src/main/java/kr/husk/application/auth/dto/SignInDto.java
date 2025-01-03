package kr.husk.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SignInDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "SignIn.Request", description = "로그인 요청 DTO")
    public static class Request {

        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;
        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        private String password;

    }

    @Getter
    @AllArgsConstructor
    @Schema(name = "SignIn.Request", description = "로그인 요청 DTO")
    public static class Response {
        private String message;
        private JwtTokenDto jwtTokenDto;
    }
}