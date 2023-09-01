package ru.adel.socialmedia.models;

import lombok.*;

import javax.persistence.*;

@EqualsAndHashCode(exclude = "post")
@Entity
@Table(name = "post_images")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;



    @Override
    public String toString() {
        return "PostImage{" +
                "id=" + id +
                ", imageUrl='" + imageUrl + '\'' +
                ", post=" + post +
                '}';
    }



}
