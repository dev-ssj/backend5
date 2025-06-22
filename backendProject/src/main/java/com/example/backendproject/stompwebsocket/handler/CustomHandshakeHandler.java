package com.example.backendproject.stompwebsocket.handler;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

//연결된 요청 url에서 사용자를 식별
public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {

        String nickname = getNickname(request.getURI().getQuery());
        return new StompPricipal(nickname);
    }
    //요청이 들어오면 닉네임을 추출해서 닉네임이 없으면 닉네임 없음 출력, 있으면 사용자 추출하는 핸들러
    private String getNickname(String query){
        if (query == null || !query.contains("nickname=")){
            return "닉네임없음";
        }
        else {
            return query.split("nickname=")[1];
        }
    }

}
