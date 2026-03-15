package com.project.posts.controller;

import com.project.posts.model.dto.PostRequestDTO;
import com.project.posts.model.dto.PostResponseDTO;
import com.project.posts.model.entity.Post;
import com.project.posts.repository.PostRepository;
import com.project.posts.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;

    @GetMapping
    public ResponseEntity<List<PostResponseDTO>> getAllPosts() {
        // Deixe o Service fazer o trabalho pesado dentro da transação
        return ResponseEntity.ok(postService.findAll());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponseDTO> create(
            @RequestPart("data") PostRequestDTO request,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.createPost(request, file));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDTO> update(@PathVariable Long id, @RequestBody PostRequestDTO request) {
        return ResponseEntity.ok(postService.updatePost(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok(Map.of("message", "Post removido com sucesso!"));
    }


}
