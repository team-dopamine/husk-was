package kr.husk.presentation.rest;

import jakarta.servlet.http.HttpServletRequest;
import kr.husk.application.auth.dto.ChangePasswordDto;
import kr.husk.application.auth.dto.SendAuthCodeDto;
import kr.husk.application.auth.dto.SignInDto;
import kr.husk.application.auth.dto.SignOutDto;
import kr.husk.application.auth.dto.SignUpDto;
import kr.husk.application.auth.dto.VerifyAuthCodeDto;
import kr.husk.application.auth.service.AuthService;
import kr.husk.application.auth.service.GitHubOAuthService;
import kr.husk.application.auth.service.GoogleOAuthService;
import kr.husk.common.exception.GlobalException;
import kr.husk.domain.auth.exception.AuthExceptionCode;
import kr.husk.presentation.api.AuthApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {
    private final AuthService authService;
    private final GoogleOAuthService googleOAuthService;
    private final GitHubOAuthService gitHubOAuthService;

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

    @Override
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(SignInDto.Request dto) {
        return ResponseEntity.ok(authService.signIn(dto));
    }

    @Override
    @GetMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestParam("type") String type, @RequestParam("code") String code) {
        if ("google".equals(type)) {
            return ResponseEntity.ok(googleOAuthService.googleSignIn(code));
        } else if ("github".equals(type)) {
            return ResponseEntity.ok(gitHubOAuthService.gitHubSignIn(code));
        } else {
            throw new GlobalException(AuthExceptionCode.NOT_ALLOWED_TYPE);
        }
    }

    @Override
    @PostMapping("/sign-out")
    public ResponseEntity<?> signOut(SignOutDto.Request dto, HttpServletRequest request) {
        return ResponseEntity.ok(authService.signOut(dto, request));
    }

    @Override
    @PatchMapping("/user")
    public ResponseEntity<?> updatePassword(ChangePasswordDto.Request dto, HttpServletRequest request) {
        return ResponseEntity.ok(authService.changePassword(dto, request));
    }
}
