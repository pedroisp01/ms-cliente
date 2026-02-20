package com.desafio.ms_cliente.model;

import com.desafio.ms_cliente.model.enums.StatusCliente;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
@Data // Se usar Lombok
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @Email(message = "Email inválido")
    private String email;

    @Column(unique = true)
    private String cpf;

    @Enumerated(EnumType.STRING)
    private StatusCliente status;
}