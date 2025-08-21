package com.teste_mv.user_service.Unit.Service;

import com.teste_mv.user_service.DTO.TaskDTO;
import com.teste_mv.user_service.Entities.User;
import com.teste_mv.user_service.Exceptions.UserAlreadyExistsException;
import com.teste_mv.user_service.Exceptions.UserHasTasksException;
import com.teste_mv.user_service.Exceptions.UserNotFoundException;
import com.teste_mv.user_service.Feign.TaskClient;
import com.teste_mv.user_service.Repository.UserRepository;
import com.teste_mv.user_service.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskClient taskClient;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setName("Carlos");
        user.setEmail("carlos@test.com");
    }

    @Test
    void createUser_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        User created = userService.createUser(user);

        assertEquals(user.getName(), created.getName());
        assertEquals(user.getEmail(), created.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void createUser_EmailAlreadyExists_ThrowsException() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(user));

        assertTrue(exception.getMessage().contains("Já existe um usuário cadastrado"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getAllUsers_ReturnsList() {
        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals(user.getEmail(), result.get(0).getEmail());
    }

    @Test
    void getUserById_UserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertEquals(user.getName(), result.getName());
    }

    @Test
    void getUserById_UserNotFound_ThrowsException() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> userService.getUserById(2L));

        assertTrue(exception.getMessage().contains("Usuário não encontrado"));
    }

    @Test
    void updateUser_Success() {
        User updated = new User();
        updated.setName("Carlos Eduardo");
        updated.setEmail("carlos.edu@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updated);

        User result = userService.updateUser(1L, updated);

        assertEquals("Carlos Eduardo", result.getName());
        assertEquals("carlos.edu@test.com", result.getEmail());
    }

    @Test
    void updateUser_UserNotFound_ThrowsException() {
        User updated = new User();
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> userService.updateUser(2L, updated));

        assertTrue(exception.getMessage().contains("Usuário não encontrado"));
    }

    @Test
    void deleteUser_NoTasks_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskClient.getTasksByUserId(1L)).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUser_HasTasks_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskClient.getTasksByUserId(1L))
                .thenReturn(List.of(new TaskDTO("teste", "Teste tarefa", "Finalizada")));

        Exception exception = assertThrows(UserHasTasksException.class, () -> userService.deleteUser(1L));

        assertTrue(exception.getMessage().contains("Usuário não pode ser deletado"));
        verify(userRepository, never()).delete(any());
    }


    @Test
    void deleteUser_UserNotFound_ThrowsException() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.deleteUser(2L));

        assertTrue(exception.getMessage().contains("Usuário não encontrado"));
        verify(userRepository, never()).delete(any());
    }

}
