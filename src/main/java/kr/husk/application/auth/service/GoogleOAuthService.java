package kr.husk.application.auth.service;

import kr.husk.application.auth.config.GoogleConfig;
import kr.husk.application.auth.dto.JwtTokenDto;
import kr.husk.application.auth.dto.SignInDto;
import kr.husk.common.exception.GlobalException;
import kr.husk.common.jwt.util.JwtProvider;
import kr.husk.domain.auth.entity.User;
import kr.husk.domain.auth.exception.AuthExceptionCode;
import kr.husk.domain.auth.service.UserService;
import kr.husk.domain.auth.type.OAuthProvider;
import kr.husk.infrastructure.persistence.ConcurrentMapRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleOAuthService {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final GoogleConfig googleConfig;
    private final ConcurrentMapRefreshTokenRepository concurrentMapRefreshTokenRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<SignInDto.Response> googleSignIn(@RequestParam("code") String code) {
        String token = getToken(code);
        Map<String, Object> userInfo = getGoogleUserInfo(token);
        String email = (String) userInfo.get("email");

        if (!userService.isExist(email, OAuthProvider.GOOGLE)) {
            User user = User.builder()
                    .email(email)
                    .password(null)
                    .oAuthProvider(OAuthProvider.GOOGLE)
                    .build();

            userService.create(user);
        }

        String accessToken = jwtProvider.generateAccessToken(email);
        concurrentMapRefreshTokenRepository.create(email);
        String refreshToken = concurrentMapRefreshTokenRepository.read(email).get();

        JwtTokenDto tokenDto = JwtTokenDto.builder()
                .grantType("Bearer ")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        SignInDto.Response response = new SignInDto.Response("Google OAuth 로그인 성공", tokenDto);

        log.info("OAuth Google 로그인에 성공하였습니다. 이메일: {}", email);
        return ResponseEntity.ok(response);
    }

    public String getToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", googleConfig.getClientId());
        params.add("client_secret", googleConfig.getClientSecret());
        params.add("redirect_uri", googleConfig.getRedirectUri());
        params.add("code", code);

        Map<String, Object> response = restTemplate.postForObject(googleConfig.getTokenUri(), params, Map.class);

        if (response != null && response.containsKey("access_token")) {
            return (String) response.get("access_token");
        } else {
            throw new GlobalException(AuthExceptionCode.ACCESSTOKEN_REQUEST_FAILED);
        }

    }

    private Map<String, Object> getGoogleUserInfo(String accessToken) {
        String userInfoUrl = googleConfig.getUserInfoUri() + "?access_token=" + accessToken;

        Map<String, Object> userInfoResponse = restTemplate.getForObject(userInfoUrl, Map.class);

        if (userInfoResponse != null) {
            return userInfoResponse;
        } else {
            throw new GlobalException(AuthExceptionCode.GOOGLE_USERINFO_NOTFOUND);
        }
    }
}