package com.example.backendproject.security.config;

import com.example.backendproject.security.jwt.JwtTokenFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration              //설정 클래스 등록
@EnableWebSecurity          //스프링 시큐리티 활성화
@RequiredArgsConstructor    //생성자 자동생성
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    //스프링 시큐리티에서 어떤 순서로 어떤 보안 구칙의 필터를 가질지 정의하는 클래스
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)  //CSRF 비활성화. REST API라면 대부분 CSRF 필요 없음
                //.permitAll() : 누구나 접근 가능(정적 자원, 로그인 요청 등)
                //.authenticated() : 인증된 사용자만 접근 가능
                .authorizeHttpRequests((auth) -> auth.requestMatchers("/","/index.html","/*.html","/favicon.ico",
                                        "/js/**", "/css/**",
                                        "/images/**", "/fetchWithAuth.js","/.well-known/**").permitAll() //인증 필요없이 모두 허용하는 경로
                                .requestMatchers("/api/auth/**","/api/comments/**").permitAll()

                                .requestMatchers("/api/user/**","/boards","/boards/**").authenticated() // 인증이 필요한 경로
                        
                //인증 실패 시 예외처리
                ).exceptionHandling(e -> e
                        //인증이 안된 사용자가 접근하려고 할때
                        .authenticationEntryPoint((request, response, authException) -> {
                                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                                })
                        //인증은 되었지만 권한이 없을때
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                        })
                )
                //스프링 시큐리티에서 세션관리 정책을 설정하는부분
                // 기본적으로 스프링시큐리티는 세션을 생성함.
                //하지만 JWT 기반 인증은 세션 상태를 저장하지 않는 무상태 방식
                //인증 정보를 세션에 저장하지 않고, 매 요청마다 토큰으로 인증
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                //매 요청마다 적용할 필터
                //UsernamePasswordAuthenticationFilter 이전에 jwtTokenFilter를 실행하도록 필터 체인에 등록
                //즉 , 스프링 시큐리티가 내부적으로 인증을 시도하기 전에 먼저 우리가 만든 JWT 검사를 하도록 한다.
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();   //위 명시한 설정들을 적용
    }
    
    //회원가입시에 비밀번호를 암호화해주는 메서드
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /*
    * application.properties → jwtKey (SecretKey 생성)
    * JwtTokenProvider (토큰 생성/검증)
    * CustomUserDetails (User 감싸기)
    * CustomUserDetailService (DB에서 User 찾기)
    * JwtTokenFilter (요청마다 토큰 검사 후 인증 객체 등록)
    * SecurityConfig (필터 체인과 인증 규칙 정의)
    * Role (유저 권한 정의)
    */
}