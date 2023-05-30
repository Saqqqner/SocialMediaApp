package ru.adel.SocialMediaApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.adel.SocialMediaApp.models.Post;
import ru.adel.SocialMediaApp.models.PostImage;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findByPost(Post post);
    Optional<PostImage> findByImageUrl(String imageUrl);
    void deleteByPost(Post post);
}
