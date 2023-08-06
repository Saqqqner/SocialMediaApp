package ru.adel.socialmedia.dto;

import lombok.Data;
import ru.adel.socialmedia.models.Post;
import ru.adel.socialmedia.models.User;

@Data
public class LikeDTO {
    private Long id;

    private User user;

    private Post post;


}
