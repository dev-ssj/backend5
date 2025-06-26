package org.example.backendproject.purewebsocket.dto;


import lombok.Getter;

@Getter
public class ChatMessage {

    private String roomId; //방 ID
    private String message; //메시지
    private String from;    //발신자
}
