package com.desafio.ms_cliente.dto;

import com.desafio.ms_cliente.model.enums.StatusCliente;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO implements Serializable {

    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "O CPF é obrigatório")
    private String cpf;

    @Enumerated(EnumType.STRING)
    private StatusCliente status;

}