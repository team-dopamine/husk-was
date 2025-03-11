package kr.husk.application.connection.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.husk.domain.auth.entity.User;
import kr.husk.domain.connection.entity.Connection;
import kr.husk.domain.keychain.entity.KeyChain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

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

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(name = "ConnectionInfo.Summary", description = "SSH 커넥션 조회에 대한 단순 정보 응답 DTO")
    public static class Summary {
        private Long id;
        private String name;
        private String host;
        private String port;

        public static List<Summary> from(List<Connection> connections) {
            return connections.stream()
                    .filter(connection -> connection.isDeleted() == false)
                    .map(connection -> Summary.builder()
                            .id(connection.getId())
                            .name(connection.getName())
                            .host(masking(connection.getHost()))
                            .port(connection.getPort())
                            .build())
                    .collect(Collectors.toUnmodifiableList());
        }

        private static String masking(String host) {
            String[] parts = host.split("\\.");
            return parts[0] + "." + parts[1] + ".*.*";
        }
    }
}
