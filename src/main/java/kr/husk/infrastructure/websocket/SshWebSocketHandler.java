package kr.husk.infrastructure.websocket;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.Session;
import kr.husk.domain.connection.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SshWebSocketHandler extends TextWebSocketHandler {

    private final ConnectionService connectionService;

    private final Map<WebSocketSession, ChannelShell> sshChannels = new HashMap<>();
    private final Map<WebSocketSession, Session> sshSessions = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Web Socket 연결이 완료되었습니다. {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("받은 메시지: {}", payload);

        if (payload.startsWith("connect:")) {
            Long connectionId = Long.parseLong(payload.split(":")[1]);
            connectToSsh(session, connectionId);
        } else {
            sendCommandToSsh(session, payload);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("WebSocket 연결이 닫혔습니다: {}", session.getId());
        ChannelShell sshChannel = sshChannels.remove(session);
        if (sshChannel != null && sshChannel.isConnected()) {
            sshChannel.disconnect();
        }

        Session sshSession = sshSessions.remove(session);
        if (sshSession != null && sshSession.isConnected()) {
            sshSession.disconnect();
        }
    }

    private void connectToSsh(WebSocketSession webSocketSession, Long connectionId) throws Exception {
//        Connection connection = connectionService.read(connectionId);
//        KeyChain keyChain = connection.getKeyChain();
//
//        JSch jsch = new JSch();
//        byte[] privateKeyBytes = keyChain.getContent().getBytes(StandardCharsets.UTF_8);
//        jsch.addIdentity(connection.getUsername(), privateKeyBytes, null, null);
//
//        Session sshSession = jsch.getSession(connection.getUsername(), connection.getHost(), Integer.parseInt(connection.getPort()));
//        sshSession.setConfig("StrictHostKeyChecking", "no");
//        sshSession.connect();
//
//        ChannelShell channel = (ChannelShell) sshSession.openChannel("shell");
//        channel.setPty(true);
//        InputStream inputStream = channel.getInputStream();
//        OutputStream outputStream = channel.getOutputStream();
//        channel.connect();
//
//        sshSessions.put(webSocketSession, sshSession);
//        sshChannels.put(webSocketSession, channel);
//
//        new Thread(() -> {
//            try {
//                byte[] buffer = new byte[1024];
//                int read;
//                while ((read = inputStream.read(buffer)) != -1) {
//                    webSocketSession.sendMessage(new TextMessage(new String(buffer, 0, read, StandardCharsets.UTF_8)));
//                }
//            } catch (Exception e) {
//                log.error("SSH 출력 읽기 중 오류 발생", e);
//            }
//        }).start();
//
//        log.info("SSH 세션이 생성되었습니다: {}", sshSession);
    }

    private void sendCommandToSsh(WebSocketSession webSocketSession, String command) throws Exception {
        ChannelShell channel = sshChannels.get(webSocketSession);
        if (channel == null || !channel.isConnected()) {
            webSocketSession.sendMessage(new TextMessage("Error: SSH session is not connected."));
            return;
        }

        OutputStream outputStream = channel.getOutputStream();
        outputStream.write((command + "\n").getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
