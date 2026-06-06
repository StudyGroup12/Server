package com.studygroup.domain.auth.service;

import com.studygroup.domain.auth.dto.LoginRequest;
import com.studygroup.domain.auth.dto.MemberResponse;
import com.studygroup.domain.auth.dto.SignupRequest;
import com.studygroup.domain.auth.dto.TokenResponse;
import com.studygroup.domain.auth.entity.Member;
import com.studygroup.domain.auth.repository.MemberRepository;
import com.studygroup.global.exception.CustomException;
import com.studygroup.global.jwt.JwtProvider;
import com.studygroup.global.response.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public void signup(SignupRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();
        memberRepository.save(member);
    }

    public TokenResponse login(LoginRequest request, HttpServletResponse response) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtProvider.createAccessToken(member.getId());
        String refreshToken = jwtProvider.createRefreshToken(member.getId());

        // Refresh Token은 HttpOnly 쿠키로 전달
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .sameSite("Strict")
                .path("/api/auth/refresh")
                .maxAge(604800)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return new TokenResponse(accessToken);
    }

    public TokenResponse refresh(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        Long userId = jwtProvider.getUserId(refreshToken);
        return new TokenResponse(jwtProvider.createAccessToken(userId));
    }

    public MemberResponse getMyPage(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        return MemberResponse.from(member);
    }
}
