package kr.husk.presentation.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kr.husk.application.auth.dto.SendAuthCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthApi {
    @Operation(summary = "인증 코드 전송", description = "이메일로 인증 코드를 전송하기 위한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 코드 전송 성공"),
            @ApiResponse(responseCode = "400", description = "인증 코드 전송 실패")
    })
    ResponseEntity<?> sendAuthCode(@RequestBody SendAuthCode.Request dto);
}
