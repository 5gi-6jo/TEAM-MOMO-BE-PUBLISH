package com.sparta.team6.momo.socket;

import com.sparta.team6.momo.dto.ChatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import com.sparta.team6.momo.model.MessageType;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> attributes = headerAccessor.getSessionAttributes();
        String nickname = null; Long planId = null;

        if (attributes != null) {
            nickname = (String) attributes.get("nickname");
            planId = (Long) attributes.get("planId");
        }

        if (nickname != null && planId != null) {
            log.info("User Disconnected : " + nickname);

            ChatDto chatDto = ChatDto.builder()
                    .planId(planId)
                    .type(MessageType.LEAVE)
                    .sender(nickname)
                    .build();

            messagingTemplate.convertAndSend("/topic/chat" + planId, chatDto);
        }
    }
}
