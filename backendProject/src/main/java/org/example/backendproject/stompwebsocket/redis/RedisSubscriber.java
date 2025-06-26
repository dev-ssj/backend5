package org.example.backendproject.stompwebsocket.redis;
import org.example.backendproject.stompwebsocket.dto.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/*
* 메시지 수신자 
*/
@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener{
    private final SimpMessagingTemplate simpMessagingTemplate;  //Spring WebSocket을 통해 메시지를 전송하는 템플릿
    private ObjectMapper objectMapper = new ObjectMapper(); //JSON 문자열 <-> 자바 객체

    @Override
    public void onMessage(Message message, byte[] pattern) {

        try {
            //Redis는 메시지를 byte[]로 전송한다. byte[] -> 문자열(JSON텍스트)로 변환
            String msgBody = new String(message.getBody());
            //JSON 문자열을 objectMapper로 자바객체로 변환
            ChatMessage chatMessage = objectMapper.readValue(msgBody, ChatMessage.class);

            //getTo()가 있으면 귓속말, 아니면 일반채팅
            if (chatMessage.getTo() != null && !chatMessage.getTo().isEmpty()) {
                // 귓속말 convertAndSendToUser()는 특정사용자에게 메시지를 보냄. /user/{id}/queue/priavete 경로 구독해야함
                simpMessagingTemplate.convertAndSendToUser(chatMessage.getTo(), "/queue/private", chatMessage);
            } else {
                // 일반 메시지 /topic/room.1 형태로 브로드캐스트
                simpMessagingTemplate.convertAndSend("/topic/room." + chatMessage.getRoomId(), chatMessage);


            }
        }
        catch (Exception e) {
        }
    }
}