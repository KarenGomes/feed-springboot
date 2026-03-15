package com.project.posts.model.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record PostRequestDTO(
        @NotBlank(message = "O título é obrigatório") String title,
        @NotBlank(message = "O conteúdo é obrigatório") String content,
        String imagePath,
        BigDecimal latitude,
        BigDecimal longitude
) {}
