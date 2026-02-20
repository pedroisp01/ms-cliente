package com.desafio.ms_cliente.service;

import com.desafio.ms_cliente.dto.AtualizarClienteDTO;
import com.desafio.ms_cliente.dto.ClienteDTO;
import com.desafio.ms_cliente.exception.BusinessException;
import com.desafio.ms_cliente.mapper.ClienteMapper;
import com.desafio.ms_cliente.model.Cliente;
import com.desafio.ms_cliente.model.enums.StatusCliente;
import com.desafio.ms_cliente.repository.ClienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @Mock
    private ClienteMapper mapper;

    @Mock
    private ClienteProducer producer;

    @InjectMocks
    private ClienteService service;

    @Test
    @DisplayName("Deve salvar um cliente com status PENDENTE")
    void deveSalvarClienteComSucesso() {
        ClienteDTO dto = new ClienteDTO();
        Cliente entity = new Cliente();

        when(mapper.toEntity(any())).thenReturn(entity);
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toDTO(any())).thenReturn(dto);

        service.salvar(dto);

        assertEquals(StatusCliente.PENDENTE, entity.getStatus());
        verify(producer, times(1)).enviarMensagem(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao tentar salvar CPF duplicado")
    void deveLancarErroAoSalvarCpfDuplicado() {
        ClienteDTO dto = new ClienteDTO();
        dto.setCpf("11122233344");

        // Inserir lógica para evitar NPE antes da validação
        when(mapper.toEntity(any())).thenReturn(new Cliente());
        when(repository.existsByCpf(anyString())).thenReturn(true);

        assertThrows(BusinessException.class, () -> service.salvar(dto));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar lista de todos os clientes")
    void deveListarTodos() {
        when(repository.findAll()).thenReturn(List.of(new Cliente(), new Cliente()));
        when(mapper.toDTO(any())).thenReturn(new ClienteDTO());

        List<ClienteDTO> resultado = service.listarTodos();

        assertEquals(2, resultado.size());
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Deve buscar cliente por ID com sucesso")
    void deveBuscarPorId() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(cliente));
        when(mapper.toDTO(cliente)).thenReturn(new ClienteDTO());

        ClienteDTO resultado = service.buscarPorId(1L);

        assertNotNull(resultado);
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Deve atualizar apenas o nome do cliente (Atualização Parcial)")
    void deveAtualizarParcialmente() {
        // Arrange
        Long id = 1L;
        Cliente clienteExistente = new Cliente();
        clienteExistente.setNome("Nome Antigo");
        clienteExistente.setCpf("123");

        AtualizarClienteDTO dto = new AtualizarClienteDTO();
        dto.setNome("Nome Novo"); // Só enviamos o nome

        when(repository.findById(id)).thenReturn(Optional.of(clienteExistente));
        when(repository.save(any())).thenReturn(clienteExistente);
        when(mapper.toDTO(any())).thenReturn(new ClienteDTO());

        // Act
        service.atualizar(id, dto);

        // Assert
        assertEquals("Nome Novo", clienteExistente.getNome());
        assertEquals("123", clienteExistente.getCpf()); // CPF deve continuar o mesmo
        verify(repository).save(clienteExistente);
    }

    @Test
    @DisplayName("Deve deletar cliente com sucesso quando for ADMIN")
    void deveDeletarComSucesso() {
        when(repository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> service.deletar(1L));

        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar erro ao deletar cliente inexistente")
    void deveErroAoDeletarInexistente() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThrows(BusinessException.class, () -> service.deletar(1L));
        verify(repository, never()).deleteById(any());
    }
}