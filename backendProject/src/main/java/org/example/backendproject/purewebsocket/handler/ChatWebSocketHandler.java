package org.example.backendproject.purewebsocket.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.backendproject.purewebsocket.dto.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

    //sessions : 현재 웹소켓에 연결된 클라이언트들(WebSocketSession)을 관리하는 Set컬렉션
    //           이 컬렉션에서 WebSocketSession 객체를 추가하거나 제거하면서 접속적인 유저를 추적
    //HashSet을 사용하는 이유 : Set은 중복을 허용하지 않음 -> 같은 클라이언트 세션이 여러번 저장되는 것 방지 가능
    //Collections.synchronizedSet() : 기본적으로 HashSet은 스레드에 안전하지 않음. 웹소켓 서버는 멀티스레드 환경에서 작동하므로, 여러 사용자가 동시에 접속하거나 연결을 끊는 경우가 발생 -> 동시성 문제
    //이 때 synchronizedSet()를 사용해 동기화된 안전한 Set을 만들어 동시성 문제를 예방한다.
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    //서버에서 메시지를 보내면 JSON형식이다. 그것을 자바 객체로 변환해주고, 다시 자바객체를 JSON 문자열로 바꿔주는 역할
    private final ObjectMapper objectMapper =new ObjectMapper();

    //방과 방 안에 있는 세션을 관리하는 객체
    private final Map<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();

    //ctrl + o : 오버라이드 단축키
    //클라이언트가 웹소켓 서버에 접속했을 때 호출
    //WebSocketSession session : 서버에 접속한 id.
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        //서버에 접속한 id를 sessions에 넣어줌(관리하기 위해)
        sessions.add(session);

        log.info("접속된 클라이언트 세션 ID = " + session.getId());
    }

    //클라이언트가 보낸 메세지를 서버가 받았을 때 호출(즉, 사용자가 메시지를 보냈을 떄)
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);

        // JSON 문자열 -> 자바객체 변환
        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);


        String roomId = chatMessage.getRoomId();    //클라이언트에게 받은 메세지에서 roomID를 추출
        if(!rooms.containsKey(roomId)){ //방을 관리하는 객체에 현재 세션이 들어가는 방이 있는지 확인
            rooms.put(roomId, ConcurrentHashMap.newKeySet());   //없으면 새로운 방을 생성
        }
    
        //방이 있으면 기존의 방에 session만 추가
        rooms.get(roomId).add(session);


        for(WebSocketSession s: rooms.get(roomId)){
            if(s.isOpen()){
                //자바 객체 -> JSON 문자열
                s.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));

                log.info("전송된 메시지 = " + chatMessage.getMessage());
            }
        }
    }

    //클라이언트의 연결이 끊겼을 때 호출
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        
        //연결이 끊기면 session 삭제
        sessions.remove(session);
        
        //연결이 해제되면 소속되어 있는 방에서 제거
        for(Set<WebSocketSession> room : rooms.values()){
            room.remove(session);
        }
    }
}
