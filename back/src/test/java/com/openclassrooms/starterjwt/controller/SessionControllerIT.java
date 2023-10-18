package com.openclassrooms.starterjwt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.controllers.SessionController;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.SessionService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class SessionControllerIT {

    @Autowired
    private SessionMapper sessionMapper;

    @Autowired
    private SessionService sessionService;

    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionController sessionController;

    private MockMvc mockMvc;

    private final String id = "10";

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        sessionService = new SessionService(sessionRepository, userRepository);
        sessionController = new SessionController(sessionService, sessionMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(sessionController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("should return a session")
    void giveIDSession_thenFindSessionById_shouldReturnASession() throws Exception {
        Session session = new Session();
        session.setName("Yoga");

        when(sessionRepository.findById(Long.parseLong(id))).thenReturn(Optional.of(session));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/session/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Yoga"));

        verify(sessionRepository).findById(Long.parseLong(id));

    }

    @Test
    @DisplayName("should return not found status")
    void giveIDSession_thenFindSessionById_shouldNotFoundStatus() throws Exception {
        when(sessionRepository.findById(Long.parseLong(id))).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/session/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(sessionRepository).findById(Long.parseLong(id));
    }

    @Test
    @DisplayName("should return bad request status")
    void giveIDSession_thenFindSessionById_shouldBadRequestStatus() throws Exception {
        when(sessionRepository.findById(Long.parseLong(id))).thenThrow(NumberFormatException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/session/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(sessionRepository).findById(Long.parseLong(id));
    }

    @Test
    @DisplayName("should return all session")
    void giveIDSession_thenFindSessionById_shouldReturnAllSession() throws Exception {
        List<Session> session = List.of(new Session().setName("Yoga"),
                new Session().setName("Zen"));

        when(sessionRepository.findAll()).thenReturn(session);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/session"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Yoga"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Zen"));

        verify(sessionRepository).findAll();
    }

    @Test
    @DisplayName("should create a session")
    void giveIDSession_thenFindSessionById_shouldCreateSession() throws Exception {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Test");
        sessionDto.setDate(new Date());
        sessionDto.setDescription("Description session");
        sessionDto.setTeacher_id(1L);
        Session session = sessionMapper.toEntity(sessionDto);
        ObjectMapper objectMapper = new ObjectMapper();
        when(sessionRepository.save(session)).thenReturn(session);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(session.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(session.getDescription()));

        verify(sessionRepository).save(session);
    }

    @Test
    @DisplayName("should update a session")
    void giveIDSession_thenFindSessionById_shouldUpdateSession() throws Exception {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Yoga");
        sessionDto.setDate(new Date());
        sessionDto.setDescription("Description session");
        sessionDto.setTeacher_id(1L);
        sessionDto.setId(Long.parseLong(id));

        Session session = sessionMapper.toEntity(sessionDto);

        when(sessionRepository.save(session)).thenReturn(session);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/session/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(id)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(sessionDto.getName()));

        verify(sessionRepository).save(session);
    }

    @Test
    @DisplayName("should update session and return bad request status")
    void giveIDSession_thenUpdateSession_shouldBadRequestStatus() throws Exception{
        Session session;
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Yoga");
        sessionDto.setDate(new Date());
        sessionDto.setDescription("Description session");
        sessionDto.setTeacher_id(1L);
        sessionDto.setId(Long.parseLong(id));

        session = sessionMapper.toEntity(sessionDto);

        when(sessionRepository.save(session)).thenThrow(NumberFormatException.class);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/session/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(id)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(sessionRepository).save(session);
    }

    @Test
    @DisplayName("should delete a session")
    void giveIDSession_thenFindSessionById_shouldDeleteSession() throws Exception {
        Session session = new Session();

        when(sessionRepository.findById(Long.parseLong(id))).thenReturn(Optional.of(session));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/session/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(sessionRepository).findById(Long.parseLong(id));

    }

    @Test
    @DisplayName("should delete session and return not found status")
    void giveIDSession_thenFindSessionById_shouldReturnNotFoundStatus() throws Exception {
        when(sessionRepository.findById(Long.parseLong(id))).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/session/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(sessionRepository).findById(Long.parseLong(id));
    }

    @Test
    @DisplayName("should delete session and return bad request status")
    void giveIDSession_thenFindSessionById_shouldReturnBadRequestStatus() throws Exception {
        when(sessionRepository.findById(Long.parseLong(id))).thenThrow(NumberFormatException.class);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/session/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(sessionRepository).findById(Long.parseLong(id));
    }

    @Test
    @DisplayName("should participate at a session")
    void giveIDSession_thenUserParticipateToASession_shouldParticipate() throws Exception {
        Session session = new Session();
        User user = new User();
        session.setId(Long.parseLong(id));
        session.setUsers(new ArrayList<>());

        when(sessionRepository.findById(Long.parseLong(id))).thenReturn(Optional.of(session));
        when(userRepository.findById(Long.parseLong(id))).thenReturn(Optional.of(user));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/session/{id}/participate/{userId}", id, id))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(sessionRepository).findById(Long.parseLong(id));
        verify(userRepository).findById(Long.parseLong(id));
        assert(session.getUsers().contains(user));
    }

    @Test
    @DisplayName("should participate at a session but return bad request status")
    void giveIDSession_thenUserParticipateToASession_shouldReturnBadRequestStatus() throws Exception {
        when(sessionRepository.findById(Long.parseLong(id))).thenThrow(NumberFormatException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/session/{id}/participate/{userId}", id, id))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(sessionRepository).findById(Long.parseLong(id));
    }

    @Test
    @DisplayName("should unParticipate at a session but return bad request status")
    void giveIDSession_thenUserUnparticipateToASession_shouldReturnBadRequestStatus() throws Exception {
        when(sessionRepository.findById(Long.parseLong(id))).thenThrow(NumberFormatException.class);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/session/{id}/participate/{userId}", id, id))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(sessionRepository).findById(Long.parseLong(id));
    }


    @Test
    @DisplayName("should unparticipate at a session")
    void giveIDSession_thenUserUnparticipateToASession_shouldUnParticipate() throws Exception {
        Session session = new Session();
        User user = new User();
        user.setId(Long.parseLong(id));
        session.setId(Long.parseLong(id));
        session.setUsers(List.of(user));
        when(sessionRepository.findById(Long.parseLong(id))).thenReturn(Optional.of(session));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/session/{id}/participate/{userId}", id, id))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(sessionRepository).findById(Long.parseLong(id));

        assert(!session.getUsers().contains(user));
    }
}
