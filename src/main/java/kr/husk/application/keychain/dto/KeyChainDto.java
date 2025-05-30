package kr.husk.application.keychain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import kr.husk.common.service.EncryptionService;
import kr.husk.domain.keychain.entity.KeyChain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

public class KeyChainDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "KeyChain.Request", description = "키체인 등록 요청 DTO")
    public static class Request {
        @NotBlank(message = "이름은 필수 입력값입니다.")
        @Schema(description = "키체인명", example = "test")
        private String name;

        @NotBlank(message = "키체인 내용은 필수 입력값입니다.")
        @Schema(description = "키체인 내용", example = "MSxzAclsdQsdp")
        private String content;
    }

    @Getter
    @AllArgsConstructor
    @Schema(name = "KeyChain.Response", description = "키체인 등록 응답 DTO")
    public static class Response {
        @Setter
        @Schema(description = "응답 메시지", example = "키체인 등록이 완료되었습니다.")
        private String message;

        public static Response of(String message) {
            return new Response(message);
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "KeyChain.Summary", description = "키체인 조회 응답 DTO")
    public static class Overview {
        @Schema(description = "키체인 id", example = "1")
        private Long id;

        @Schema(description = "키체인명", example = "husk")
        private String name;

        public static List<Overview> from(List<KeyChain> keyChains) {
            return keyChains.stream()
                    .filter(keyChain -> keyChain.isDeleted() == false)
                    .map(keyChain -> KeyChainDto.Overview.builder()
                            .id(keyChain.getId())
                            .name(keyChain.getName())
                            .build())
                    .collect(Collectors.toUnmodifiableList());
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "KeyChain.Payload", description = "키체인 복호화 응답 DTO")
    public static class Payload {
        @Schema(description = "키체인 내용", example = "Mxsa59s*2d^dsa")
        private String content;

        public static Payload from(KeyChain keyChain, EncryptionService encryptionService) {
            return Payload.builder()
                    .content(encryptionService.decrypt(keyChain.getContent()))
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "KeyChain.UpdateRequest", description = "키체인 수정 DTO")
    public static class UpdateRequest {
        @Schema(description = "키체인 id", example = "1")
        private Long id;

        @NotBlank(message = "이름은 필수 입력값입니다.")
        @Schema(description = "키체인명", example = "husk")
        private String name;

        @NotBlank(message = "키체인 내용은 필수 입력값입니다.")
        @Schema(description = "키체인 내용", example = "Mxsa59s*2d^dsa")
        private String content;
    }

}









