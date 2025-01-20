package kr.husk.application.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import kr.husk.application.auth.dto.JwtTokenDto;
import kr.husk.application.auth.dto.SendAuthCodeDto;
import kr.husk.application.auth.dto.SignInDto;
import kr.husk.application.auth.dto.SignOutDto;
import kr.husk.application.auth.dto.SignUpDto;
import kr.husk.application.auth.dto.VerifyAuthCodeDto;
import kr.husk.common.exception.GlobalException;
import kr.husk.common.jwt.util.JwtProvider;
import kr.husk.domain.auth.entity.User;
import kr.husk.domain.auth.exception.AuthExceptionCode;
import kr.husk.domain.auth.exception.UserExceptionCode;
import kr.husk.domain.auth.repository.AuthCodeRepository;
import kr.husk.domain.auth.service.UserService;
import kr.husk.domain.auth.type.OAuthProvider;
import kr.husk.infrastructure.config.AuthConfig;
import kr.husk.infrastructure.persistence.ConcurrentMapRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthConfig authConfig;
    private final JavaMailSender mailSender;
    private final UserService userService;
    private final AuthCodeRepository authCodeRepository;
    private final JwtProvider jwtProvider;
    private final ConcurrentMapRefreshTokenRepository concurrentMapRefreshTokenRepository;

    @Transactional
    public SendAuthCodeDto.Response sendAuthCode(SendAuthCodeDto.Request dto) {
        if (userService.isExist(dto.getEmail(), OAuthProvider.NONE)) {
            throw new GlobalException(UserExceptionCode.EMAIL_ALREADY_EXISTS);
        }

        String authCode = generateAuthCode();
        authCodeRepository.create(dto.getEmail(), authCode);

        try {
            sendEmail(dto.getEmail(), authCode);
            log.info("인증 코드가 성공적으로 전송되었습니다. 이메일: {}", dto.getEmail());
        } catch (Exception e) {
            log.error("이메일 전송 실패. 이메일: {}", dto.getEmail(), e);
            throw new GlobalException(AuthExceptionCode.EMAIL_SEND_FAILED);
        }

        return SendAuthCodeDto.Response.of("인증 코드가 성공적으로 전송되었습니다.");
    }

    @Transactional
    public VerifyAuthCodeDto.Response verifyAuthCode(VerifyAuthCodeDto.Request dto) {
        String savedCode = authCodeRepository.read(dto.getEmail());
        if (savedCode != null && savedCode.equals(dto.getAuthCode())) {
            authCodeRepository.delete(dto.getEmail());
            log.info("인증에 성공했습니다. 이메일: {}", dto.getEmail());
            return VerifyAuthCodeDto.Response.of("인증에 성공했습니다.");
        }
        log.error("인증에 실패했습니다. 이메일: {}", dto.getEmail());
        throw new GlobalException(AuthExceptionCode.VERIFICATION_CODE_NOT_MATCH);
    }

    @Transactional
    public SignUpDto.Response signUp(SignUpDto.Request dto) {
        if (userService.isExist(dto.getEmail(), OAuthProvider.NONE)) {
            throw new GlobalException(UserExceptionCode.EMAIL_ALREADY_EXISTS);
        }
        User user = dto.toEntity();
        user.encodePassword(authConfig.passwordEncoder());
        userService.create(user);
        log.info("회원가입에 성공했습니다. 이메일: {}", dto.getEmail());
        return SignUpDto.Response.of("회원가입에 성공했습니다.");
    }

    @Transactional
    public SignInDto.Response signIn(SignInDto.Request dto) {
        User user = userService.read(dto.getEmail(), OAuthProvider.NONE);
        if (!user.isMatched(authConfig.passwordEncoder(), dto.getPassword())) {
            log.info("비밀번호가 일치하지 않습니다. 이메일: {}", dto.getEmail());
            throw new GlobalException(AuthExceptionCode.PASSWORD_MISMATCHED);
        }

        String accessToken = jwtProvider.generateAccessToken(user.getEmail());
        concurrentMapRefreshTokenRepository.create(user.getEmail());
        String refreshToken = concurrentMapRefreshTokenRepository.read(user.getEmail()).get();

        JwtTokenDto tokenDto = JwtTokenDto.builder()
                .grantType("Bearer ")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        log.info("로그인에 성공하였습니다. 이메일: {}", dto.getEmail());
        return new SignInDto.Response("로그인에 성공하였습니다.", tokenDto);
    }

    @Transactional
    public SignOutDto.Response signOut(SignOutDto.Request dto, HttpServletRequest request) {
        String accessToken = jwtProvider.resolveToken(request);
        String email = jwtProvider.getEmail(accessToken);
        if (jwtProvider.validateToken(accessToken)) {
            String refreshToken = dto.getRefreshToken().substring(7);
            String storedRefreshToken = concurrentMapRefreshTokenRepository.read(email)
                    .orElseThrow(() -> new GlobalException(AuthExceptionCode.REFRESH_TOKEN_NOT_FOUND));

            if (!storedRefreshToken.equals(refreshToken) || !jwtProvider.validateToken(refreshToken)) {
                throw new GlobalException(AuthExceptionCode.INVALID_REFRESH_TOKEN);
            }

            concurrentMapRefreshTokenRepository.delete(email);
        } else {
            throw new GlobalException(AuthExceptionCode.INVALID_ACCESS_TOKEN);
        }
        log.info("로그아웃에 성공했습니다: 이메일: { " + email + " }");
        return SignOutDto.Response.of("로그아웃에 성공하였습니다.");
    }

    public String readTermsOfService() {
        return "제1장 총칙\n" +
                "\n" +
                "제1조 (목적)\n" +
                "본 약관은 Dopamine(이하 \"회사\"라 함)이 제공하는 서비스(이하 \"서비스\"라 함)의 이용조건 및 절차, 회사와 회원간의 권리, 의무 및 책임사항 등을 규정함을 목적으로 합니다.\n" +
                "\n" +
                "제2조 (용어의 정의)\n" +
                "1. \"서비스\"란 회사가 제공하는 모든 서비스를 의미합니다.\n" +
                "2. \"회원\"이란 회사와 서비스 이용계약을 체결하고 회사가 제공하는 서비스를 이용하는 개인 또는 법인을 말합니다.\n" +
                "3. \"아이디(ID)\"란 회원의 식별과 서비스 이용을 위하여 회원이 선정하고 회사가 승인하는 문자와 숫자의 조합을 말합니다.\n" +
                "\n" +
                "제2장 서비스 이용계약\n" +
                "\n" +
                "제3조 (이용계약의 성립)\n" +
                "1. 이용계약은 회원이 되고자 하는 자가 본 약관에 동의하고 회사가 정한 절차에 따라 가입신청을 하며, 회사가 이를 승낙함으로써 체결됩니다.\n" +
                "2. 회사는 가입신청자의 신청에 대하여 서비스 이용을 승낙함을 원칙으로 합니다.\n" +
                "\n" +
                "제4조 (회원가입)\n" +
                "1. 회원가입은 신청자가 온라인으로 회사가 제공하는 회원가입 양식에 필요한 정보를 기입하고 본 약관에 동의한다는 의사표시를 함으로써 이루어집니다.\n" +
                "2. 회사는 다음 각 호에 해당하는 회원가입 신청에 대해서는 승낙을 하지 않을 수 있습니다.\n" +
                "   - 실명이 아니거나 타인의 명의를 도용한 경우\n" +
                "   - 허위의 정보를 기재한 경우\n" +
                "   - 기타 회원으로 등록하는 것이 부적절하다고 판단되는 경우\n" +
                "\n" +
                "제3장 권리와 의무\n" +
                "\n" +
                "제5조 (회사의 의무)\n" +
                "1. 회사는 관련 법령과 본 약관이 금지하거나 미풍양속에 반하는 행위를 하지 않습니다.\n" +
                "2. 회사는 서비스 제공과 관련하여 취득한 회원의 개인정보를 본인의 승낙 없이 제3자에게 제공하지 않습니다.\n" +
                "\n" +
                "제6조 (회원의 의무)\n" +
                "1. 회원은 서비스 이용과 관련하여 다음 각 호의 행위를 하여서는 안됩니다.\n" +
                "   - 타인의 정보도용\n" +
                "   - 회사가 제공하는 서비스의 운영을 방해하는 행위\n" +
                "   - 타인의 명예를 손상시키거나 불이익을 주는 행위\n" +
                "   - 기타 관련 법령에 위배되는 행위\n" +
                "\n" +
                "제4장 서비스 이용\n" +
                "\n" +
                "제7조 (서비스 이용시간)\n" +
                "1. 서비스 이용시간은 회사의 업무상 또는 기술상 특별한 지장이 없는 한 연중무휴, 1일 24시간을 원칙으로 합니다.\n" +
                "2. 회사는 서비스를 일정범위로 분할하여 각 범위별로 이용가능 시간을 별도로 정할 수 있습니다.\n" +
                "\n" +
                "제8조 (서비스 이용의 제한 및 중지)\n" +
                "1. 회사는 다음 각 호의 경우에 서비스 제공을 중지할 수 있습니다.\n" +
                "   - 서비스용 설비의 보수 등 공사로 인한 부득이한 경우\n" +
                "   - 전기통신사업법에 규정된 기간통신사업자가 전기통신서비스를 중지했을 경우\n" +
                "   - 기타 불가항력적 사유가 있는 경우\n" +
                "\n" +
                "제5장 계약해지 및 이용제한\n" +
                "\n" +
                "제9조 (계약해지 및 이용제한)\n" +
                "1. 회원이 이용계약을 해지하고자 하는 때에는 회원 본인이 온라인을 통해 회사에 해지신청을 하여야 합니다.\n" +
                "2. 회사는 회원이 다음 각 호에 해당하는 행위를 하였을 경우 사전통지 없이 이용계약을 해지할 수 있습니다.\n" +
                "   - 타인의 서비스 이용을 방해하거나 정보를 도용하는 등 전자거래질서를 위협하는 경우\n" +
                "   - 서비스를 이용하여 법령과 본 약관이 금지하거나 공서양속에 반하는 행위를 하는 경우\n" +
                "\n" +
                "제6장 손해배상 및 기타사항\n" +
                "\n" +
                "제10조 (손해배상)\n" +
                "회사와 회원은 서비스 이용과 관련하여 고의 또는 과실로 상대방에게 손해를 입힌 경우에는 이를 배상할 책임이 있습니다.\n" +
                "\n" +
                "제11조 (분쟁해결)\n" +
                "1. 회사와 회원은 서비스와 관련하여 발생한 분쟁을 원만하게 해결하기 위하여 필요한 모든 노력을 하여야 합니다.\n" +
                "2. 제1항의 규정에도 불구하고 분쟁이 해결되지 않을 경우 관할법원을 통하여 해결합니다.\n" +
                "\n" +
                "부칙\n" +
                "본 약관은 20XX년 XX월 XX일부터 시행합니다.";
    }

    private String generateAuthCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private void sendEmail(String to, String authCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[HUSK] 회원가입을 위한 인증코드 발송");
        message.setText("인증코드: " + authCode);
        mailSender.send(message);
    }
}