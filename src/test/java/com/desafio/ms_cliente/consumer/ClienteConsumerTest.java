package com.desafio.ms_cliente.consumer;

import com.desafio.ms_cliente.dto.ClienteDTO;
import com.desafio.ms_cliente.model.Cliente;
import com.desafio.ms_cliente.model.enums.StatusCliente;
import com.desafio.ms_cliente.repository.ClienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteConsumerTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteConsumer consumer;

    @Test
    @DisplayName("Deve alterar status para ATIVO ao consumir mensagem com sucesso")
    void deveAtivarClienteAoConsumir() {
        // Arrange
        ClienteDTO dto = new ClienteDTO();
        dto.setId(1L);
        dto.setNome("Pedro");

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setStatus(StatusCliente.PENDENTE);

        when(repository.findById(1L)).thenReturn(Optional.of(cliente));

        // Act
        consumer.consumir(dto);

        // Assert
        assertEquals(StatusCliente.ATIVO, cliente.getStatus());
        verify(repository, times(1)).save(cliente);
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Não deve realizar ação se o cliente não for encontrado no banco")
    void naoDeveFazerNadaQuandoClienteNaoExiste() {
        // Arrange
        ClienteDTO dto = new ClienteDTO();
        dto.setId(99L);

        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act
        consumer.consumir(dto);

        // Assert
        verify(repository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve relançar exceção para acionar o mecanismo de retry/DLQ do RabbitMQ")
    void deveRelancarExcecaoQuandoOcorreErroNoBanco() {
        // Arrange
        ClienteDTO dto = new ClienteDTO();
        dto.setId(1L);

        when(repository.findById(1L)).thenThrow(new RuntimeException("Erro de conexão com banco"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> consumer.consumir(dto));
        verify(repository, never()).save(any());
    }
}