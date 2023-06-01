package ru.adel.SocialMediaApp.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.adel.SocialMediaApp.dto.PostDTO;
import ru.adel.SocialMediaApp.dto.PostImageDTO;
import ru.adel.SocialMediaApp.security.MyUserDetails;
import ru.adel.SocialMediaApp.services.PostService;
import ru.adel.SocialMediaApp.services.impl.PostServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Посты")
public class PostController {
    private final PostServiceImpl postService;

    public PostController(PostServiceImpl postService) {
        this.postService = postService;
    }

    @PostMapping()
    @Operation(summary = "Создание поста")
    @ApiResponse(responseCode = "201", description = "Пост успешно создан")
    public ResponseEntity<PostDTO> createPost(Authentication authentication, @RequestBody PostDTO postDTO) {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Long userId = myUserDetails.getUser().getId();
        PostDTO createdPost = postService.createPost(postDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @PutMapping("/{postId}")
    @Operation(summary = "Обновление поста")
    @ApiResponse(responseCode = "200", description = "Пост успешно обновлен")

    public ResponseEntity<PostDTO> updatePost(@PathVariable Long postId, @RequestBody PostDTO postDTO, Authentication authentication) {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Long userId = myUserDetails.getUser().getId();
        PostDTO updatedPost = postService.updatePost(postId, postDTO, userId);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "Удаление поста")
    @ApiResponse(responseCode = "204", description = "Пост успешно удален")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, Authentication authentication) {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Long userId = myUserDetails.getUser().getId();
        postService.deletePost(postId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}")
    @Operation(summary = "Получение поста по идентификатору")
    @ApiResponse(responseCode = "200", description = "Успешно получен пост")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long postId) {
        PostDTO postDTO = postService.getPostById(postId);
        return ResponseEntity.ok(postDTO);
    }

    @GetMapping("/user")
    @Operation(summary = "Получение постов пользователя")
    @ApiResponse(responseCode = "200", description = "Успешно получены посты")
    public ResponseEntity<Page<PostDTO>> getPostsByUser(Authentication authentication,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Long userId = myUserDetails.getUser().getId();
        Page<PostDTO> posts = postService.getPostsByUser(userId, page, size);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/subscribed")
    @Operation(summary = "Получение постов от подписанных пользователей")
    @ApiResponse(responseCode = "200", description = "Успешно получены посты")
    public ResponseEntity<Page<PostDTO>> getPostsBySubscribedUsers(Authentication authentication,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Long userId = myUserDetails.getUser().getId();
        Page<PostDTO> posts = postService.getPostsByFollowingUsers(userId, page, size);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}/images")
    @Operation(summary = "Получение изображений поста")
    @ApiResponse(responseCode = "200", description = "Успешно получены изображения")
    public ResponseEntity<List<String>> getPostImagesByPost(@PathVariable Long postId) {
        List<String> postImages = postService.getPostImagesByPost(postId);
        return ResponseEntity.ok(postImages);
    }
}