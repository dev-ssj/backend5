package com.example.backendproject.purewebsocket.config;

import com.example.backendproject.purewebsocket.handler.ChatWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/*
*   WebSocket연결을 설정하는 Spring 설정 클래스 
*/
//@Configuration    //스프링 설정 클래스임을 나타냄
//@EnableWebSocket ->STOMP실행을 위해 주석처리한것.    //웹소켓 서버 활성화 어노테이션
public class WebSocketConfig implements WebSocketConfigurer {   //웹소켓 핸들러 등록을 위한 인터페이스 구현

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        //ws-chat 앤드포인트로 요청을 보낼 수 있는지 결정하는 보안 정책 설정
        //ws://서버주소/ws-chat로 클라이언트가 접속하면 ChatWebSocketHandler가 작동 -> api를 매핑하는 것과 비슷한 역할
        //addHandler()를 통해 등록된 핸들러는 HTTP가 아닌 웹 소켓 요청을 처리하므로 HTTP GET요청이 들어오면 404에러 발생
        registry.addHandler(new ChatWebSocketHandler(),"/ws-chat")
                .setAllowedOriginPatterns("*");
                //WebScoket은 브라우저 정책보안(CORS)에 영향을 받는다.
                //"*" : 모든 외부 접근 허용 설정
    }
}
