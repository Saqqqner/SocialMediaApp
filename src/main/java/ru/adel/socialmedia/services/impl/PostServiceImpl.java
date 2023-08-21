package ru.adel.socialmedia.services.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.adel.socialmedia.dto.PostDTO;
import ru.adel.socialmedia.models.Like;
import ru.adel.socialmedia.models.Post;
import ru.adel.socialmedia.models.PostImage;
import ru.adel.socialmedia.models.User;
import ru.adel.socialmedia.repositories.LikeRepository;
import ru.adel.socialmedia.repositories.PostImageRepository;
import ru.adel.socialmedia.repositories.PostRepository;
import ru.adel.socialmedia.repositories.UserRepository;
import ru.adel.socialmedia.services.PostService;
import ru.adel.socialmedia.util.exception.LikeAlreadyExistsException;
import ru.adel.socialmedia.util.exception.PostNotFoundException;
import ru.adel.socialmedia.util.exception.UnauthorizedException;
import ru.adel.socialmedia.util.exception.UserNotFoundException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {
    private static final String MSG_POST = "Post not found with ID: ";
    private static final String MSG_USER = "User not found with ID: ";
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PostImageRepository postImageRepository;
    private final LikeRepository likeRepository;

    @Override
    @Transactional
    public PostDTO createPost(PostDTO postDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(MSG_USER + userId));

        Post post = modelMapper.map(postDTO, Post.class);
        post.setUser(user);

        Set<PostImage> postImages = new HashSet<>();
        if (postDTO.getImages() != null) {
            for (String imageUrl : postDTO.getImages()) {
                PostImage postImage = new PostImage();
                postImage.setImageUrl(imageUrl);
                postImage.setPost(post);
                postImages.add(postImage);
            }
        }
        post.setImages(postImages);

        Post savedPost = postRepository.save(post);
        return modelMapper.map(savedPost, PostDTO.class);
    }


    @Override
    @Transactional
    public PostDTO updatePost(Long postId, PostDTO postDTO, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(MSG_POST + postId));

        // Проверка авторизации пользователя
        if (!post.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Unauthorized to update this post");
        }

        // Получение старых изображений
        Set<PostImage> oldImages = post.getImages();

        // Создание нового набора изображений
        Set<PostImage> newImages = new HashSet<>();

        // Проверка наличия новых изображений
        if (postDTO.getImages() != null) {
            for (String imageUrl : postDTO.getImages()) {
                PostImage postImage = new PostImage();
                postImage.setImageUrl(imageUrl);
                postImage.setPost(post);
                newImages.add(postImage);
            }
        }

        // Удаление старых изображений, если есть новые
        if (!newImages.isEmpty()) {
            oldImages.retainAll(newImages); // Удаление старых изображений, оставляя только те, которые есть в новом наборе
            post.getImages().addAll(newImages); // Добавление новых изображений
        }


        post.setTitle(postDTO.getTitle());
        post.setText(postDTO.getText());

        Post updatedPost = postRepository.save(post);
        return modelMapper.map(updatedPost, PostDTO.class);
    }


    @Override
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(MSG_POST + postId));

        // Проверка авторизации пользователя
        if (!post.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Unauthorized to delete this post");
        }

        postRepository.deleteById(postId);
    }

    @Override
    public PostDTO getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(MSG_POST + postId));

        return modelMapper.map(post, PostDTO.class);
    }

    @Override
    public Page<PostDTO> getPostsByUser(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(MSG_USER + userId));

        // Создаем объект Pageable для пагинации и сортировки
        PageRequest pageRequest = PageRequest.of(page, size);

        // Получаем страницу постов для пользователя с пагинацией и сортировкой
        Page<Post> postPage = postRepository.findByUserOrderByCreatedAtDesc(user, pageRequest);

        List<PostDTO> postDTOs = postPage.getContent().stream()
                .map(post -> modelMapper.map(post, PostDTO.class))
                .toList();

        return new PageImpl<>(postDTOs, pageRequest, postPage.getTotalElements());
    }

    @Override
    public Page<PostDTO> getPostsByFollowingUsers(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(MSG_USER + userId));

        // Получаем всех пользователей, на которых подписан текущий пользователь
        List<User> subscribedUsers = new ArrayList<>(user.getFollowing());

        // Создаем объект Pageable для пагинации
        PageRequest pageRequest = PageRequest.of(page, size);

        // Получаем страницу постов с учетом подписанных пользователей и сортировки по времени создания
        Page<Post> postPage = postRepository.findByUserInOrderByCreatedAtDesc(subscribedUsers, pageRequest);

        List<PostDTO> postDTOs = postPage.getContent().stream()
                .map(post -> modelMapper.map(post, PostDTO.class))
                .toList();

        return new PageImpl<>(postDTOs, pageRequest, postPage.getTotalElements());
    }

    @Override
    public List<String> getPostImagesByPost(Long postId) {
        // Получаем пост по его идентификатору из postDTO
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(MSG_POST + postId));

        // Получаем все изображения, связанные с данным постом
        List<PostImage> postImages = postImageRepository.findByPost(post);

        // Преобразуем список PostImage в список URL-адресов

        return postImages.stream()
                .map(PostImage::getImageUrl)
                .toList();
    }

    @Override
    @Transactional
    public void removeLikeFromPost(Long postId, Long userId) {
        // Получите пост из базы данных по его id
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(MSG_POST + postId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(MSG_USER + userId));

        // Найдите лайк, который нужно удалить
        Like likeToRemove = post.getLikes().stream()
                .filter(like -> like.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElse(null);

        if (likeToRemove != null) {
            // Удалите лайк из множества лайков поста
            post.getLikes().remove(likeToRemove);
            user.getLikes().remove(likeToRemove);

            // Удалите лайк из базы данных
            likeRepository.delete(likeToRemove);

            // Обновите счетчик лайков через метод репозитория
            postRepository.updateLikesCount(postId);
        }
    }

    @Override
    @Transactional
    public void addLikeToPost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(MSG_POST + postId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(MSG_USER + userId));

        likeRepository.findByPostAndUser(post, user)
                .ifPresent(like -> {
                    throw new LikeAlreadyExistsException("User with ID " + userId + " already liked post with ID " + postId);
                });

        // Создайте новый лайк
        Like like = new Like();
        like.setPost(post);
        like.setUser(user);

        // Добавьте лайк в множество лайков поста
        post.getLikes().add(like);
        user.getLikes().add(like);

        // Сохраните лайк в базе данных
        likeRepository.save(like);

        // Обновите счетчик лайков через метод репозитория
        postRepository.updateLikesCount(postId);
    }

    @Override
    public Set<PostDTO> getLikedPostsByUser(Long userId) {
        return likeRepository.findAllByUserId(userId)
                .stream()
                .map(Like::getPost)
                .map(post -> modelMapper.map(post, PostDTO.class))
                .collect(Collectors.toSet());
    }
}
