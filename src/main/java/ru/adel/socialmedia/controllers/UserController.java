package ru.adel.socialmedia.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.adel.socialmedia.dto.ChangePasswordDTO;
import ru.adel.socialmedia.dto.UserDTO;
import ru.adel.socialmedia.models.User;
import ru.adel.socialmedia.repositories.UserRepository;
import ru.adel.socialmedia.security.MyUserDetails;
import ru.adel.socialmedia.security.jwt.JWTUtil;
import ru.adel.socialmedia.services.impl.UserServiceImpl;
import ru.adel.socialmedia.util.exception.IncorrectPasswordException;
import ru.adel.socialmedia.util.exception.UserNotFoundException;
import ru.adel.socialmedia.util.response.JWTResponse;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Пользователи")
public class UserController {
    private final UserServiceImpl userService;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserServiceImpl userService, UserRepository userRepository, JWTUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Получение пользователя по идентификатору")
    @ApiResponse(responseCode = "200", description = "Успешно получен пользователь")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        UserDTO userDTO = userService.getUserById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userDTO);
    }

    @GetMapping()
    @Operation(summary = "Получение всех пользователей, кроме текущего")
    @ApiResponse(responseCode = "200", description = "Успешно получены пользователи")
    public ResponseEntity<List<UserDTO>> getAllUsersExceptCurrent(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        Long userId = myUserDetails.getUser().getId();
        List<UserDTO> users = userService.getAllUsersExceptCurrentUser(userId);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/update")
    @Operation(summary = "Обновление данных пользователя")
    @ApiResponse(responseCode = "200", description = "Данные пользователя успешно обновлены")
    public ResponseEntity<JWTResponse> updateUser(@AuthenticationPrincipal MyUserDetails myUserDetails, @Valid @RequestBody  UserDTO userDTO) {
        Long userId = myUserDetails.getUser().getId();
        UserDTO existingUser = userService.getUserById(userId);
        if (userDTO.getUsername() != null) {
            existingUser.setUsername(userDTO.getUsername());
        }
        if (userDTO.getEmail() != null) {
            existingUser.setEmail(userDTO.getEmail());
        }
        UserDTO updatedUser = userService.updateUser(userId, existingUser);
        String newToken = jwtUtil.generateToken(updatedUser.getUsername());
        JWTResponse jwtResponse = new JWTResponse(newToken);
        return ResponseEntity.status(HttpStatus.OK).body(jwtResponse);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Удаление пользователя")
    @ApiResponse(responseCode = "204", description = "Пользователь успешно удален")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        Long userId = myUserDetails.getUser().getId();
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/password")
    @Operation(summary = "Изменение пароля пользователя")
    @ApiResponse(responseCode = "200", description = "Пароль успешно изменен")
    public ResponseEntity<UserDTO> changePassword(@AuthenticationPrincipal MyUserDetails myUserDetails, @RequestBody @Valid ChangePasswordDTO changePasswordDTO) {
        Long userId = myUserDetails.getUser().getId();
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()){
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
        User user = userOptional.get();

        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPassword())) {
            throw new IncorrectPasswordException("Неверный текущий пароль");
        }

        UserDTO updatedUserDTO = userService.changePassword(userId, changePasswordDTO.getNewPassword());

        return ResponseEntity.status(HttpStatus.OK).body(updatedUserDTO);
    }


}
