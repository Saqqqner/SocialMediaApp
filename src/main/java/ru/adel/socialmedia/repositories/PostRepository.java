package ru.adel.socialmedia.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.adel.socialmedia.models.Post;
import ru.adel.socialmedia.models.User;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByUserInOrderByCreatedAtDesc(List<User> users, Pageable pageable);

    Page<Post> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    @Modifying
    @Query(value = "UPDATE Post p SET p.likesCount = (SELECT COUNT(l) FROM p.likes l) WHERE p.id = :postId")
    void updateLikesCount(@Param("postId") Long postId);

}