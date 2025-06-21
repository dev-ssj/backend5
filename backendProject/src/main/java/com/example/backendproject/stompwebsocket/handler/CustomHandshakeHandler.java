package com.example.backendproject.stompwebsocket.handler;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

//웹소켓이 연결된 요청 url에서 사용자를 식별해주는 핸드셰이크 처리기
public class CustomHandshakeHandler extends DefaultHandshakeHandler {
    @Override
                //웹소켓 연결 요청이 들어왔을 때 실행됨 -> 사용자를 식별하여 Pricipal 객체 반환
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {

        //URL쿼리 스트링에서 nicname=값 을 파싱한다.
        String nickname = getNickname(request.getURI().getQuery());
        //추출한 닉네임을 사용자 정보로 등록
        return new StompPricipal(nickname);
    }

    //요청이 들어오면 닉네임을 추출해서 닉네임이 없으면 닉네임 없음 출력, 있으면 사용자 추출하는 핸들러
    private String getNickname(String query){
        if(query == null || !query.contains("nickname=")){
            return "닉네임 없음";
        }
        else{   //예시)/ws-chat?nickname=홍길동 -> "홍길동"이 Pricipal로 등록됨
            return query.split("nickname=")[1];
        }
    }
}

