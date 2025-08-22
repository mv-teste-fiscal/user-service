package com.teste_mv.user_service.Exceptions;

public class UserHasTasksException extends RuntimeException {
    public UserHasTasksException(Long id) {
        super("Usuário não pode ser deletado pois possui tarefas associadas. ");
    }
}