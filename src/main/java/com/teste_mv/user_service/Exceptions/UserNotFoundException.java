package com.teste_mv.user_service.Exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("Usuário não encontrado com id: " + id);
    }
}