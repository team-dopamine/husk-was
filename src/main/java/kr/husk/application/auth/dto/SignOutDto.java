package kr.husk.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class SignOutDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "SignOut.Request", description = "로그아웃 요청 DTO")
    public static class Request {
        private String refreshToken;
    }

    @Getter
    @AllArgsConstructor
    @Schema(name = "SignOut.Response", description = "로그아웃 응답 DTO")
    public static class Response {
        @Setter
        @Schema(description = "응답 메시지", example = "로그아웃에 성공하였습니다.")
        private String message;

        public static Response of(String message) {
            return new Response(message);
        }
    }
}
