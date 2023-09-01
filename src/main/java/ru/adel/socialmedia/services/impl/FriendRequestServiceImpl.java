package ru.adel.socialmedia.services.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.adel.socialmedia.dto.UserDTO;
import ru.adel.socialmedia.models.User;
import ru.adel.socialmedia.repositories.UserRepository;
import ru.adel.socialmedia.services.FriendRequest;
import ru.adel.socialmedia.util.exception.UserNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendRequestServiceImpl implements FriendRequest {

    private static final Logger logger = LoggerFactory.getLogger(FriendRequestServiceImpl.class);
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getFollowingUsers(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        return user.getFollowing().stream()
                .map(u -> modelMapper.map(u, UserDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getFollowers(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        return user.getFollowers().stream()
                .map(u -> modelMapper.map(u, UserDTO.class))
                .toList();
    }

    @Override
    @Transactional
    public UserDTO sendFriendRequest(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + friendId));
        if (user.getFollowers().contains(friend)) {
            // Если пользователь уже является подписчиком, вызываем метод acceptFriendRequest
            return acceptFriendRequest(userId, friendId);
        }
        user.getFollowing().add(friend);//Добавляем друга в список пользователей на которых подписан данный пользователь
        friend.getFollowers().add(user); // Добавляем пользователя в список подписчиков друга
        userRepository.save(user);
        userRepository.save(friend);
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    @Transactional
    public UserDTO cancelFriendRequest(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + friendId));
        if (user.getFollowing().contains(friend)) {
            user.getFollowing().remove(friend); // Удаляем пользователя из списка подписчиков друга
            friend.getFollowers().remove(user); // Удаляем друга из списка пользователей, на которых подписан текущий пользователь
            userRepository.save(user);
            userRepository.save(friend);
        }
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    @Transactional
    public UserDTO acceptFriendRequest(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + friendId));
        if (!user.getFriends().contains(friend) && user.getFollowers().contains(friend)) {
            user.getFriends().add(friend);//Добавляем друга в список друзей пользователя
            friend.getFriends().add(user); //Добавляем пользователя в список друзей данного друга
            friend.getFollowers().add(user);//Добавляем пользователя в список пользователей на которых подписан друг
            user.getFollowing().add(friend); // Добавляем друга в список подписчиков пользователя
            userRepository.save(user);
            userRepository.save(friend);
        }
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    @Transactional
    public UserDTO removeFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + friendId));
        if (user.getFriends().contains(friend)) {
            user.getFriends().remove(friend);
            friend.getFriends().remove(user);
            // Удаляем пользователя из списка подписчиков друга
            friend.getFollowers().remove(user);
            // Удаляем друга из списка пользователей, на которых подписан текущий пользователь
            user.getFollowing().remove(friend);
            userRepository.save(user);
            userRepository.save(friend);
        }
        return modelMapper.map(user, UserDTO.class);
    }
}
