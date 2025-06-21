package com.example.backendproject.stompwebsocket.controller;

import com.example.backendproject.stompwebsocket.dto.ChatMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

//STOMP는 이게 끝.
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate template;
    
    //동적으로 방 생성 가능
    @Value("${PROJECT_NAME:web server}")
    private String instansName;

    @MessageMapping("/chat.sendMessage")
    public void sendmessage(ChatMessage message){

        message.setMessage(instansName+ " "+message.getMessage());

        if(message.getTo() != null && !message.getTo().isEmpty()){
            //귓속말
            //내 아이디로 귓속말 경로를 활성화 함
            template.convertAndSendToUser(message.getTo(), "/queue/private",message);
        }else {
            //일반 메시지
            //message에서 roomId를 추출해서 해당 roomId를 구독하고 있는 클라이언트에게 메시지를 전달
            template.convertAndSend("/topic/"+message.getRoomId(),message);
        }
    }
}
