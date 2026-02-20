package com.desafio.ms_cliente.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String erro;
    private String mensagem;
    private List<CampoErro> campos;

    @Data
    @AllArgsConstructor
    public static class CampoErro {
        private String nome;
        private String mensagem;
    }
}
