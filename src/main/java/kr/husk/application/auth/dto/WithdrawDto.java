package kr.husk.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class WithdrawDto {

    @Getter
    @AllArgsConstructor
    @Schema(name = "Withdraw.Response", description = "회원 탈퇴 응답 DTO")
    public static class Response {
        @Setter
        @Schema(description = "응답 메시지", example = "탈퇴 처리가 성공적으로 완료되었습니다.")
        private String message;
        private LocalDateTime withdrawnAt;

        public static Response of(String message) {
            return new Response(message, LocalDateTime.now());
        }
    }
}
