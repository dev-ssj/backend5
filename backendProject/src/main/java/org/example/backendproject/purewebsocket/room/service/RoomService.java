package org.example.backendproject.purewebsocket.room.service;

import org.example.backendproject.purewebsocket.room.entity.ChatRoom;
import org.example.backendproject.purewebsocket.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    public ChatRoom createRoom(String roonId){

        return roomRepository.findByRoomId(roonId)
                .orElseGet(()->{
                    ChatRoom chatRoom = new ChatRoom();
                    chatRoom.setRoomId(roonId);
                    return roomRepository.save(chatRoom);
                });
    }

    public List<ChatRoom> findAllRooms() {
        return roomRepository.findAll();
    }


}
