package ru.adel.SocialMediaApp.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.adel.SocialMediaApp.util.exception.*;
import ru.adel.SocialMediaApp.util.response.ErrorResponse;
import ru.adel.SocialMediaApp.util.response.ValidationError;
import ru.adel.SocialMediaApp.util.response.ValidationErrorResponse;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RestExceptionHandlerTest {
    @InjectMocks
    private RestExceptionHandler restExceptionHandler;

    @Test
    public void handleUnauthorizedException_ReturnsUnauthorizedResponse() {
        UnauthorizedException ex = new UnauthorizedException("Unauthorized");
        ResponseEntity<ErrorResponse> response = restExceptionHandler.handleUnauthorizedException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertEquals(HttpStatus.UNAUTHORIZED, errorResponse.getStatus());
        assertEquals("Unauthorized", errorResponse.getMessage());
    }

    @Test
    public void handleIncorrectPasswordException_ReturnsUnauthorizedResponse() {
        IncorrectPasswordException ex = new IncorrectPasswordException("Incorrect password");
        ResponseEntity<ErrorResponse> response = restExceptionHandler.handleIncorrectPasswordException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertEquals(HttpStatus.UNAUTHORIZED, errorResponse.getStatus());
        assertEquals("Incorrect password", errorResponse.getMessage());
    }

    @Test
    public void handleDuplicateUserException_ReturnsBadRequestResponse() {
        DuplicateUserException ex = new DuplicateUserException("User already exists");
        ResponseEntity<ErrorResponse> response = restExceptionHandler.handleDuplicateUserException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatus());
        assertEquals("User already exists", errorResponse.getMessage());
    }

    @Test
    public void handleBadRequestException_ReturnsBadRequestResponse() {
        BadRequestException ex = new BadRequestException("Bad request");
        ResponseEntity<ErrorResponse> response = restExceptionHandler.handleBadRequestException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatus());
        assertEquals("Bad request", errorResponse.getMessage());
    }

    @Test
    public void handleUserNotFoundException_ReturnsNotFoundResponse() {
        UserNotFoundException ex = new UserNotFoundException("User not found");
        ResponseEntity<ErrorResponse> response = restExceptionHandler.handleUsernameNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertEquals(HttpStatus.NOT_FOUND, errorResponse.getStatus());
        assertEquals("User not found", errorResponse.getMessage());
    }

    @Test
    public void handleInternalServerException_ReturnsInternalServerErrorResponse() {
        InternalServerException ex = new InternalServerException("Internal server error");
        ResponseEntity<ErrorResponse> response = restExceptionHandler.handleInternalServerException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse.getStatus());
        assertEquals("Internal server error", errorResponse.getMessage());
    }




}