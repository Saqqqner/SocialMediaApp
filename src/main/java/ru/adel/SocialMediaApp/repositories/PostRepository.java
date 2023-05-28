package ru.adel.SocialMediaApp.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.adel.SocialMediaApp.models.Post;
import ru.adel.SocialMediaApp.models.User;

import java.util.List;
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByUserInOrderByCreatedAtDesc(List<User> users, Pageable pageable);
    Page<Post> findByUserOrderByCreatedAtDesc(User user,Pageable pageable);
}