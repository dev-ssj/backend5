package com.example.backendproject.stompwebsocket.config;

import com.example.backendproject.stompwebsocket.handler.CustomHandshakeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/*
*   STOMP WebSocket 설정을 담당하는 클래스 
*/
@Configuration
@EnableWebSocketMessageBroker   //STOMP 메시지 브로커 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override   //엔드포인트 등록
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")    //클라이언트가 연결할 WebSocket 엔드포인트를 /ws-chat으로 지정
                .setHandshakeHandler(new CustomHandshakeHandler()) //연결 시 사용자 닉네임 등록
                .setAllowedOriginPatterns("*"); //모든 접근 허용
    }

    @Override   //메시지 브로커 설정
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //Prefix <- 메세지의 목적지를 구분하기 위한 접두어
        /** 구독용 Profix **/
        // /topic : 일반 채팅 받을 접두어
        // /queue : 귓속말 받을 접두어

        //구독용 경로 서버 -> 클라이언트(메시지를 분배한다) /topic : 공개 채팅방, /queue : 귓속말
        registry.enableSimpleBroker("/topic", "/queue");
        
        //전송용 경로 클라이언트 -> 서버 (사용자가 메시지를 보낸다) 접두어
        // /app/chat.sendMessage
        registry.setApplicationDestinationPrefixes("/app");

        /** 서버가 특정 사용자에게 메시지를 보낼 때(귓속말) 클라이언트가 구독할 경로 접두어 **/
        registry.setUserDestinationPrefix("/user"); //user/{to}/queue/private
    }
    
    
}
