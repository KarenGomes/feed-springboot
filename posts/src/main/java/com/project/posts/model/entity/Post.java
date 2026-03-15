package com.project.posts.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.math.BigDecimal;

@Entity
@Table(name = "posts")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE) // Implementa o ON DELETE CASCADE no banco
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_path", nullable = true)
    private String imagePath;

    // Precisão 10, escala 8 (ex: -90.12345678)
    @Column(precision = 10, scale = 8, nullable = true)
    private BigDecimal latitude;

    // Precisão 11, escala 8 (ex: -180.12345678)
    @Column(precision = 11, scale = 8, nullable = true)
    private BigDecimal longitude;
}