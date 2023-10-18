package com.openclassrooms.starterjwt.controller;

import com.openclassrooms.starterjwt.controllers.TeacherController;
import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.services.TeacherService;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class TeacherControllerIT {

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private TeacherService teacherService;

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherController teacherController;

    private final String id = "10";

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        teacherService = new TeacherService(teacherRepository);
        teacherController = new TeacherController(teacherService, teacherMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(teacherController).build();
    }

    @Test
    @DisplayName("should return a Teacher")
    void giveIDTeacher_thenFindTeacherById_shouldReturnATeacherDTO() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setFirstName("valery");
        teacher.setLastName("Dupont");
        TeacherDto expectedTeacherDto = teacherMapper.toDto(teacher);
        when(teacherRepository.findById(Long.parseLong(id))).thenReturn(Optional.of(teacher));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/teacher/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(expectedTeacherDto.getLastName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(expectedTeacherDto.getFirstName()));

        verify(teacherRepository).findById(Long.parseLong(id));
    }


    @Test
    @DisplayName("should return not found status")
    void giveIDTeacher_thenFindTeacherById_shouldReturnNotFoundStatus() throws Exception {
        when(teacherRepository.findById(Long.parseLong(id))).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/teacher/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(teacherRepository).findById(Long.parseLong(id));
    }

    @Test
    @DisplayName("should return bad request status")
    void giveIDTeacher_thenFindTeacherById_shouldReturnBadRequestStatus() throws Exception {
        when(teacherRepository.findById(Long.parseLong(id))).thenThrow(NumberFormatException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/teacher/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(teacherRepository).findById(Long.parseLong(id));
    }


    @Test
    @DisplayName("should return all teacher")
    void giveIDTeacher_thenFindTeacherById_shouldReturnAllTeacher() throws Exception {
        List<Teacher> teacher = List.of(new Teacher().setFirstName("Valery").setLastName("Dupont"),
                new Teacher().setFirstName("Thomas").setLastName("Lagrange"));

        when(teacherRepository.findAll()).thenReturn(teacher);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/teacher"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].firstName").value("Valery"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].lastName").value("Lagrange"));

        verify(teacherRepository).findAll();
    }
}
