package com.example.backendproject.security.jwt;

import com.example.backendproject.security.core.CustomUserDetailService;
import io.netty.util.internal.StringUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//http응답을 가로채서 검증을 하는 클래스
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    //JwtTokenFilter : 모든 HTTP 요청을 가로채서 JWT 토큰을 검사하는 필터 역할
    //OncePerRequestFilter는 한 요청당 딱 한번만 실행되는 필터 역할

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailService customUserDetailService;


    //HTTP 매 요청마다 호출
    @Override
    protected void doFilterInternal(HttpServletRequest request,     //http 요청
                                    HttpServletResponse response,   //http 응답
                                    FilterChain filterChain) throws ServletException, IOException {

        //요청 헤더에서 토큰 추출
        String accessToken = getTokenFromRequest(request);
        if(accessToken != null && jwtTokenProvider.validateToken(accessToken)){


            UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(accessToken);
            //토큰에서 사용자f를 꺼내서 사용자 인증 객체에 담는다.

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            //http요청으로부터 부가정보(ip, 세션 등)을 추출해서 사용자 인증 객체에 넣어줌
            
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            //토큰에서 사용자 인증정보를 조회해서 인증정보를 현재 스레드에 인증된 사용자로 등록

            String url = request.getRequestURI().toString();
            String method = request.getMethod(); //"GET", "POST", "PUT"...
            System.out.println("현재 들어온 HTTP 요청 = " + url);
        }
        /**
            [다른 필터들의 정보]
            CharacterEncodingFilter: 문자 인코딩 처리
            CorsFilter: CORS 정책 처리
            CsrfFilter: CSRF 보안 처리
            JWTTokenFilter: JWT 토큰 처리(핵심)
            SecurityContextFilter: 인증/인가 정보 저장
            ExceptionFilter: 예외 처리
         **/

        filterChain.doFilter(request,response); //JwtTokenFilter를 거치고 다음 필터로 넘어감
    }

    //HTTP 요청 헤더에서 토큰 추출하는 메서드
    public String getTokenFromRequest(HttpServletRequest request){
        String token = null;

        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            token = bearerToken.substring(7);
        }

        return token;
    }

    //http 요청에서 사용자 정보들 담는 객체
    private UsernamePasswordAuthenticationToken getAuthentication(String token){
        
        //JWT 토큰에서 userid 추출
        Long userid = jwtTokenProvider.getUserIdFromToken(token);

        //위 추출한 id를 DB에서 사용자 정보 조회
        UserDetails userDetails = customUserDetailService.loadUserById(userid);

        return new UsernamePasswordAuthenticationToken(
                userDetails,   //사용자 정보
                null,                   //credential을 담는 부분 -> 이미 인증 되었기 떄문에 null
                userDetails.getAuthorities()    //사용자의 권한
        );

    }
}
