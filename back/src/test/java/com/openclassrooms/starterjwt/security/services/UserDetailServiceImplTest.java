package com.openclassrooms.starterjwt.security.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserDetailServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    public void setup(){
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    @DisplayName("should not found user")
    public void giveUserEmail_thenLoadUserByUsername_shouldThrowUsernameNotFoundException() {
        String email = "test@test.fr";
        when(userRepository.findByEmail(email)).thenThrow(UsernameNotFoundException.class);

        assertThrows(UsernameNotFoundException.class, () -> userRepository.findByEmail(email));
    }

    @Test
    @DisplayName("should return UserDetailsImpl object")
    public void giveUserEmail_thenLoadUserByUsername_shouldReturnUserDetailsImplObject() {
        String email = "test@test.fr";
        User user = new User();
        user.setId(1L).setEmail(email).setLastName("Delore").setFirstName("Victore").setPassword("test");
        UserDetails userDetails = UserDetailsImpl
                .builder()
                .id(user.getId())
                .username(user.getEmail())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .password(user.getPassword())
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails newUserDetail = userDetailsService.loadUserByUsername(email);

        assertEquals(userDetails, newUserDetail);
    }
}
