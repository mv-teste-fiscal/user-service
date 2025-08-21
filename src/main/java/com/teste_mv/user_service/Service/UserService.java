package com.teste_mv.user_service.Service;

import com.teste_mv.user_service.DTO.TaskDTO;
import com.teste_mv.user_service.Entities.User;
import com.teste_mv.user_service.Feign.TaskClient;
import com.teste_mv.user_service.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskClient taskClient;

    public User createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Já existe um usuário cadastrado com este e-mail: " + user.getEmail());
        }

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id " + id));
    }

    public User updateUser(Long id, User userUpdated) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id " + id));

        user.setName(userUpdated.getName());
        user.setEmail(userUpdated.getEmail());

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id " + id));

        List<TaskDTO> tasks = taskClient.getTasksByUserId(id);

        if (tasks.isEmpty()) {
            userRepository.delete(user);
        }else {
            throw new RuntimeException("Usuário não pode ser deleteado pois possui tarefas associadas ");
        }
    }
}
