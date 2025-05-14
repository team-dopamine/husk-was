package kr.husk.presentation.rest;

import jakarta.servlet.http.HttpServletRequest;
import kr.husk.application.connection.dto.ConnectionInfoDto;
import kr.husk.domain.connection.service.ConnectionService;
import kr.husk.presentation.api.ConnectionApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/connections")
public class ConnectionController implements ConnectionApi {

    private final ConnectionService connectionService;

    @Override
    @PostMapping("")
    public ResponseEntity<?> create(HttpServletRequest request, ConnectionInfoDto.Request dto) {
        return ResponseEntity.ok(connectionService.create(request, dto));
    }

    @Override
    @GetMapping("")
    public ResponseEntity<?> list(HttpServletRequest request) {
        return ResponseEntity.ok(connectionService.list(request));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<?> read(HttpServletRequest request, Long id) {
        return ResponseEntity.ok(connectionService.read(request, id));
    }

    @Override
    @PostMapping("/{id}/ssh-session")
    public ResponseEntity<?> connect(HttpServletRequest request, Long id) {
        return ResponseEntity.ok(connectionService.connect(request, id));
    }

    @Override
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(HttpServletRequest request, Long id, ConnectionInfoDto.Request dto) {
        return ResponseEntity.ok(connectionService.update(request, id, dto));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(HttpServletRequest request, Long id) {
        return ResponseEntity.ok(connectionService.delete(request, id));
    }
}
