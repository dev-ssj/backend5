package com.example.backendproject.stompwebsocket.config;

import com.example.backendproject.stompwebsocket.handler.CustomHandshakeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setHandshakeHandler(new CustomHandshakeHandler()) //귓속말 가능하게 해줌
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //Prefix <- 메세지의 목적지를 구분하기 위한 접두고

        /** 구독용 Profix **/

        // /topic : 일반 채팅 받을 접두어
        // /queue : 귓속말 받을 접두어

        //구독용 경로 서버 -> 클라이언트(메시지를 분배한다)
        registry.enableSimpleBroker("/topic", "/queue");
        
        //전송용 경로 클라이언트 -> 서버 (메시지가 들어온다)
        registry.setApplicationDestinationPrefixes("/app");
        
        // /user 특정 사용자에게 메시지를 보낼 접두어
        /** 서버가 특정 사용자에게 메시지를 보낼 떄, 클라이언트가 구독할 경로 접두어 **/
        registry.setUserDestinationPrefix("/user"); //서버 -> 특정사용자
    }
    
    
}
