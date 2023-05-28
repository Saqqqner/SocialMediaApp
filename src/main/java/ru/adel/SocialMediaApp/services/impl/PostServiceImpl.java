package ru.adel.SocialMediaApp.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.adel.SocialMediaApp.dto.PostDTO;
import ru.adel.SocialMediaApp.dto.PostImageDTO;
import ru.adel.SocialMediaApp.models.Post;
import ru.adel.SocialMediaApp.models.PostImage;
import ru.adel.SocialMediaApp.models.User;
import ru.adel.SocialMediaApp.repositories.PostImageRepository;
import ru.adel.SocialMediaApp.repositories.PostRepository;
import ru.adel.SocialMediaApp.repositories.UserRepository;
import ru.adel.SocialMediaApp.services.PostService;
import ru.adel.SocialMediaApp.util.exception.PostNotFoundException;
import ru.adel.SocialMediaApp.util.exception.UnauthorizedException;
import ru.adel.SocialMediaApp.util.exception.UserNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PostImageRepository postImageRepository;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, ModelMapper modelMapper, PostImageRepository postImageRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.postImageRepository = postImageRepository;
    }

    //Создаем пост
    @Override
    public PostDTO createPost(PostDTO postDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Post post = modelMapper.map(postDTO, Post.class);
        post.setUser(user);

        Post savedPost = postRepository.save(post);
        return modelMapper.map(savedPost, PostDTO.class);
    }

    @Override
    public PostDTO updatePost(Long postId, PostDTO postDTO, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + postId));

        // Проверка авторизации пользователя
        if (!post.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Unauthorized to update this post");
        }

        modelMapper.map(postDTO, post);

        Post updatedPost = postRepository.save(post);
        return modelMapper.map(updatedPost, PostDTO.class);
    }

    @Override
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + postId));

        // Проверка авторизации пользователя
        if (!post.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Unauthorized to delete this post");
        }

        postRepository.deleteById(postId);
    }

    @Override
    public PostDTO getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + postId));

        return modelMapper.map(post, PostDTO.class);
    }

    @Override
    public Page<PostDTO> getPostsByUser(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден с ID: " + userId));

        // Создаем объект Pageable для пагинации и сортировки
        PageRequest pageRequest = PageRequest.of(page, size);

        // Получаем страницу постов для пользователя с пагинацией и сортировкой
        Page<Post> postPage = postRepository.findByUserOrderByCreatedAtDesc(user, pageRequest);

        List<PostDTO> postDTOs = postPage.getContent().stream()
                .map(post -> modelMapper.map(post, PostDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<>(postDTOs, pageRequest, postPage.getTotalElements());
    }

    @Override
    public Page<PostDTO> getPostsBySubscribedUsers(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Получаем всех пользователей, на которых подписан текущий пользователь
        List<User> subscribedUsers = new ArrayList<>(user.getFollowing());

        // Создаем объект Pageable для пагинации
        PageRequest pageRequest = PageRequest.of(page, size);

        // Получаем страницу постов с учетом подписанных пользователей и сортировки по времени создания
        Page<Post> postPage = postRepository.findByUserInOrderByCreatedAtDesc(subscribedUsers, pageRequest);

        List<PostDTO> postDTOs = postPage.getContent().stream()
                .map(post -> modelMapper.map(post, PostDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<>(postDTOs, pageRequest, postPage.getTotalElements());
    }
    @Override
    public List<PostImageDTO> getPostImagesByPost(Long postId) {
        // Получаем пост по его идентификатору из postDTO
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        // Получаем все изображения, связанные с данным постом
        List<PostImage> postImages = postImageRepository.findByPost(post);

        // Преобразуем список PostImage в список PostImageDTO
        List<PostImageDTO> postImageDTOs = postImages.stream()
                .map(postImage -> modelMapper.map(postImage, PostImageDTO.class))
                .collect(Collectors.toList());

        return postImageDTOs;
    }
}
