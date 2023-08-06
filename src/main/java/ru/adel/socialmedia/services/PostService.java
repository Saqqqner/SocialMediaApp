package ru.adel.socialmedia.services;

import org.springframework.data.domain.Page;
import ru.adel.socialmedia.dto.PostDTO;

import java.util.List;
import java.util.Set;

public interface PostService {
    PostDTO createPost(PostDTO postDTO, Long userId);

    PostDTO updatePost(Long postId, PostDTO postDTO, Long userId);

    void deletePost(Long postId, Long userId);

    PostDTO getPostById(Long postId);

    Page<PostDTO> getPostsByUser(Long userId, int page, int size);

    Page<PostDTO> getPostsByFollowingUsers(Long userId, int page, int size);

    List<String> getPostImagesByPost(Long postId);

    void removeLikeFromPost(Long postId, Long userId);

    void addLikeToPost(Long postId, Long userId);

    Set<PostDTO> getLikedPostsByUser(Long userId);

}
