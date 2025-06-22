package com.example.backendproject.stompwebsocket.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/*
* Redis 메시지 리스너 설정 
*/
@Profile("!test") //테스트 시 redis는 무시한다.
@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisSubscriber redisSubscriber;  //Redis에서 발행된 메시지를 수신하는 클래스

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory redisConnectionFactory) {
        //RedisMessageListenerContainer : Redis Pub/Sub를 구독할 리스너 컨테이너
        RedisMessageListenerContainer Container = new RedisMessageListenerContainer();
        //RedisConnectionFactory : redis에 연결하기 위핸 커넥션 팩토리 주입
        Container.setConnectionFactory(redisConnectionFactory);

        //room.*:방에 대한 일반 채팅 메시지 토픽
        Container.addMessageListener(new MessageListenerAdapter(redisSubscriber),new PatternTopic("room.*"));
        //private.* : 귓속말 메시지 토픽
        Container.addMessageListener(new MessageListenerAdapter(redisSubscriber), new PatternTopic("private.*")); //귓속말
        //MessageListenerAdapter(redisSubscriber) : RedisSubscriber 객체를 Redis 메시지 리스너로 래핑
        return Container;   //리스너 컨테이너를 빈으로 등록하여 Redis 메시지를 수신 대기

    }
}