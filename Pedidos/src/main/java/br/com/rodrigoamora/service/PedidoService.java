package br.com.rodrigoamora.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.rodrigoamora.dto.PedidoDto;
import br.com.rodrigoamora.dto.StatusDto;
import br.com.rodrigoamora.model.Pedido;
import br.com.rodrigoamora.model.Status;
import br.com.rodrigoamora.repository.PedidoRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository repository;

    @Autowired
    private ModelMapper modelMapper;


    public List<PedidoDto> obterTodos() {
        return repository.findAll().stream()
                .map(p -> modelMapper.map(p, PedidoDto.class))
                .collect(Collectors.toList());
    }

    public PedidoDto obterPorId(Long id) {
        Pedido pedido = repository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        return modelMapper.map(pedido, PedidoDto.class);
    }

    public PedidoDto criarPedido(PedidoDto dto) {
        Pedido pedido = modelMapper.map(dto, Pedido.class);

        pedido.setDataHora(LocalDateTime.now());
        pedido.setStatus(Status.REALIZADO);
        pedido.getItens().forEach(item -> item.setPedido(pedido));
        Pedido salvo = repository.save(pedido);

        return modelMapper.map(pedido, PedidoDto.class);
    }

    public PedidoDto atualizaStatus(Long id, StatusDto dto) {

        Pedido pedido = repository.porIdComItens(id);

        if (pedido == null) {
            throw new EntityNotFoundException();
        }

        pedido.setStatus(dto.getStatus());
        repository.atualizaStatus(dto.getStatus(), pedido);
        return modelMapper.map(pedido, PedidoDto.class);
    }

    public void aprovaPagamentoPedido(Long id) {

        Pedido pedido = repository.porIdComItens(id);

        if (pedido == null) {
            throw new EntityNotFoundException();
        }

        pedido.setStatus(Status.PAGO);
        repository.atualizaStatus(Status.PAGO, pedido);
    }
}
