package com.desafio.ms_cliente.service;

import com.desafio.ms_cliente.config.RabbitMQConfig;
import com.desafio.ms_cliente.dto.ClienteDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClienteProducer {

    private final RabbitTemplate rabbitTemplate;

    public ClienteProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Async
    public void enviarMensagem(ClienteDTO payload) {
        try {
            log.info("Postando JSON do cliente na fila: {}", payload.getNome());
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.QUEUE_NAME,
                    payload
            );
        } catch (Exception e) {
            log.error("Erro ao enviar DTO para a fila", e);
        }
    }
}
