package ru.adel.socialmedia.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.adel.socialmedia.models.Like;
import ru.adel.socialmedia.models.Post;
import ru.adel.socialmedia.models.User;

import java.util.List;
import java.util.Optional;


public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findAllByUserId(Long userId);

    Optional<Like> findByPostAndUser(Post post, User user);
}
