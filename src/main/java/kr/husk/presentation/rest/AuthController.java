package kr.husk.presentation.rest;

import kr.husk.application.auth.dto.SendAuthCodeDto;
import kr.husk.application.auth.dto.SignUpDto;
import kr.husk.application.auth.dto.VerifyAuthCodeDto;
import kr.husk.application.auth.service.AuthService;
import kr.husk.presentation.api.AuthApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {
    private final AuthService authService;

    @Override
    @PostMapping("/send-code")
    public ResponseEntity<?> sendAuthCode(SendAuthCodeDto.Request dto) {
        return ResponseEntity.ok(authService.sendAuthCode(dto));
    }

    @Override
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyAuthCode(VerifyAuthCodeDto.Request dto) {
        return ResponseEntity.ok(authService.verifyAuthCode(dto));
    }

    @Override
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(SignUpDto.Request dto) {
        return ResponseEntity.created(null).body(authService.signUp(dto));
    }

    @Override
    @GetMapping("/terms-of-service")
    public ResponseEntity<?> getTermsOfService() {
        return ResponseEntity.ok(authService.readTermsOfService());
    }
}
