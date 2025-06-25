package com.example.backendproject.security.jwt;

import com.example.backendproject.security.core.CustomUserDetails;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    /** JWT 토큰 생성 및 추출 검증 하는 클래스 **/
    
    private final SecretKey secretKey;  //토큰 만들때 서명하는 키
    
    //로그인이 완료되면 호출되는 메서드
    //현재 로그인이 완료된 사용자 정보를 기반으로 access, refresh token 발급
    public String generatedToken(Authentication authentication, Long expiratingMillie){

        //현재 로그인한 사용자의 정보를 꺼냄
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        //토큰 만료시간 생성(밀리초 단위까지)
        Date expiryDate = new Date(new Date().getTime() + expiratingMillie);

        //토큰 발급될 때 넣을 정보들
        Claims claims = Jwts.claims();
        claims.put("user-id", customUserDetails.getId());
        claims.put("username",customUserDetails.getUsername());

        //토큰이 발급되는 과정(user-id와 username도 포함)
        return Jwts.builder()
                .setSubject(customUserDetails.getUsername())    //이 JWT 토큰의 주제를 지정
                .setClaims(claims)  //payload
                .setIssuedAt(new Date())    //토큰 발급 시간
                .setExpiration(expiryDate)  //토큰 만료 시간
                .signWith(secretKey, SignatureAlgorithm.HS512)  //시크릿 키와 알고리즘을 이용해서 암호화하여 서명
                .compact();  //<- 에서 저장한 정보들을 최종적으로 문자열로 만들어주는 메서드
    }

    //parseClaimsJws : 서명이 있는 토큰
    //parseClaimsJwt : 서명이 없는 토큰
    //JWT 토큰에서 사용자 ID를 추출하는 메서드
    public Long getUserIdFromToken(String token){
        return Jwts
                .parserBuilder()            //jwt 토큰을 해석하겠다고 선언
                .setSigningKey(secretKey)   //토큰을 검증하기 위해 비밀키 사용
                .build()                    //해석할 준비 완료
                .parseClaimsJws(token)  //전달 받은 토큰을 파싱
                .getBody()                      //파싱한 토큰의 payload 부분을 꺼내서
                .get("user-id", Long.class);    //user-id를 반환
    }

    //토큰이 유효한지 검증하는 메서드
    public Boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch (MalformedJwtException e){
            //토큰 형식이 잘못되었을 때
            return false;
        }catch (ExpiredJwtException e){
            //토큰이 만료가 되었을 때
            return false;
        }catch (UnsupportedJwtException e){
            //지원하지 않는 토큰일 때
            return false;
        }catch (IllegalArgumentException e){
            //토큰 문자열이 비어있거나 이상할 때
            return false;
        }catch (JwtException e){
            //기타 예외
            return false;
        }
    }
}
