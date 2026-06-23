package com.laundrypos.modules.services.service;

import com.laundrypos.modules.services.dto.ServiceRequest;
import com.laundrypos.modules.services.dto.ServiceResponse;
import com.laundrypos.modules.services.model.ServiceEntity;
import com.laundrypos.modules.services.repository.ServiceRepository;
import com.laundrypos.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceEntityService {

    private final ServiceRepository serviceRepository;

    public List<ServiceResponse> findAll() {
        return serviceRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    public List<ServiceResponse> findActive() {
        return serviceRepository.findByActiveTrue().stream()
            .map(this::toResponse)
            .toList();
    }

    public ServiceResponse findById(Long id) {
        return serviceRepository.findById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));
    }

    @Transactional
    public ServiceResponse create(ServiceRequest request) {
        if (serviceRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("El servicio ya existe");
        }

        var service = ServiceEntity.builder()
            .name(request.name())
            .cost(request.cost())
            .build();

        return toResponse(serviceRepository.save(service));
    }

    @Transactional
    public ServiceResponse update(Long id, ServiceRequest request) {
        var service = serviceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

        if (!service.getName().equals(request.name()) && serviceRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("El nombre del servicio ya está en uso");
        }

        service.setName(request.name());
        service.setCost(request.cost());

        return toResponse(serviceRepository.save(service));
    }

    @Transactional
    public void delete(Long id) {
        var service = serviceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));
        serviceRepository.delete(service);
    }

    @Transactional
    public ServiceResponse toggleStatus(Long id) {
        var service = serviceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));
        service.setActive(!service.isActive());
        return toResponse(serviceRepository.save(service));
    }

    private ServiceResponse toResponse(ServiceEntity entity) {
        return new ServiceResponse(
            entity.getId(),
            entity.getName(),
            entity.getCost(),
            entity.isActive(),
            entity.getCreatedAt()
        );
    }
}
