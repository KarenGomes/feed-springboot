package com.project.posts.service;

import com.project.posts.model.dto.PostRequestDTO;
import com.project.posts.model.dto.PostResponseDTO;
import com.project.posts.model.entity.Post;
import com.project.posts.model.entity.User;
import com.project.posts.repository.PostRepository;
import com.project.posts.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Value("${UPLOAD_DIR}")
    private String uploadDir;

    // Método para buscar Posts.
    @Transactional(readOnly = true)
    public List<PostResponseDTO> findAll() {
        return postRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Transactional
    public PostResponseDTO createPost(PostRequestDTO request, MultipartFile file) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Post post = new Post();
        post.setTitle(request.title());
        post.setContent(request.content());
        post.setLatitude(request.latitude());
        post.setLongitude(request.longitude());
        post.setUser(author);

        // Lógica de Upload
        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file);
            post.setImagePath("/uploads/" + fileName); // Caminho relativo para o front-end
        }

        Post savedPost = postRepository.save(post);
        return mapToResponseDTO(savedPost);
    }

    private String saveFile(MultipartFile file) {
        try {
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar imagem.");
        }
    }

    @Transactional
    public PostResponseDTO updatePost(Long id, PostRequestDTO request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post não encontrado."));

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!post.getUser().getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("Você não tem permissão para editar este post.");
        }

        // Só atualiza se o campo não for nulo no JSON enviado
        if (request.title() != null && !request.title().isBlank()) {
            post.setTitle(request.title());
        }

        if (request.content() != null && !request.content().isBlank()) {
            post.setContent(request.content());
        }

        if (request.imagePath() != null) {
            post.setImagePath(request.imagePath());
        }

        if (request.latitude() != null) {
            post.setLatitude(request.latitude());
        }

        if (request.longitude() != null) {
            post.setLongitude(request.longitude());
        }

        return mapToResponseDTO(postRepository.save(post));
    }

    // Método auxiliar para transformar Entidade em DTO de Resposta
    private PostResponseDTO mapToResponseDTO(Post post) {
        return new PostResponseDTO(
                post.getId(),
                post.getUser().getUsername(),
                post.getTitle(),
                post.getContent(),
                post.getImagePath(),
                post.getLatitude(),
                post.getLongitude()
        );
    }

    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post não encontrado."));

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!post.getUser().getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("Não permitido excluir posts de terceiros.");
        }
        postRepository.delete(post);
    }
}