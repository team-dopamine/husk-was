package kr.husk.application.connection.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.husk.domain.auth.entity.User;
import kr.husk.domain.connection.entity.Connection;
import kr.husk.domain.keychain.entity.KeyChain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ConnectionInfoDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "ConnectionInfo.Request", description = "SSH 커넥션 저장 요청 DTO")
    public static class Request {
        private String name;
        private String host;
        private String username;
        private String port;
        private String keyChainName;

        public Connection toEntity(User user, KeyChain keyChain) {
            return Connection.builder()
                    .user(user)
                    .keyChain(keyChain)
                    .name(name)
                    .host(host)
                    .username(username)
                    .port(port)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Schema(name = "ConnectionInfo.Response", description = "SSH 커넥션 저장 요청에 대한 응답 DTO")
    public static class Response {
        private String message;

        public static Response of(String message) {
            return new Response(message);
        }
    }
}
