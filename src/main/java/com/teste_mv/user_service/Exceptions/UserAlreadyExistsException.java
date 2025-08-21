package com.teste_mv.user_service.Exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super("Já existe um usuário cadastrado com o e-mail: " + email);
    }
}




