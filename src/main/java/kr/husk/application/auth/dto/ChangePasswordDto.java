package kr.husk.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ChangePasswordDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "ChangePassword.CurrentPasswordRequest", description = "현재 비밀번호 확인 요청 DTO")
    public static class CurrentPasswordRequest {
        @NotBlank(message = "현재 비밀번호 입력은 필수값입니다.")
        private String currentPassword;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "ChangePassword.Request", description = "비밀번호 변경 요청 DTO")
    public static class Request {
        @NotBlank(message = "새로운 비밀번호 입력은 필수값입니다.")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()-_=+]).{8,16}$",
                message = "비밀번호는 8자 이상 16자 이하이며, 숫자, 소문자, 대문자, 특수문자를 포함해야 합니다.")
        private String newPassword;

        @NotBlank(message = "비밀번호 확인은 필수값입니다.")
        private String confirmPassword;
    }

    @Getter
    @AllArgsConstructor
    @Schema(name = "ChangePassword.Response", description = "사용자 비밀번호 재설정 응답 DTO")
    public static class Response {
        @Setter
        @Schema(name = "응답 메세지", description = "비밀번호 변경이 완료되었습니다.")
        private String message;

        public static Response of(String message) {
            return new Response(message);
        }
    }
}
