package ru.adel.SocialMediaApp.services;

import org.springframework.data.domain.Page;
import ru.adel.SocialMediaApp.dto.PostDTO;
import ru.adel.SocialMediaApp.dto.PostImageDTO;
import java.util.List;

public interface PostService {
    PostDTO createPost(PostDTO postDTO, Long userId);
    PostDTO updatePost(Long postId,PostDTO postDTO,Long userId);
    void deletePost(Long postId,Long userId);
    PostDTO getPostById(Long postId);
    Page<PostDTO> getPostsByUser(Long userId, int page, int size);
    Page<PostDTO> getPostsBySubscribedUsers(Long userId, int page, int size);

    List<PostImageDTO> getPostImagesByPost(Long postId);


}
