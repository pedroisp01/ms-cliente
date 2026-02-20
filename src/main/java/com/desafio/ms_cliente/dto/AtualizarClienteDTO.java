package com.desafio.ms_cliente.dto;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
public class AtualizarClienteDTO {

    private String nome;

    @Email(message = "Se informado, o email deve ser válido")
    private String email;

    @Pattern(regexp = "\\d{11}", message = "Se informado, o CPF deve conter 11 dígitos")
    private String cpf;
}