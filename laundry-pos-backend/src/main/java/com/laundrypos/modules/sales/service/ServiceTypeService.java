package com.laundrypos.modules.sales.service;

import com.laundrypos.modules.sales.dto.ServiceTypeRequest;
import com.laundrypos.modules.sales.dto.ServiceTypeResponse;
import com.laundrypos.modules.sales.model.ServiceType;
import com.laundrypos.modules.sales.repository.ServiceTypeRepository;
import com.laundrypos.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceTypeService {

    private final ServiceTypeRepository serviceTypeRepository;

    public List<ServiceTypeResponse> findAll() {
        return serviceTypeRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    public ServiceTypeResponse findById(Long id) {
        return serviceTypeRepository.findById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Tipo de servicio no encontrado"));
    }

    @Transactional
    public ServiceTypeResponse create(ServiceTypeRequest request) {
        var serviceType = ServiceType.builder()
            .name(request.name())
            .pricePerKg(request.pricePerKg())
            .estimatedTimeMinutes(request.estimatedTimeMinutes())
            .build();

        return toResponse(serviceTypeRepository.save(serviceType));
    }

    @Transactional
    public ServiceTypeResponse update(Long id, ServiceTypeRequest request) {
        var serviceType = serviceTypeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tipo de servicio no encontrado"));

        serviceType.setName(request.name());
        serviceType.setPricePerKg(request.pricePerKg());
        serviceType.setEstimatedTimeMinutes(request.estimatedTimeMinutes());

        return toResponse(serviceTypeRepository.save(serviceType));
    }

    @Transactional
    public void delete(Long id) {
        var serviceType = serviceTypeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tipo de servicio no encontrado"));
        serviceTypeRepository.delete(serviceType);
    }

    private ServiceTypeResponse toResponse(ServiceType entity) {
        return new ServiceTypeResponse(
            entity.getId(),
            entity.getName(),
            entity.getPricePerKg(),
            entity.getEstimatedTimeMinutes()
        );
    }
}
