package ru.adel.socialmedia.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.adel.socialmedia.dto.AuthenticationRequest;
import ru.adel.socialmedia.dto.RegistrationRequest;
import ru.adel.socialmedia.dto.UserDTO;
import ru.adel.socialmedia.security.jwt.JWTUtil;
import ru.adel.socialmedia.services.RegistrationService;
import ru.adel.socialmedia.util.exception.UnauthorizedException;
import ru.adel.socialmedia.util.response.JWTResponse;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Аутентификация и авторизация")
public class AuthController {
    private final RegistrationService registrationService;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;


    public AuthController(RegistrationService registrationService, JWTUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.registrationService = registrationService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/registration")
    @Operation(summary = "Регистрация пользователя")
    @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован")
    public ResponseEntity<JWTResponse> performRegistration(@RequestBody @Valid RegistrationRequest registrationRequest) {
        UserDTO registeredUser = registrationService.registerUser(registrationRequest);
        String token = jwtUtil.generateToken(registeredUser.getUsername());
        JWTResponse response = new JWTResponse(token);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/login")
    @Operation(summary = "Аутентификация пользователя")
    @ApiResponse(responseCode = "200", description = "Успешная аутентификация")
    public ResponseEntity<JWTResponse> performLogin(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Неверно введен логин или пароль");
        }
        String token = jwtUtil.generateToken(authenticationRequest.getUsername());
        JWTResponse response = new JWTResponse(token);
        return ResponseEntity.ok(response);
    }

}
