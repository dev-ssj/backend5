package com.example.backendproject.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
public class jwtKey {
    @Value("${jwt.secretKey}")  //application.proierties에 설정
    private String secretKey;

    //서명키를 만들어서 반환하는 메서드
    @Bean
    public SecretKey secretKey(){
        byte[] keyBytes = secretKey.getBytes();     //설정 파일에서 불러온 키 값을 바이트 배열로 변환(HMAC 서명을 위해 필요)
        return new SecretKeySpec(keyBytes,"HmacSHA512");    //바이트 배열을 HmacSHA256용 Security 비밀키 객체로 생성
                                                                        //JWT 서명에서 사용할 javax.crypto.SecretKey타입으로 변환됨
        
    }

}
