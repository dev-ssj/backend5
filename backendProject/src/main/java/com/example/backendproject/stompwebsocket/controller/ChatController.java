package com.example.backendproject.stompwebsocket.controller;

import com.example.backendproject.stompwebsocket.dto.ChatMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/*
*   STOMP 메시지를 처리하는 WebSocket 컨트롤러
*/
//STOMP 설정은 이거만 해주면 된다!
@Controller
@RequiredArgsConstructor    //final 필드 생성자 주입
public class ChatController {

    //메시지를 특정 유저나 구독자에게 전송하는 스프링 제공 유틸리티
    private final SimpMessagingTemplate template;
    
    //동적으로 방 생성 가능
    //.yml 또는 .env에 정의된 환경변수의 PROJECT_NAME의 값을 가져온다. 없으면 기본값인 web server 사용
    @Value("${PROJECT_NAME:web server}")
    private String instansName;

    //클라이언트가 /app/chat.sendMessage로 메시지를 보내면 이 메서드가 호출된다.
    @MessageMapping("/chat.sendMessage")
    public void sendmessage(ChatMessage message){

        //메시지에 서버 인스턴스명을 붙임 -> 로드밸런싱 환경
        message.setMessage(instansName+ " "+message.getMessage());

        //귓속말인지 판별하는 if문. to가 null이 아니고 비어있지 않으면 귓속말
        if(message.getTo() != null && !message.getTo().isEmpty()){
            //귓속말
            //특정사용자(message.getTo())에게 /user/{to}/queue/private 경로로 귓속말 보냄. 접두어는 WebSocketConfig에서 지정
            template.convertAndSendToUser(message.getTo(), "/queue/private",message);
        }else {
            //일반 메시지
            //message에서 roomId를 추출해서 해당 roomId를 구독하고 있는 클라이언트에게 /topic/{roomId} 경로로 메시지를 전달
            template.convertAndSend("/topic/"+message.getRoomId(),message);
        }
    }
}
