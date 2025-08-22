package com.teste_mv.user_service.Service;

import com.teste_mv.user_service.DTO.TaskDTO;
import com.teste_mv.user_service.Entities.User;
import com.teste_mv.user_service.Exceptions.UserAlreadyExistsException;
import com.teste_mv.user_service.Exceptions.UserHasTasksException;
import com.teste_mv.user_service.Exceptions.UserNotFoundException;
import com.teste_mv.user_service.Feign.TaskClient;
import com.teste_mv.user_service.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TaskClient taskClient;

    @Autowired
    public UserService(UserRepository userRepository, TaskClient taskClient) {
        this.userRepository = userRepository;
        this.taskClient = taskClient;
    }

    public User createUser(User user) {
        userRepository.findByEmail(user.getEmail())
                .ifPresent(u -> { throw new UserAlreadyExistsException(user.getEmail()); });

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User updateUser(Long id, User updatedUser) {
        User user = getUserById(id);
        if(!updatedUser.getEmail().equals(user.getEmail())) {
            userRepository.findByEmail(updatedUser.getEmail())
                    .ifPresent(u -> {
                        throw new UserAlreadyExistsException(user.getEmail());
                    });

            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);

        List<TaskDTO> tasks = taskClient.getTasksByUserId(id);
        if (!tasks.isEmpty()) {
            throw new UserHasTasksException(id);
        }

        userRepository.delete(user);
    }
}
