package com.laundrypos.modules.clients.service;

import com.laundrypos.modules.clients.dto.ClientRequest;
import com.laundrypos.modules.clients.dto.ClientResponse;
import com.laundrypos.modules.clients.model.Client;
import com.laundrypos.modules.clients.repository.ClientRepository;
import com.laundrypos.modules.sales.model.ServiceOrder;
import com.laundrypos.modules.sales.repository.ServiceOrderRepository;
import com.laundrypos.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ServiceOrderRepository serviceOrderRepository;

    public List<ClientResponse> findAll() {
        return clientRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    public ClientResponse findById(Long id) {
        var client = clientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
        return toResponse(client);
    }

    public ClientResponse create(ClientRequest request) {
        var client = Client.builder()
            .name(request.name())
            .phone(request.phone())
            .email(request.email())
            .build();
        return toResponse(clientRepository.save(client));
    }

    public ClientResponse update(Long id, ClientRequest request) {
        var client = clientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
        client.setName(request.name());
        client.setPhone(request.phone());
        client.setEmail(request.email());
        return toResponse(clientRepository.save(client));
    }

    public List<ClientResponse> search(String phone) {
        return clientRepository.findByPhoneContaining(phone).stream()
            .map(this::toResponse)
            .toList();
    }

    public List<ServiceOrder> getServiceHistory(Long clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Cliente", clientId);
        }
        return serviceOrderRepository.findByClientId(clientId);
    }

    private ClientResponse toResponse(Client client) {
        return new ClientResponse(
            client.getId(), client.getName(), client.getPhone(),
            client.getEmail(), client.getLoyaltyPoints(), client.getCreatedAt());
    }
}
