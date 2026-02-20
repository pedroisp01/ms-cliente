package com.desafio.ms_cliente.service;

import com.desafio.ms_cliente.config.RabbitMQConfig;
import com.desafio.ms_cliente.dto.AtualizarClienteDTO;
import com.desafio.ms_cliente.dto.ClienteDTO;
import com.desafio.ms_cliente.exception.BusinessException;
import com.desafio.ms_cliente.mapper.ClienteMapper;
import com.desafio.ms_cliente.model.Cliente;
import com.desafio.ms_cliente.model.enums.StatusCliente;
import com.desafio.ms_cliente.repository.ClienteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ClienteService {


    private final ClienteRepository repository;

    private final ClienteMapper mapper;

    private final ClienteProducer producer;

    public ClienteService(ClienteRepository repository, ClienteMapper mapper, ClienteProducer producer) {
        this.repository = repository;
        this.mapper = mapper;
        this.producer = producer;
    }

    @Transactional
    public ClienteDTO salvar(ClienteDTO dto) {
        if (repository.existsByCpf(dto.getCpf())) {
            throw new BusinessException("CPF já cadastrado no sistema");
        }

        Cliente entity = mapper.toEntity(dto);

        entity.setStatus(StatusCliente.PENDENTE);

        Cliente salvo = repository.save(entity);

        ClienteDTO dtoQueue = mapper.toDTO(salvo);
        producer.enviarMensagem(dtoQueue);

        return dtoQueue;
    }

    @Transactional(readOnly = true)
    public List<ClienteDTO> listarTodos() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClienteDTO buscarPorId(Long id) {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado com o ID: " + id, HttpStatus.NOT_FOUND));
        return mapper.toDTO(cliente);
    }

    @Transactional
    public ClienteDTO atualizar(Long id, AtualizarClienteDTO dto) {
        log.info("Iniciando atualização parcial do cliente ID: {}", id);

        Cliente clienteExistente = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Não foi possível alterar: Cliente não encontrado"));

        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            clienteExistente.setNome(dto.getNome());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            clienteExistente.setEmail(dto.getEmail());
        }

        if (dto.getCpf() != null && !dto.getCpf().isBlank()) {
            if (!clienteExistente.getCpf().equals(dto.getCpf()) && repository.existsByCpf(dto.getCpf())) {
                throw new BusinessException("O novo CPF informado já está cadastrado para outro cliente");
            }
            clienteExistente.setCpf(dto.getCpf());
        }

        Cliente salvo = repository.save(clienteExistente);

        return mapper.toDTO(salvo);
    }

    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('ADMIN')")
    public void deletar(Long id) throws BusinessException {

        log.info("Iniciando exclusão do cliente com ID: {}", id);

        if (!repository.existsById(id))
            throw new BusinessException("Não foi possível deletar: Cliente não encontrado");

        repository.deleteById(id);
    }
}