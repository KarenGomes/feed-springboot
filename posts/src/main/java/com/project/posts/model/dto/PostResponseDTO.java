package com.project.posts.model.dto;

import java.math.BigDecimal;

public record PostResponseDTO(
        Long id,
        String username,
        String title,
        String content,
        String imagePath,
        BigDecimal latitude,
        BigDecimal longitude
) {}
