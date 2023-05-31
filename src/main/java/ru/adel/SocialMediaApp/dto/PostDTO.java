package ru.adel.SocialMediaApp.dto;

import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor

public class PostDTO {
    private Long id;
    private String title;
    private String text;
    private Set<String> images;


}
