package com.example.backendproject.stompwebsocket.controller;

import com.example.backendproject.stompwebsocket.dto.ChatMessage;
import com.example.backendproject.stompwebsocket.gpt.GPTService;
import com.example.backendproject.stompwebsocket.redis.RedisPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

//STOMP는 이게 끝.
@Controller
@RequiredArgsConstructor
public class ChatController {

    //서버가 클라이언트에게 수동으로 메세지를 보낼 수 있도록 하는 클래스
    private final SimpMessagingTemplate template;

    //환경 변수를 받아서 화면에 출력
    @Value("${PROJECT_NAME:web Server}")
    private String instansName;
    private final RedisPublisher redisPublisher;
    private ObjectMapper objectMapper  = new ObjectMapper();

    //GPT 호출 서비스
    private final GPTService gptService;

    /*GPT 응답 처리용 엔드포인트*/
    //클라이언트가 /app/gpt로 메시지를 보냄
    @MessageMapping("/gpt")
    public void sendMessageGPT(ChatMessage message) throws Exception{

        template.convertAndSend("/topic/gpt",message);//내가 보낸 메시지 출력

        //사용자가 보낸 메시지를 받음. gpt 목적지 반환
        String getResponse = gptService.getMessage(message.getMessage());

        ChatMessage chatMessage = new ChatMessage("난 GPT(젠킨스)", getResponse);

        template.convertAndSend("/topic/gpt", chatMessage);
    }

    @MessageMapping("/chat.sendMessage")
    public void sendmessage(ChatMessage message) throws JsonProcessingException {

        message.setMessage(instansName+" "+message.getMessage());

        String channel = null;
        String msg = null;

        if (message.getTo() != null && !message.getTo().isEmpty()) {
            // 귓속말
            //내 아이디로 귓속말경로를 활성화 함
            channel = "private."+message.getRoomId();
            msg = objectMapper.writeValueAsString(message);

        } else {
            // 일반 메시지
            channel = "room."+message.getRoomId();
            msg = objectMapper.writeValueAsString(message);
        }
        redisPublisher.publish(channel,msg);
    }
}