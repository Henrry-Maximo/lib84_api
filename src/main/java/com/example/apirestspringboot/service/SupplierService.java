package com.example.apirestspringboot.service;

import com.example.apirestspringboot.dto.SupplierRecordDto;
import com.example.apirestspringboot.entity.Supplier;
import com.example.apirestspringboot.exception.SupplierByNameNotFoundException;
import com.example.apirestspringboot.exception.SupplierNotFoundException;
import com.example.apirestspringboot.repository.SupplierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Transactional
    public Supplier create(SupplierRecordDto dto) {
        var supplier = new Supplier();

        supplier.setName(dto.name());
        supplier.setEmail(dto.email());
        supplier.setPhone(dto.phone());
        supplier.setCnpj(dto.cnpj());

        return this.supplierRepository.save(supplier);
    }

    public Page<Supplier> getAll(Pageable pageable) {
        return this.supplierRepository.findAll(pageable);
    }

    public Supplier getById(UUID id) {
        var supplier = this.supplierRepository.findById(id);

        if (supplier.isEmpty()) {
            throw new SupplierNotFoundException(id);
        }

        return supplier.get();
    }

    public Supplier getByName(String name) {
        var supplier = this.supplierRepository.findByNameContainingIgnoreCase(name);

        if (supplier.isEmpty()) {
            throw new SupplierByNameNotFoundException(name);
        }

        return supplier.get();
    }

    @Transactional
    public Supplier update(UUID id, SupplierRecordDto dto) {
        var supplier = this.getById(id);

        supplier.setName(dto.name());
        supplier.setEmail(dto.email());
        supplier.setPhone(dto.phone());
        supplier.setCnpj(dto.cnpj());

        return this.supplierRepository.save(supplier);
    }

    @Transactional
    public void delete(UUID id) {
        var supplier = this.getById(id);

        this.supplierRepository.delete(supplier);
    }
}
