package kr.husk.application.auth.service;

import kr.husk.application.auth.config.GitHubConfig;
import kr.husk.application.auth.dto.JwtTokenDto;
import kr.husk.application.auth.dto.SignInDto;
import kr.husk.common.exception.GlobalException;
import kr.husk.common.jwt.util.JwtProvider;
import kr.husk.domain.auth.entity.User;
import kr.husk.domain.auth.exception.AuthExceptionCode;
import kr.husk.domain.auth.exception.UserExceptionCode;
import kr.husk.domain.auth.repository.OAuthTokenRepository;
import kr.husk.domain.auth.service.UserService;
import kr.husk.domain.auth.type.OAuthProvider;
import kr.husk.infrastructure.persistence.ConcurrentMapRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubOAuthService {
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final GitHubConfig gitHubConfig;
    private final ConcurrentMapRefreshTokenRepository concurrentMapRefreshTokenRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final OAuthTokenRepository oAuthTokenRepository;

    public ResponseEntity<SignInDto.Response> gitHubSignIn(@RequestParam("code") String code) {
        String token = getToken(code);
        Map<String, Object> userInfo = getGitHubUserInfo(token);
        String email = userInfo.get("login") + "@github.com";
        oAuthTokenRepository.create(email, token);

        checkUser(email);

        String accessToken = jwtProvider.generateAccessToken(email);
        concurrentMapRefreshTokenRepository.create(email);
        String refreshToken = concurrentMapRefreshTokenRepository.read(email).get();

        JwtTokenDto tokenDto = JwtTokenDto.builder()
                .grantType("Bearer ")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        SignInDto.Response response = new SignInDto.Response("GitHub OAuth 로그인 성공 ", tokenDto);

        log.info("OAuth GitHub 로그인에 성공하였습니다. 이메일: {}", email);
        return ResponseEntity.ok(response);
    }

    public String getToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", gitHubConfig.getClientId());
        params.add("client_secret", gitHubConfig.getClientSecret());
        params.add("redirect_uri", gitHubConfig.getRedirectUri());
        params.add("code", code);

        Map<String, Object> response = restTemplate.postForObject(gitHubConfig.getTokenUri(), params, Map.class);

        if (response != null && response.containsKey("access_token")) {
            return (String) response.get("access_token");
        } else {
            throw new GlobalException(AuthExceptionCode.ACCESSTOKEN_REQUEST_FAILED);
        }

    }

    private Map<String, Object> getGitHubUserInfo(String accessToken) {
        String userInfoUrl = gitHubConfig.getUserInfoUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Accept", "application/json");

        HttpEntity<?> entity = new HttpEntity<>(headers);

        Map<String, Object> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, Map.class).getBody();

        if (userInfoResponse != null) {
            return userInfoResponse;
        } else {
            throw new GlobalException(AuthExceptionCode.OAUTH_USERINFO_NOTFOUND);
        }
    }

    public void checkUser(String email) {
        User user = userService.read(email);
        if (user != null && user.getOAuthProvider() != OAuthProvider.GITHUB) {
            throw new GlobalException(UserExceptionCode.EMAIL_ALREADY_EXISTS);
        }

        if (user == null) {
            User newUser = User.builder()
                    .email(email)
                    .password(null)
                    .oAuthProvider(OAuthProvider.GITHUB)
                    .build();

            userService.create(newUser);
        } else {
            if (user.isDeleted()) {
                if (user.isWithin30DaysFromDeletion()) {
                    log.info("[GitHub OAuth] 30일 이내에 탈퇴한 OAuth 계정으로 로그인이 시도되어 계정이 복구됩니다.");
                    user.restore();
                    userService.update(user, null);
                } else {
                    throw new GlobalException(UserExceptionCode.WITHDRAWN_USER);
                }
            }
        }
    }

    public void unlink(String email) {
        try {
            String token = oAuthTokenRepository.read(email);
            String revokeUrl = gitHubConfig.getRevokeUrl() + gitHubConfig.getClientId() + "/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(gitHubConfig.getClientId(), gitHubConfig.getClientSecret());

            Map<String, String> body = new HashMap<>();
            body.put("access_token", token);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
            restTemplate.exchange(revokeUrl, HttpMethod.DELETE, request, String.class);

            log.info("[GitHub OAuth] 이메일: {}이 연결 해제 되었습니다.", email);
        } catch (Exception e) {
            log.error("[GitHub OAuth] 이메일: {}의 연결 해제 중 오류가 발생하였습니다.", email);
            throw new GlobalException(AuthExceptionCode.OAUTH_UNLINK_FAILED);
        }
    }
}
