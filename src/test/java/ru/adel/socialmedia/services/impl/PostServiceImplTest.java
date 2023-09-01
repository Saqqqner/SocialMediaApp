package ru.adel.socialmedia.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.adel.socialmedia.dto.PostDTO;
import ru.adel.socialmedia.models.Post;
import ru.adel.socialmedia.models.PostImage;
import ru.adel.socialmedia.models.User;
import ru.adel.socialmedia.repositories.PostImageRepository;
import ru.adel.socialmedia.repositories.PostRepository;
import ru.adel.socialmedia.repositories.UserRepository;
import ru.adel.socialmedia.util.exception.PostNotFoundException;
import ru.adel.socialmedia.util.exception.UnauthorizedException;
import ru.adel.socialmedia.util.exception.UserNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @InjectMocks
    private PostServiceImpl postService;


    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostImageRepository postImageRepository;

    @Mock
    private ModelMapper modelMapper;


    @Test
    void createPost_ValidData_SuccessfullyCreated() {
        // Создаем данные для теста
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle("Test Post");
        postDTO.setText("This is a test post");
        postDTO.setImages(Set.of("image1.jpg", "image2.jpg"));
        Long userId = 121L;

        User user = new User();
        user.setId(userId);


        // Создаем мок объекты для сохранения поста и его изображений
        Post savedPost = new Post();
        savedPost.setId(1L);
        savedPost.setTitle(postDTO.getTitle());
        savedPost.setText(postDTO.getText());
        savedPost.setUser(user);

        Set<PostImage> savedPostImages = new HashSet<>();
        for (String imageUrl : postDTO.getImages()) {
            PostImage postImage = new PostImage();
            postImage.setId(1L);
            postImage.setImageUrl(imageUrl);
            postImage.setPost(savedPost);
            savedPostImages.add(postImage);
        }
        savedPost.setImages(savedPostImages);


        // Создаем маппинг для возвращаемого поста
        PostDTO savedPostDTO = new PostDTO();
        savedPostDTO.setId(savedPost.getId());
        savedPostDTO.setTitle(savedPost.getTitle());
        savedPostDTO.setText(savedPost.getText());
        savedPostDTO.setImages(postDTO.getImages());

        when(modelMapper.map(savedPost, PostDTO.class)).thenReturn(savedPostDTO);
        when(modelMapper.map(postDTO, Post.class)).thenReturn(savedPost);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(savedPost);

        // Вызываем метод создания поста
        PostDTO createdPost = postService.createPost(postDTO, userId);

        // Проверяем, что методы репозитория и маппера были вызваны с правильными аргументами
        verify(userRepository, Mockito.times(1)).findById(userId);
        verify(postRepository, Mockito.times(1)).save(any(Post.class));
        verify(modelMapper, Mockito.times(1)).map(savedPost, PostDTO.class);

        // Проверяем, что возвращенный объект соответствует ожиданиям
        assertNotNull(createdPost);
        assertEquals(savedPostDTO.getId(), createdPost.getId());
        assertEquals(savedPostDTO.getTitle(), createdPost.getTitle());
        assertEquals(savedPostDTO.getText(), createdPost.getText());
        assertEquals(savedPostDTO.getImages(), createdPost.getImages());
    }

    @Test
    void createPost_UserNotFound() {
        Long userId = 1L;
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle("Test Post");

        // Настройка макета репозитория пользователя
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Вызов метода createPost() и проверка, что исключение UserNotFoundException было выброшено
        assertThrows(UserNotFoundException.class, () -> postService.createPost(postDTO, userId));

        // Убедитесь, что методы репозитория не были вызваны
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(postRepository);
    }

    @Test
    void updatePost_PostNotFound() {
        Long postId = 1L;
        Long userId = 1L;
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle("Updated Post");

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Вызов метода updatePost() и проверка, что исключение PostNotFoundException было выброшено
        assertThrows(PostNotFoundException.class, () -> postService.updatePost(postId, postDTO, userId));

        verify(postRepository, times(1)).findById(postId);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(userRepository);

    }

    @Test
    void updatePost_Unauthorized() {
        Long postId = 1L;
        Long userId = 1L;
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle("Updated Post");
        postDTO.setText("Updated text");

        // Создание макета поста
        Post post = new Post();
        post.setId(postId);
        User user = new User();
        user.setId(userId + 1L);
        post.setUser(user);

        // Настройка макета репозитория постов
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // Вызов метода updatePost() и проверка, что исключение UnauthorizedException было выброшено
        assertThrows(UnauthorizedException.class, () -> postService.updatePost(postId, postDTO, userId));

        // Убедитесь, что методы репозитория были вызваны
        verify(postRepository, times(1)).findById(postId);
        verifyNoMoreInteractions(postRepository);
    }

    @Test
    void updatePost_ValidData_SuccessfullyUpdated() {
        Long userId = 1L;
        Long postId = 1L;
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle("Updated Post");
        postDTO.setText("Updated Text");
        postDTO.setImages(Set.of("ososo.jpg"));


        Post post = new Post();


        Set<PostImage> oldImages = new HashSet<>();
        PostImage image1 = new PostImage();
        image1.setId(1L);
        image1.setImageUrl("image1.jpg");
        image1.setPost(post);
        oldImages.add(image1);

        post.setImages(oldImages);
        post.setText("Old Title");
        post.setTitle("Old Text");
        post.setId(postId);

        User user = new User();
        user.setId(userId);
        post.setUser(user);

        PostDTO savedPostDTO = new PostDTO();
        savedPostDTO.setId(post.getId());
        savedPostDTO.setTitle(postDTO.getTitle());
        savedPostDTO.setText(postDTO.getText());
        savedPostDTO.setImages(postDTO.getImages());


        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(modelMapper.map(post, PostDTO.class)).thenReturn(savedPostDTO);


        PostDTO updatedPostDTO = postService.updatePost(postId, postDTO, userId);

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(post);

        assertEquals(postDTO.getText(), updatedPostDTO.getText());
        assertEquals(postDTO.getTitle(), updatedPostDTO.getTitle());
        assertEquals(postDTO.getImages(), updatedPostDTO.getImages());


    }

    @Test
    void deletePost_SuccessfullyDelete() {
        Long postId = 1L;
        Long userId = 4L;

        User user = new User();
        user.setId(userId);

        Post post = new Post();
        post.setUser(user);
        post.setTitle("Title");
        post.setText("Text");
        post.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        postService.deletePost(postId, userId);

        verify(postRepository, Mockito.times(1)).findById(postId);
        verify(postRepository, Mockito.times(1)).deleteById(postId);
    }

    @Test
    void deletePost_PostNotFound_PostNotFoundExceptionThrown() {
        Long userId = 1L;
        Long postId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.deletePost(postId, userId));
    }

    @Test
    void deletePost_UnauthorizedUser_UnauthorizedExceptionThrown() {
        // Arrange
        Long postId = 1L;
        Long userId = 2L;

        User user = new User();
        user.setId(3L);

        Post post = new Post();
        post.setId(postId);
        post.setUser(user);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));


        assertThrows(UnauthorizedException.class, () -> postService.deletePost(postId, userId));
    }

    @Test
    void getPostById_ExistingPost_Success() {
        Long postId = 1L;
        Post post = new Post();
        post.setText("Mama");
        post.setId(postId);

        PostDTO postDTO = new PostDTO();
        postDTO.setId(postId);
        post.setText("Mama");

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(modelMapper.map(post, PostDTO.class)).thenReturn(postDTO);

        PostDTO result = postService.getPostById(postId);

        assertEquals(postDTO.getId(), result.getId());

        verify(postRepository).findById(postId);
        verify(modelMapper).map(post, PostDTO.class);
    }

    @Test
    void getPostById_InvalidId_ThrowsPostNotFoundException() {
        Long postId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.getPostById(postId));

        verify(postRepository).findById(postId);
    }

    @Test
    void getPostsByUser_ExistingUser_ReturnsPageOfPostDTOs() {
        // Arrange
        Long userId = 1L;
        int page = 0;
        int size = 20;

        User user = new User();
        user.setId(userId);

        List<Post> posts = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            Post post = new Post();
            post.setUser(user);
            post.setTitle("Title " + i);
            post.setText("Text " + i);
            posts.add(post);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findByUserOrderByCreatedAtDesc(user, pageable)).thenReturn(postPage);


        for (Post post : posts) {
            PostDTO postDTO = new PostDTO();
            postDTO.setId(post.getId());
            postDTO.setTitle(post.getTitle());
            postDTO.setText(post.getText());
            when(modelMapper.map(post, PostDTO.class)).thenReturn(postDTO);
        }


        // Act
        Page<PostDTO> result = postService.getPostsByUser(userId, page, size);

        // Assert
        assertEquals(size, result.getSize());
        assertEquals(posts.size(), result.getTotalElements());

        List<PostDTO> postDTOs = result.getContent();
        assertEquals(size, postDTOs.size());

        for (int i = 0; i < size; i++) {
            Post post = posts.get(i);
            PostDTO postDTO = postDTOs.get(i);
            assertEquals(post.getId(), postDTO.getId());
            assertEquals(post.getTitle(), postDTO.getTitle());
            assertEquals(post.getText(), postDTO.getText());
        }

        verify(userRepository).findById(userId);
        verify(postRepository).findByUserOrderByCreatedAtDesc(user, pageable);
    }

    @Test
    void getPostsByUser_NonExistingUser_ThrowsUserNotFoundException() {
        // Arrange
        Long userId = 1L;
        int page = 0;
        int size = 10;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> postService.getPostsByUser(userId, page, size));

        verify(userRepository).findById(userId);
        verifyNoInteractions(postRepository);
    }

    @Test
    void getPostsByFollowingUsers_ExistingUser_ReturnsPageOfPostDTOs() {
        // Arrange
        Long userId = 1L;
        int page = 0;
        int size = 20;

        User user = new User();
        user.setId(userId);
        user.setFollowing(new HashSet<>());

        User followingUser1 = new User();
        followingUser1.setId(2L);

        User followingUser2 = new User();
        followingUser2.setId(3L);

        user.getFollowing().add(followingUser1);
        user.getFollowing().add(followingUser2);
        List<User> followingUsers = new ArrayList<>(user.getFollowing());


        List<Post> posts = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            Post post = new Post();
            post.setId((long) i);
            post.setUser(followingUser1);
            post.setTitle("Title " + i);
            post.setText("Text " + i);
            posts.add(post);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findByUserInOrderByCreatedAtDesc(followingUsers, pageable)).thenReturn(postPage);
        when(modelMapper.map(any(), eq(PostDTO.class))).thenReturn(new PostDTO());

        // Act
        Page<PostDTO> result = postService.getPostsByFollowingUsers(userId, page, size);

        // Assert
        assertEquals(size, result.getSize());
        assertEquals(posts.size(), result.getTotalElements());

        List<PostDTO> postDTOs = result.getContent();
        assertEquals(size, postDTOs.size());

        verify(userRepository).findById(userId);
        verify(postRepository).findByUserInOrderByCreatedAtDesc(followingUsers, pageable);
        verify(modelMapper, times(size)).map(any(), eq(PostDTO.class));
    }

    @Test
    void getPostImagesByPost_ExistingPost_ReturnsListOfImageUrls() {
        // Arrange
        Long postId = 1L;

        // Создаем пост
        Post post = new Post();
        post.setId(postId);

        // Создаем изображения
        List<PostImage> postImages = new ArrayList<>();
        postImages.add(new PostImage(1L, "image1.jpg", post));
        postImages.add(new PostImage(2L, "image2.jpg", post));
        postImages.add(new PostImage(3L, "image3.jpg", post));

        // Мокируем репозитории
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postImageRepository.findByPost(post)).thenReturn(postImages);

        // Act
        List<String> result = postService.getPostImagesByPost(postId);

        // Assert
        assertEquals(postImages.size(), result.size());
        assertTrue(result.contains("image1.jpg"));
        assertTrue(result.contains("image2.jpg"));
        assertTrue(result.contains("image3.jpg"));

        verify(postRepository).findById(postId);
        verify(postImageRepository).findByPost(post);
    }

}

