package com.desafio.ms_cliente.consumer;

import com.desafio.ms_cliente.config.RabbitMQConfig;
import com.desafio.ms_cliente.dto.ClienteDTO;
import com.desafio.ms_cliente.exception.BusinessException;
import com.desafio.ms_cliente.model.enums.StatusCliente;
import com.desafio.ms_cliente.repository.ClienteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class ClienteConsumer {

    private final ClienteRepository repository;

    public ClienteConsumer(ClienteRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    @Transactional
    public void consumir(ClienteDTO clienteDto) {
        log.info("[CONSUMER] Iniciando ativação para o cliente: {}", clienteDto.getNome());

        try {
            repository.findById(clienteDto.getId()).ifPresent(cliente -> {
                cliente.setStatus(StatusCliente.ATIVO);
                repository.save(cliente);

                log.info("[CONSUMER] Cliente {} ativado com sucesso.", cliente.getNome());
            });

        } catch (Exception e) {
            log.error("[ERRO] Falha ao processar ativação do cliente {}: {}", clienteDto.getNome(), e.getMessage());
            throw e;
        }
    }
}