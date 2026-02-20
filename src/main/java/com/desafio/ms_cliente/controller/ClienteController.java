package com.desafio.ms_cliente.controller;

import com.desafio.ms_cliente.dto.AtualizarClienteDTO;
import com.desafio.ms_cliente.dto.ClienteDTO;
import com.desafio.ms_cliente.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ClienteDTO> criar(@Valid @RequestBody ClienteDTO cliente) {
        return ResponseEntity.ok(service.salvar(cliente));
    }

    @GetMapping
    public List<ClienteDTO> listar() {
        return service.listarTodos();
    }

    @GetMapping("/buscar")
    public ResponseEntity<ClienteDTO> buscar(@NotNull @RequestParam("id") Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/alterar")
    public ResponseEntity<ClienteDTO> alterar(
            @NotNull @RequestParam("id") Long id,
            @Valid @RequestBody AtualizarClienteDTO clienteAtualizado) {

        return ResponseEntity.ok(service.atualizar(id, clienteAtualizado));
    }

    @DeleteMapping("/apagar")
    public ResponseEntity<Void> excluir(@NotNull @RequestParam("id") Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}