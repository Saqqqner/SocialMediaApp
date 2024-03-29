package ru.adel.socialmedia.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.adel.socialmedia.dto.PostDTO;
import ru.adel.socialmedia.security.MyUserDetails;
import ru.adel.socialmedia.services.impl.PostServiceImpl;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

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
    public ResponseEntity<PostDTO> createPost(@AuthenticationPrincipal MyUserDetails myUserDetails, @RequestBody @Valid PostDTO postDTO) {
        Long userId = myUserDetails.getUser().getId();
        PostDTO createdPost = postService.createPost(postDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @PutMapping("/{postId}")
    @Operation(summary = "Обновление поста")
    @ApiResponse(responseCode = "200", description = "Пост успешно обновлен")
    public ResponseEntity<PostDTO> updatePost(@AuthenticationPrincipal MyUserDetails myUserDetails, @PathVariable Long postId, @RequestBody @Valid PostDTO postDTO) {
        Long userId = myUserDetails.getUser().getId();
        PostDTO updatedPost = postService.updatePost(postId, postDTO, userId);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "Удаление поста")
    @ApiResponse(responseCode = "204", description = "Пост успешно удален")
    public ResponseEntity<Void> deletePost(@AuthenticationPrincipal MyUserDetails myUserDetails, @PathVariable Long postId) {
        Long userId = myUserDetails.getUser().getId();
        postService.deletePost(postId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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
    public ResponseEntity<Page<PostDTO>> getPostsByUser(@AuthenticationPrincipal MyUserDetails myUserDetails,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        Long userId = myUserDetails.getUser().getId();
        Page<PostDTO> posts = postService.getPostsByUser(userId, page, size);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/subscribed")
    @Operation(summary = "Получение постов от подписанных пользователей")
    @ApiResponse(responseCode = "200", description = "Успешно получены посты")
    public ResponseEntity<Page<PostDTO>> getPostsBySubscribedUsers(@AuthenticationPrincipal MyUserDetails myUserDetails,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
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

    @PostMapping("/{postId}/like")
    public ResponseEntity<String> addLikeToPost(@AuthenticationPrincipal MyUserDetails myUserDetails, @PathVariable Long postId) {
        Long userId = myUserDetails.getUser().getId();
        postService.addLikeToPost(postId, userId);
        return ResponseEntity.ok("Liked");
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<String> removeLikeFromPost(@AuthenticationPrincipal MyUserDetails myUserDetails, @PathVariable Long postId) {
        Long userId = myUserDetails.getUser().getId();
        postService.removeLikeFromPost(postId, userId);
        return ResponseEntity.ok("Like removed");
    }

    @GetMapping("/like")
    public ResponseEntity<Set<PostDTO>> getLikedPostsByUser(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        Long userId = myUserDetails.getUser().getId();
        Set<PostDTO> likedPosts = postService.getLikedPostsByUser(userId);
        return ResponseEntity.ok(likedPosts);

    }
}