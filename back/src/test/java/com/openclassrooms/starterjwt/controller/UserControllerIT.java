package com.openclassrooms.starterjwt.controller;

import com.jayway.jsonpath.JsonPath;
import com.openclassrooms.starterjwt.controllers.UserController;
import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserControllerIT {

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private final String id = "10";

    @BeforeEach
    void setUp(){
        userService = new UserService(userRepository);
        userController = new UserController(userService,userMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    @DisplayName("should return a user")
    void giveIDUser_thenFindUserById_shouldReturnUserDto() throws Exception {
        User user = new User();
        user.setEmail("test@test.fr");
        user.setLastName("test");
        user.setFirstName("test");

        UserDto expectedUserDto = userMapper.toDto(user);
        when(userRepository.findById(Long.parseLong(id))).thenReturn(Optional.of(user));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(expectedUserDto.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(expectedUserDto.getLastName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(expectedUserDto.getFirstName()));
        verify(userRepository).findById(Long.parseLong(id));
    }

    @Test
    @DisplayName("should not found user")
    void giveIDUser_thenFindUserById_shouldNotFoundUser() throws Exception {
        when(userRepository.findById(Long.parseLong(id))).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        verify(userRepository).findById(Long.parseLong(id));
    }


    @Test
    @DisplayName("should throw bad request error")
    void giveIDUser_thenFindUserById_shouldThrowBadRequestError() throws Exception {
        when(userRepository.findById(Long.parseLong(id))).thenThrow(NumberFormatException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(userRepository).findById(Long.parseLong(id));
    }

    @Test
    @WithUserDetails("test@test.fr")
    @DisplayName("should delete a user")
    void giveIDUser_thenFindUserById_shouldDeleteUser() throws Exception {
        User user = new User();
        user.setEmail("test@test.fr");

        when(userRepository.findById(Long.parseLong(id))).thenReturn(Optional.of(user));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(userRepository).findById(Long.parseLong(id));
    }

    @Test
    @DisplayName("should return not found status")
    void giveIDUser_thenFindUserById_shouldReturnNotFoundStatus() throws Exception {
        when(userRepository.findById(Long.parseLong(id))).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(userRepository).findById(Long.parseLong(id));
    }

    @Test
    @WithUserDetails("test@test.fr")
    @DisplayName("should return unauthorized status")
    void giveIDUser_thenFindUserById_shouldReturnUnauthorizedStatus() throws Exception {
        User user = new User();
        user.setEmail("t@test.fr");

        when(userRepository.findById(Long.parseLong(id))).thenReturn(Optional.of(user));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        verify(userRepository).findById(Long.parseLong(id));
    }

    @Test
    @DisplayName("should return bad request status")
    void giveIDUser_thenFindUserById_shouldReturnBadRequestStatus() throws Exception {
        when(userRepository.findById(Long.parseLong(id))).thenThrow(NumberFormatException.class);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(userRepository).findById(Long.parseLong(id));
    }
}
