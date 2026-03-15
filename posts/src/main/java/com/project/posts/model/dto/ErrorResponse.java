package com.project.posts.model.dto;

public record ErrorResponse(
        String message,
        long timestamp,
        int status
) {}
