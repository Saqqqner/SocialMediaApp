package ru.adel.SocialMediaApp.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.adel.SocialMediaApp.dto.AuthenticationRequest;
import ru.adel.SocialMediaApp.dto.RegistrationRequest;
import ru.adel.SocialMediaApp.dto.UserDTO;
import ru.adel.SocialMediaApp.security.jwt.JWTUtil;
import ru.adel.SocialMediaApp.services.impl.RegistrationServiceImpl;
import ru.adel.SocialMediaApp.util.exception.BadRequestException;
import ru.adel.SocialMediaApp.util.exception.UnauthorizedException;
import ru.adel.SocialMediaApp.util.response.JWTResponse;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
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
    public ResponseEntity<?> performRegistration(  @RequestBody RegistrationRequest registrationRequest ) {
            UserDTO registeredUser = registrationService.registerUser(registrationRequest);
            String token = jwtUtil.generateToken(registeredUser.getUsername());

            JWTResponse response = new JWTResponse(token);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }



    @PostMapping("/login")
    public ResponseEntity<JWTResponse> performLogin(  @RequestBody AuthenticationRequest authenticationRequest){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Неверно введен логин или пароль");
        } String token = jwtUtil.generateToken(authenticationRequest.getUsername());
        JWTResponse response = new JWTResponse(token);
        return ResponseEntity.ok(response);
    }
}
