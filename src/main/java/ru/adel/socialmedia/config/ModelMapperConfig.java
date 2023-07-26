package ru.adel.socialmedia.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.adel.socialmedia.dto.MessageDTO;
import ru.adel.socialmedia.dto.PostDTO;
import ru.adel.socialmedia.dto.RegistrationRequest;
import ru.adel.socialmedia.dto.UserDTO;
import ru.adel.socialmedia.models.Message;
import ru.adel.socialmedia.models.Post;
import ru.adel.socialmedia.models.User;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Конфигурация маппинга для игнорирования поля id при копировании из PostDTO в Post
        modelMapper.createTypeMap(PostDTO.class, Post.class)
                .addMappings(mapping -> mapping.skip(Post::setId));
        modelMapper.createTypeMap(UserDTO.class, User.class)
                .addMappings(mapping -> mapping.skip(User::setId));
        modelMapper.createTypeMap(RegistrationRequest.class, User.class)
                .addMapping(RegistrationRequest::getEmail, User::setEmail)
                .addMapping(RegistrationRequest::getUsername, User::setUsername)
                .addMapping(RegistrationRequest::getPassword, User::setPassword);
        modelMapper.addMappings(new PropertyMap<Message, MessageDTO>() {
            @Override
            protected void configure() {
                map().setRecipientId(source.getRecipient().getId());
                map().setSenderId(source.getSender().getId());
            }
        });


        return modelMapper;
    }
}
