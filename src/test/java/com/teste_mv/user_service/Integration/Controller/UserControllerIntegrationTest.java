package com.teste_mv.user_service.Integration.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teste_mv.user_service.DTO.TaskDTO;
import com.teste_mv.user_service.Entities.User;
import com.teste_mv.user_service.Feign.TaskClient;
import com.teste_mv.user_service.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskClient taskClient;

    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        user = new User();
        user.setName("Carlos");
        user.setEmail("carlos@test.com");
        user = userRepository.save(user);
    }

    @Test
    void getAllUsers_ShouldReturnList() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Carlos")));
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        mockMvc.perform(get("/user/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("carlos@test.com")));
    }

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        User newUser = new User();
        newUser.setName("Maria");
        newUser.setEmail("maria@test.com");

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Maria")))
                .andExpect(jsonPath("$.email", is("maria@test.com")));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        user.setName("Carlos Eduardo");
        user.setEmail("carlos.edu@test.com");

        mockMvc.perform(put("/user/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Carlos Eduardo")))
                .andExpect(jsonPath("$.email", is("carlos.edu@test.com")));
    }

    @Test
    void deleteUser_ShouldReturnSuccessMessage() throws Exception {

        TaskDTO taskDTO = new TaskDTO("teste", "Teste tarefa", "Finalizada");
        when(taskClient.getTasksByUserId(1L)).thenReturn(Collections.singletonList(taskDTO));

        mockMvc.perform(delete("/user/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Usuário deletado com sucesso.")));

        Optional<User> deleted = userRepository.findById(user.getId());
        assert(deleted.isEmpty());
    }
}
