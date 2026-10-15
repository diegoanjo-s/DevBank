package com.devbank.dto;

import java.time.LocalDateTime;

public class HeloResponse {
    public record HelloResponse(String message, String application, LocalDateTime timestamp){

    }
}
