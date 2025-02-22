package kr.husk.presentation.rest;

import jakarta.servlet.http.HttpServletRequest;
import kr.husk.application.keychain.dto.KeyChainDto;
import kr.husk.domain.keychain.service.KeyChainService;
import kr.husk.presentation.api.KeyChainApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/keychains")
@RequiredArgsConstructor
public class KeyChainController implements KeyChainApi {

    private final KeyChainService keyChainService;

    @Override
    @PostMapping("")
    public ResponseEntity<?> create(HttpServletRequest request, KeyChainDto.Request dto) {
        return ResponseEntity.ok(keyChainService.create(request, dto));
    }
}
