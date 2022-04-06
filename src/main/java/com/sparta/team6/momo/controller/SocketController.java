package com.sparta.team6.momo.controller;

import com.sparta.team6.momo.dto.ChatDto;
import com.sparta.team6.momo.dto.ChatEnterDto;
import com.sparta.team6.momo.dto.EnterDto;
import com.sparta.team6.momo.dto.MapDto;
import com.sparta.team6.momo.service.SocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SocketController {

    private final String REDIS_CHAT_KEY = "CHATS";
    private final String REDIS_CHAT_PREFIX = "CHAT";
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final SocketService socketService;
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, List<ChatDto>> hashOperations;

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    @MessageMapping("/enter") // maps/enter
    public void enter(@Payload EnterDto enterDto, SimpMessageHeaderAccessor headerAccessor) {
        ChatEnterDto chatDto = ChatEnterDto.from(enterDto);
        chatDto.setContent(chatDto.getSender() + "님이 입장하셨습니다");

        List<ChatDto> chats = hashOperations.get(REDIS_CHAT_KEY, REDIS_CHAT_PREFIX + enterDto.getPlanId());
        chatDto.setChats(chats);

//        Map<String, Object> attributes = headerAccessor.getSessionAttributes();
//        if (attributes != null) {
//            attributes.put("nickname", chatDto.getSender());
//            attributes.put("planId", chatDto.getPlanId());
//        }
        MapDto mapDto = MapDto.from(enterDto);
        socketService.setDestination(enterDto.getPlanId(), mapDto);

        simpMessagingTemplate.convertAndSend("/topic/map/" + mapDto.getPlanId(), mapDto);
        simpMessagingTemplate.convertAndSend("/topic/chat/" + chatDto.getPlanId(), chatDto);
    }

    @MessageMapping("/map.send") // maps/map.send
    public void sendMap(@Payload MapDto mapDto) {
        simpMessagingTemplate.convertAndSend("/topic/map/" + mapDto.getPlanId(), mapDto);
    }

    @MessageMapping("/chat.send") // maps/chat.send
    public void sendChat(@Payload ChatDto chatDto) {

        List<ChatDto> chats = hashOperations.get(REDIS_CHAT_KEY, REDIS_CHAT_PREFIX + chatDto.getPlanId());
        if (chats == null) {
            chats = new ArrayList<>();
        }
        chats.add(chatDto);
        hashOperations.put(REDIS_CHAT_KEY, REDIS_CHAT_PREFIX + chatDto.getPlanId(), chats);

        simpMessagingTemplate.convertAndSend("/topic/chat/" + chatDto.getPlanId(), chatDto);
    }
}
