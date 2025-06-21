package com.example.backendproject.stompwebsocket.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
*   메시지 DTO
*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String message; //채팅 내용
    private String from;    //보낸사람

    private String to;  //귓속말을 받을 사람
    private String roomId;  //방 id
}
