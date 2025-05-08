package kr.husk.infrastructure.websocket;

import kr.husk.common.service.EncryptionService;
import kr.husk.domain.connection.entity.Connection;
import kr.husk.domain.connection.service.ConnectionService;
import kr.husk.domain.keychain.entity.KeyChain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.PTYMode;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Shell;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SshWebSocketHandler extends TextWebSocketHandler {

    private final ConnectionService connectionService;
    private final EncryptionService encryptionService;
    private final Map<WebSocketSession, Shell> sshShells = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, SSHClient> sshClients = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, Session> sshSessions = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, ExecutorService> executors = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Web Socket 연결이 완료되었습니다. {}", session.getId());
        executors.put(session, Executors.newSingleThreadExecutor());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("전송할 메시지: {}", payload);
        if (payload.startsWith("connect:")) {
            Long connectionId = Long.parseLong(payload.split(":")[1]);
            log.info("{}번 Connection으로 접속합니다.", connectionId);
            connectToSsh(session, connectionId);
        } else {
            Long connectionId = (Long) session.getAttributes().get("connectionId");
            log.info("Connection ID: {}, 메시지: {}",
                    connectionId != null ? connectionId : "연결 없음",
                    payload);
            sendCommandToSsh(session, payload);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus status) throws Exception {
        cleanupResources(webSocketSession);
        log.info("{}번 Connection과의 WebSocket 연결이 종료되었습니다", webSocketSession.getAttributes().get("connectionId"));
    }

    private void connectToSsh(WebSocketSession webSocketSession, Long connectionId) throws Exception {
        Connection connection = connectionService.read(connectionId);
        KeyChain keyChain = connection.getKeyChain();

        SSHClient sshClient = new SSHClient();
        sshClient.addHostKeyVerifier(new PromiscuousVerifier());

        webSocketSession.getAttributes().put("connectionId", connectionId);
        try {
            sshClient.connect(connection.getHost(), Integer.parseInt(connection.getPort()));
            KeyProvider keyProvider = sshClient.loadKeys(
                    encryptionService.decrypt(keyChain.getContent()),
                    null,
                    null
            );
            sshClient.authPublickey(connection.getUsername(), keyProvider);

            // 세션 및 셸 채널 열기
            Session session = sshClient.startSession();

            // Echo 모드 비활성화
            Map<PTYMode, Integer> ptyConfig = new HashMap<>();
            ptyConfig.put(PTYMode.ECHO, 0);
            session.allocatePTY("xterm", 80, 24, 0, 0, ptyConfig);

            Shell shell = session.startShell();

            // 맵에 저장
            sshClients.put(webSocketSession, sshClient);
            sshSessions.put(webSocketSession, session);
            sshShells.put(webSocketSession, shell);

            // 별도 스레드에서 출력 읽기
            ExecutorService executor = executors.get(webSocketSession);
            executor.submit(() -> {
                try {
                    InputStream inputStream = shell.getInputStream();
                    byte[] buffer = new byte[8192];
                    int read;

                    // 초기 출력 대기
                    Thread.sleep(500);

                    while (!Thread.currentThread().isInterrupted()) {
                        read = inputStream.read(buffer);
                        if (read > 0) {
                            String output = new String(buffer, 0, read, StandardCharsets.UTF_8);
                            log.debug("Received output: [{}]", output);
                            if (webSocketSession.isOpen()) {
                                webSocketSession.sendMessage(new TextMessage(output));
                            }
                        }
                        Thread.sleep(10);
                    }
                } catch (IOException | InterruptedException e) {
                    if (webSocketSession.isOpen()) {
                        try {
                            log.error("SSH 출력 읽기 중 오류 발생", e);
                            webSocketSession.sendMessage(new TextMessage("Error: " + e.getMessage()));
                        } catch (IOException ex) {
                            log.error("에러 메시지 전송 실패", ex);
                        }
                    }
                    Thread.currentThread().interrupt();
                }
            });

            log.info("SSH 세션이 생성되었습니다: {}@{}", connection.getUsername(), connection.getHost());

        } catch (Exception e) {
            log.error("SSH 연결 실패", e);
            if (sshClient.isConnected()) {
                sshClient.disconnect();
            }
            webSocketSession.sendMessage(new TextMessage("Connection failed: " + e.getMessage()));
            throw e;
        }
    }

    private void sendCommandToSsh(WebSocketSession webSocketSession, String command) throws Exception {
        Shell shell = sshShells.get(webSocketSession);
        Long connectionId = (Long) webSocketSession.getAttributes().get("connectionId");

        validateSshResources(webSocketSession);
        validateSshConnection(webSocketSession);

        try {
            OutputStream outputStream = shell.getOutputStream();

            // 명령어만 전송, 응답은 백그라운드 스레드에서 처리(connectToSsh 메소드의 스레드)
            byte[] commandBytes = command.getBytes(StandardCharsets.UTF_8);
            outputStream.write(commandBytes);
            outputStream.flush();

        } catch (IOException e) {
            log.error("{}번 Connection으로의 메시지 전송 실패: {}", connectionId, e.getMessage(), e);
            webSocketSession.sendMessage(new TextMessage("Command failed: " + e.getMessage()));
            cleanupResources(webSocketSession);
        }
    }

    /**
     * 자원 정리 메소드
     *
     * @param webSocketSession
     */
    private void cleanupResources(WebSocketSession webSocketSession) {
        Optional.ofNullable(sshShells.remove(webSocketSession))
                .ifPresent(shell -> {
                    try {
                        shell.close();
                    } catch (IOException ignored) {
                    }
                });

        Optional.ofNullable(sshSessions.remove(webSocketSession))
                .ifPresent(session -> {
                    try {
                        session.close();
                    } catch (IOException ignored) {
                    }
                });

        Optional.ofNullable(sshClients.remove(webSocketSession))
                .ifPresent(client -> {
                    try {
                        client.disconnect();
                    } catch (Exception ignored) {
                    }
                });

        Optional.ofNullable(executors.remove(webSocketSession))
                .ifPresent(ExecutorService::shutdownNow);
    }

    /**
     * SSH Connection 연결 상태 확인
     *
     * @param webSocketSession
     */
    private void validateSshConnection(WebSocketSession webSocketSession) {
        SSHClient sshClient = sshClients.get(webSocketSession);
        Session session = sshSessions.get(webSocketSession);
        if (!sshClient.isConnected() || !session.isOpen()) {
            try {
                log.error("SSH 연결 끊김: sshClient.connected={}, session.open={}",
                        sshClient.isConnected(), session.isOpen());
                webSocketSession.sendMessage(new TextMessage("Error: SSH connection lost"));
                cleanupResources(webSocketSession);
            } catch (IOException e) {
                log.error("에러 메시지 전송 실패", e);
            }
        }
    }

    /**
     * SSH Connection 관련 자원 상태 확인
     *
     * @param webSocketSession
     * @throws Exception
     */
    private void validateSshResources(WebSocketSession webSocketSession) throws Exception {
        Shell shell = sshShells.get(webSocketSession);
        Session session = sshSessions.get(webSocketSession);
        SSHClient sshClient = sshClients.get(webSocketSession);

        Long id = (Long) webSocketSession.getAttributes().get("connectionId");
        if (shell == null || session == null || sshClient == null) {
            webSocketSession.sendMessage(new TextMessage("Error: SSH session is not connected."));
            log.error("{}번 Connection에 대한 SSH session이 연결되지 않았습니다.", id);
        }
    }
}