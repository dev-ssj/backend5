package com.example.backendproject.stompwebsocket.handler;

import java.security.Principal;

/*
* WebSocket 세션에 사용자 이름을 연결하기 위한 Pricipal 구현체 
*/
public class StompPricipal implements Principal {   //Pricipal : 사용자 인증 객체

    //사용자 고유 식별자
    private final String name;

    public StompPricipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
