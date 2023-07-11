package ru.adel.SocialMediaApp.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.adel.SocialMediaApp.dto.MessageDTO;
import ru.adel.SocialMediaApp.dto.PostDTO;
import ru.adel.SocialMediaApp.dto.RegistrationRequest;
import ru.adel.SocialMediaApp.dto.UserDTO;
import ru.adel.SocialMediaApp.models.Message;
import ru.adel.SocialMediaApp.models.Post;
import ru.adel.SocialMediaApp.models.User;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Конфигурация маппинга для игнорирования поля id при копировании из PostDTO в Post
        modelMapper.createTypeMap(PostDTO.class, Post.class)
                .addMappings(mapping -> mapping.skip(Post::setId));
        modelMapper.createTypeMap(UserDTO.class, User.class)
                .addMappings(mapping->mapping.skip(User::setId));
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
