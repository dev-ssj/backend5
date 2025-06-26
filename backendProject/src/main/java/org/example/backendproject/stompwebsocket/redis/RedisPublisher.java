package org.example.backendproject.stompwebsocket.redis;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/*
*   메시지 발행자 
*/
@RequiredArgsConstructor
@Component
public class RedisPublisher {

    /** 메세지를 발행하는 클래스 **/

    private final StringRedisTemplate stringRedisTemplate;  //문자열 기반 Redis 작업을 수행하는 템플릿

    ///  stomp -> pub -> sub -> stomp
    public void publish(String channel,String message){ //chnnel : 발행할 redis채널명, message : 발행할 메시지

        stringRedisTemplate.convertAndSend(channel,message);
    }
}
