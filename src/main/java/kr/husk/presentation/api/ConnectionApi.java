package kr.husk.presentation.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import kr.husk.application.connection.dto.ConnectionInfoDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "[커넥션 관련 API]", description = "사용자 SSH 커넥션 관련 API")
public interface ConnectionApi {

    @Operation(summary = "SSH 커넥션 저장", description = "SSH 접속을 위한 커넥션 정보 저장 요청 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SSH 커넥션 저장 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ConnectionInfoDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "SSH 커넥션 저장 실패")
    })
    ResponseEntity<?> create(HttpServletRequest request, @RequestBody ConnectionInfoDto.Request dto);

    @Operation(summary = "SSH 커넥션 조회", description = "SSH 커넥션 조회 요청 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SSH 커넥션 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ConnectionInfoDto.Summary.class))),
            @ApiResponse(responseCode = "400", description = "SSH 커넥션 조회 실패")
    })
    ResponseEntity<?> read(HttpServletRequest request);

    @Operation(summary = "커넥션 접속 요청", description = "커넥션 접속 요청을 위한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "커넥션 접속 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ConnectionInfoDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "커넥션 접속 실패")
    })
    ResponseEntity<?> connect(HttpServletRequest request, @PathVariable Long id);
}
