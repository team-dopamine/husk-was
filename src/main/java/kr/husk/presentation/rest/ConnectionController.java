package kr.husk.presentation.rest;

import jakarta.servlet.http.HttpServletRequest;
import kr.husk.application.connection.dto.ConnectionInfoDto;
import kr.husk.domain.connection.service.ConnectionService;
import kr.husk.presentation.api.ConnectionApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/connections")
public class ConnectionController implements ConnectionApi {

    private final ConnectionService connectionService;

    @Override
    @PostMapping("")
    public ResponseEntity<?> create(HttpServletRequest request, ConnectionInfoDto.Request dto) {
        return ResponseEntity.ok(connectionService.create(request, dto));
    }

    @Override
    @GetMapping("")
    public ResponseEntity<?> read(HttpServletRequest request) {
        return ResponseEntity.ok(connectionService.read(request));
    }

}
