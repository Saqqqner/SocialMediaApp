package ru.adel.SocialMediaApp.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.adel.SocialMediaApp.dto.ChangePasswordDTO;
import ru.adel.SocialMediaApp.dto.UserDTO;
import ru.adel.SocialMediaApp.models.User;
import ru.adel.SocialMediaApp.repositories.UserRepository;
import ru.adel.SocialMediaApp.security.MyUserDetails;
import ru.adel.SocialMediaApp.security.jwt.JWTUtil;
import ru.adel.SocialMediaApp.services.impl.UserServiceImpl;
import ru.adel.SocialMediaApp.util.exception.IncorrectPasswordException;
import ru.adel.SocialMediaApp.util.response.JWTResponse;

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

    public UserController(UserServiceImpl userService,  UserRepository userRepository, JWTUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }
    @GetMapping("/{userId}")
    @Operation(summary = "Получение пользователя по идентификатору")
    @ApiResponse(responseCode = "200", description = "Успешно получен пользователь")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId){
        UserDTO userDTO = userService.getUserById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userDTO);
    }
    @GetMapping("/other-users")
    @Operation(summary = "Получение всех пользователей, кроме текущего")
    @ApiResponse(responseCode = "200", description = "Успешно получены пользователи")
    public ResponseEntity<List<UserDTO>> getAllUsersExceptCurrent(Authentication authentication) {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Long userId = myUserDetails.getUser().getId();
        List<UserDTO> users = userService.getAllUsersExceptCurrentUser(userId);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/update")
    @Operation(summary = "Обновление данных пользователя")
    @ApiResponse(responseCode = "200", description = "Данные пользователя успешно обновлены")
    public ResponseEntity<?> updateUser(Authentication authentication, @Valid @RequestBody UserDTO userDTO) {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Long userId = myUserDetails.getUser().getId();
        userService.deleteUser(userId);

        UserDTO existingUser = userService.getUserById(userId);

        // Примените изменения только к определенным полям
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
    public ResponseEntity<?> deleteUser(Authentication authentication) {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Long userId = myUserDetails.getUser().getId();
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/password")
    @Operation(summary = "Изменение пароля пользователя")
    @ApiResponse(responseCode = "200", description = "Пароль успешно изменен")
    public ResponseEntity<UserDTO> changePassword(Authentication authentication, @RequestBody ChangePasswordDTO changePasswordDTO) {
        // Проверка текущего пароля
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Long userId = myUserDetails.getUser().getId();
        Optional<User> userOptional = userRepository.findById(userId);
        User user = userOptional.get();

        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPassword())) {
            throw new IncorrectPasswordException("Неверный текущий пароль");
        }

        // Вызов сервисного метода для изменения пароля
        UserDTO updatedUserDTO = userService.changePassword(userId, changePasswordDTO.getNewPassword());

        return ResponseEntity.status(HttpStatus.OK).body(updatedUserDTO);
    }


}
