package kr.husk.presentation.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.husk.application.auth.dto.SendAuthCodeDto;
import kr.husk.application.auth.dto.VerifyAuthCodeDto;
import kr.husk.application.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void sendAuthCodeApiTest() throws Exception {
        // give
        SendAuthCodeDto.Request dto = new SendAuthCodeDto.Request("jinlee1703@gmail.com");

        // when
        when(authService.sendAuthCode(dto)).thenReturn(SendAuthCodeDto.Response.of("인증 코드가 성공적으로 전송되었습니다."));

        // then
        mockMvc.perform(post("/auth/send-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void verifyAuthCodeApiTest() throws Exception {
        // give
        VerifyAuthCodeDto.Request dto = new VerifyAuthCodeDto.Request("jinlee1703@gmail.com", "123456");

        // when
        when(authService.verifyAuthCode(dto)).thenReturn(VerifyAuthCodeDto.Response.of("인증에 성공했습니다."));

        // then
        mockMvc.perform(post("/auth/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}