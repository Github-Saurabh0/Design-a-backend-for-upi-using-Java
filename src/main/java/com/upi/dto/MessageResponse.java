package com.upi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    private String message;
    private boolean success;
    private Object data;

    public MessageResponse(String message) {
        this.message = message;
        this.success = true;
    }

    public MessageResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
}