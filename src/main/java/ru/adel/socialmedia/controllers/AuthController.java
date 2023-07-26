package ru.adel.socialmedia.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
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
import ru.adel.socialmedia.services.impl.RegistrationServiceImpl;
import ru.adel.socialmedia.util.exception.UnauthorizedException;
import ru.adel.socialmedia.util.response.JWTResponse;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Аутентификация и авторизация")
public class AuthController {
    private final RegistrationServiceImpl registrationService;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;


    public AuthController(RegistrationServiceImpl registrationService, JWTUtil jwtUtil, AuthenticationManager authenticationManager, ModelMapper modelMapper) {
        this.registrationService = registrationService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/registration")
    @Operation(summary = "Регистрация пользователя")
    @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован")
    public ResponseEntity<?> performRegistration(@RequestBody RegistrationRequest registrationRequest) {
        UserDTO registeredUser = registrationService.registerUser(registrationRequest);
        String token = jwtUtil.generateToken(registeredUser.getUsername());

        JWTResponse response = new JWTResponse(token);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/login")
    @Operation(summary = "Аутентификация пользователя")
    @ApiResponse(responseCode = "200", description = "Успешная аутентификация")
    public ResponseEntity<JWTResponse> performLogin(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Неверно введен логин или пароль");
        }
        String token = jwtUtil.generateToken(authenticationRequest.getUsername());
        JWTResponse response = new JWTResponse(token);
        return ResponseEntity.ok(response);
    }

}
