package kr.husk.presentation.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.husk.application.auth.dto.SendAuthCodeDto;
import kr.husk.application.auth.dto.SignInDto;
import kr.husk.application.auth.dto.SignUpDto;
import kr.husk.application.auth.dto.VerifyAuthCodeDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "[인증 관련 API]", description = "사용자 인증 관련 API")
@Validated
public interface AuthApi {
    @Operation(summary = "인증 코드 전송", description = "이메일로 인증 코드를 전송하기 위한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 코드 전송 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SendAuthCodeDto.Response.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "인증 코드 전송 실패")
    })
    ResponseEntity<?> sendAuthCode(@RequestBody SendAuthCodeDto.Request dto);

    @Operation(summary = "인증 코드 검증", description = "이메일로 전송된 인증 코드를 검증하기 위한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 코드 검증 성공"),
            @ApiResponse(responseCode = "400", description = "인증 코드 검증 실패")
    })
    ResponseEntity<?> verifyAuthCode(@RequestBody VerifyAuthCodeDto.Request dto);

    @Operation(summary = "일반 사용자 회원가입", description = "일반 회원가입을 위한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "회원가입 실패")
    })
    ResponseEntity<?> signUp(@Valid @RequestBody SignUpDto.Request dto);

    @Operation(summary = "서비스 사용자 약관 조회", description = "서비스 사용자 약관을 조회하기 위한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "약관 조회 성공"),
            @ApiResponse(responseCode = "400", description = "약관 조회 실패")
    })
    ResponseEntity<?> getTermsOfService();

    @Operation(summary = "일반 사용자 로그인", description = "일반 로그인을 위한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "로그인 실패")
    })
    ResponseEntity<?> signIn(@Valid @RequestBody SignInDto.Request dto);
}
