package kr.husk.presentation.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.husk.application.keychain.dto.KeyChainDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "[키체인 관련 API]", description = "키체인(키페어) 관련 API")
public interface KeyChainApi {
    @Operation(summary = "키체인 등록", description = "키체인 등록을 위한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "키체인 등록 완료",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = KeyChainDto.Response.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "키체인 등록 실패")
    })
    ResponseEntity<?> create(HttpServletRequest request, @Valid @RequestBody KeyChainDto.Request dto);

    @Operation(summary = "키체인 조회[목록]", description = "Dashboard 키체인 목록 조회를 위한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "키체인 조회 완료",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = KeyChainDto.Overview.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "키체인 조회 실패")
    })
    ResponseEntity<?> read(HttpServletRequest request);

    @Operation(summary = "키체인 수정", description = "키체인 수정을 위한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "키체인 수정 완료",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = KeyChainDto.Response.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "키체인 수정 실패")
    })
    ResponseEntity<?> update(HttpServletRequest request, @Valid @RequestBody KeyChainDto.UpdateRequest dto);

    @Operation(summary = "키체인 삭제", description = "키체인 삭제를 위한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "키체인 삭제 완료",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = KeyChainDto.Response.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "키체인 삭제 실패")
    })
    ResponseEntity<?> delete(HttpServletRequest request, @PathVariable Long id);

    @Operation(summary = "키체인 암호 복호화", description = "키체인 암호 복호화를 위한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "키체인 복호화 완료",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = KeyChainDto.Payload.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "키체인 복호화 실패")
    })
    ResponseEntity<?> decryptContent(HttpServletRequest request, @PathVariable Long id);
}
