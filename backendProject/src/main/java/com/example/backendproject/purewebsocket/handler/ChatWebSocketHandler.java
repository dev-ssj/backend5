package com.example.backendproject.purewebsocket.handler;

import com.example.backendproject.purewebsocket.dto.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.lang.runtime.ObjectMethods;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    //sessions : 현재 웹소켓에 연결된 클라이언트들(WebSocketSession)을 관리하는 Set컬렉션
    //           이 컬렉션에서 WebSocketSession 객체를 추가하거나 제거하면서 접속중인 유저를 추적
    //HashSet을 사용하는 이유 : Set은 중복을 허용하지 않음 -> 같은 클라이언트 세션이 여러번 저장되는 것 방지 가능
    //Collections.synchronizedSet() : 기본적으로 HashSet은 스레드에 안전하지 않음. 웹소켓 서버는 멀티스레드 환경에서 작동하므로,
    // 여러 사용자가 동시에 접속하거나 연결을 끊는 경우가 발생 -> 동시성 문제
    //이 때 synchronizedSet()를 사용해 동기화된 안전한 Set을 만들어 동시성 문제를 예방한다.
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    //서버에서 메시지를 보내면 JSON형식이다. 그것을 자바 객체로 변환해주고, 다시 자바객체를 JSON 문자열로 바꿔주는 역할
    private final ObjectMapper objectMapper =new ObjectMapper();

    //방과 방 안에 있는 세션을 관리하는 객체
    private final Map<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();

    /*
    *  클라이언트가 웹소켓 서버에 접속했을 때(연결되면) 호출된다.
    */
    @Override       //WebSocketSession session : 서버에 접속한 id.
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        //서버에 접속한 클라이언트의 고유 id를 sessions에 넣어줌(관리하기 위해)
        sessions.add(session);

        System.out.println("접속된 클라이언트 세션 ID = " + session.getId());
    }


    /*
    * 클라이언트가 보낸 메시지를 서버가 받았을 때 호출(즉, 사용자가 메시지를 보냈을 때)
    */
    @Override   //session : 메시지를 보낸 클라이언트의 세션정보, message : 클라이언트가 보낸 텍스트 메시지(JSON 문자열)
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);

        // message.getPayload()로 JSON 문자열 추출 후 -> objectMapper.readValue()f를 통해 자바객체로 변환
        // 변환된 chatMessage객체는 .getMessage(), .getFrom(), .getRoomId() 등의 메서드로 값 추출 가능
        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);

        String roomId = chatMessage.getRoomId();    //클라이언트에게 받은 메세지에서 roomID를 추출
        if(!rooms.containsKey(roomId)){     //roomId키가 존재하지 않으면(처음 만들어진 방이면)
            rooms.put(roomId, ConcurrentHashMap.newKeySet());   //안전한 세션 Set을 만들어 저장(새로운 채팅방 생성)
        }   //ConcurrentHashMap을 사용하는 이유 : 웹소켓 서버는 멀티스레드 환경이므로 동시성 문제 발생 방지를 위해서.
    
        //roomId에 해당하는 방에 현재 사용자의 session 추가
        //사용자가 메시지를 보내는 순간 자동 입장됨
        rooms.get(roomId).add(session);    

        //특정 채팅방(roomId)에 참여중인 모든 세션(사용자)을 꺼내서 메시지를 전송하기 위한 반복문
        for(WebSocketSession s: rooms.get(roomId)){
            //세션이 닫히지 않았다면 클라이언트에게 메시지를 보냄
            if(s.isOpen()){
                //자바 객체 -> JSON 문자열로 변환 후 전송.
                s.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));

                System.out.println("전송된 메시지 = " + chatMessage.getMessage());
            }
        }
    }

    /*
    * 클라이언트의 연결이 끊겼을 때 호출
    */
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
