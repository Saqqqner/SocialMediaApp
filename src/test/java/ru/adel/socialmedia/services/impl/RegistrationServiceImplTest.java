package ru.adel.socialmedia.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.adel.socialmedia.dto.RegistrationRequest;
import ru.adel.socialmedia.dto.UserDTO;
import ru.adel.socialmedia.models.User;
import ru.adel.socialmedia.models.UserRole;
import ru.adel.socialmedia.repositories.UserRepository;
import ru.adel.socialmedia.util.exception.DuplicateUserException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    @Mock
    private ModelMapper modelMapper;

    private RegistrationRequest registrationRequest;
    private UserDTO userDTO;
    private User user;
    private UserDTO actualUserDTO;

    @BeforeEach
    public void setup() {
        registrationRequest = new RegistrationRequest();
        registrationRequest.setEmail("test@example.com");
        registrationRequest.setUsername("testuser");
        registrationRequest.setPassword("password");

        userDTO = new UserDTO();
        userDTO.setEmail(registrationRequest.getEmail());
        userDTO.setUsername(registrationRequest.getUsername());

        user = new User();
        user.setEmail(registrationRequest.getEmail());
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(registrationRequest.getPassword());
        actualUserDTO = new UserDTO();
        actualUserDTO.setEmail(user.getEmail());
        actualUserDTO.setUsername(user.getUsername());


    }

    @Test
    public void testRegisterUser() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(modelMapper.map(user, UserDTO.class)).thenReturn(actualUserDTO);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(registrationRequest, User.class)).thenReturn(user);

        actualUserDTO = registrationService.registerUser(registrationRequest);

        assertEquals(userDTO.getEmail(), actualUserDTO.getEmail());
        assertEquals(userDTO.getUsername(), actualUserDTO.getUsername());

        verify(userRepository).save(argThat(savedUser -> {
            assertEquals(registrationRequest.getEmail(), savedUser.getEmail());
            assertEquals(registrationRequest.getUsername(), savedUser.getUsername());
            assertEquals("encodedPassword", savedUser.getPassword());
            assertEquals(UserRole.ROLE_USER, savedUser.getRole());
            return true;
        }));
    }

    @Test
    public void testRegisterUser_UserExists() {
        String username = registrationRequest.getUsername();
        when(userRepository.existsByUsername(username)).thenReturn(true);

        assertThrows(DuplicateUserException.class, () -> {
            registrationService.registerUser(registrationRequest);
        });

        verify(userRepository).existsByUsername(username);
        verify(userRepository, never()).save(any(User.class));
    }


}
