package com.sparta.team6.momo.dto;

import lombok.*;
import com.sparta.team6.momo.model.MessageType;

@Data
@Builder
public class ChatDto {
    private Long planId;
    private MessageType type;
    private String sender;
    private String content;
}
