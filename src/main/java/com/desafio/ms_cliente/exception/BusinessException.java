package com.desafio.ms_cliente.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;

    public BusinessException(String mensagem) {
        super(mensagem);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public BusinessException(String mensagem, HttpStatus status) {
        super(mensagem);
        this.status = status;
    }
}