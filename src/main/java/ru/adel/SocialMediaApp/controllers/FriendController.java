package ru.adel.SocialMediaApp.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.adel.SocialMediaApp.dto.UserDTO;
import ru.adel.SocialMediaApp.security.MyUserDetails;
import ru.adel.SocialMediaApp.services.impl.FriendRequestImpl;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@Tag(name = "Друзья")
public class FriendController {

    private final FriendRequestImpl friendRequest;

    public FriendController(FriendRequestImpl friendRequest) {
        this.friendRequest = friendRequest;
    }


    @GetMapping("/following")
    @Operation(summary = "Получение пользователей, на которых подписан текущий пользователь")
    @ApiResponse(responseCode = "200", description = "Успешно получены пользователи")
    public ResponseEntity<List<UserDTO>> getFollowingUsers(Authentication authentication) {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Long userId = myUserDetails.getUser().getId();
        List<UserDTO> followingUsers = friendRequest.getFollowingUsers(userId);
        return ResponseEntity.status(HttpStatus.OK).body(followingUsers);
    }

    @GetMapping("/followers")
    @Operation(summary = "Получение пользователей, подписанных на текущего пользователя")
    @ApiResponse(responseCode = "200", description = "Успешно получены пользователи")
    public ResponseEntity<List<UserDTO>> getFollowers(Authentication authentication) {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Long userId = myUserDetails.getUser().getId();
        List<UserDTO> followers = friendRequest.getFollowers(userId);
        return ResponseEntity.status(HttpStatus.OK).body(followers);
    }
    @PostMapping("/send-friend-request/{friendId}")
    @Operation(summary = "Отправка запроса на добавление в друзья")
    @ApiResponse(responseCode = "200", description = "Запрос успешно отправлен")
    public ResponseEntity<UserDTO> sendFriendRequest( Authentication authentication,@PathVariable Long friendId) {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Long userId = myUserDetails.getUser().getId();
        UserDTO userDTO = friendRequest.sendFriendRequest(userId, friendId);
        return ResponseEntity.ok(userDTO);
    }
    @PostMapping("/accept-friend-request/{friendId}")
    @Operation(summary = "Принятие запроса на добавление в друзья")
    @ApiResponse(responseCode = "200", description = "Запрос успешно принят")
    public ResponseEntity<UserDTO> acceptFriendRequest(Authentication authentication, @PathVariable Long friendId) {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Long userId = myUserDetails.getUser().getId();
        UserDTO userDTO = friendRequest.acceptFriendRequest(userId, friendId);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/cancel-friend-request/{friendId}")
    @Operation(summary = "Отмена запроса на добавление в друзья")
    @ApiResponse(responseCode = "200", description = "Запрос успешно отменен")
    public ResponseEntity<UserDTO> cancelFriendRequest(Authentication authentication, @PathVariable Long friendId) {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Long userId = myUserDetails.getUser().getId();
        UserDTO userDTO = friendRequest.cancelFriendRequest(userId, friendId);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/remove-friend/{friendId}")
    @Operation(summary = "Удаление друга из списка друзей")
    @ApiResponse(responseCode = "200", description = "Друг успешно удален")
    public ResponseEntity<UserDTO> removeFriend(Authentication authentication, @PathVariable Long friendId) {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Long userId = myUserDetails.getUser().getId();
        UserDTO userDTO = friendRequest.removeFriend(userId, friendId);
        return ResponseEntity.ok(userDTO);
    }
}
