package com.graphy.backend.domain.member.service;

import com.graphy.backend.domain.member.entity.Member;
import com.graphy.backend.domain.member.repository.MemberRepository;
import com.graphy.backend.global.config.jwt.TokenProvider;
import com.graphy.backend.global.config.jwt.dto.TokenInfo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.graphy.backend.domain.member.dto.MemberDto.*;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Service
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;

    public void join(CreateMemberRequest request) {
        /**
         * TODO
         * 이메일 중복 체크
         */
        String encodedPassword = encoder.encode(request.getPassword());
        Member member = request.toEntity(encodedPassword);
        memberRepository.save(member);
    }

    public TokenInfo login(LoginMemberRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        System.out.println(1);

        TokenInfo token = tokenProvider.generateTokenDto(authentication);

        /**
         * TODO
         * Refresh Token 저장 설정
         */
        return token;
    }


}
