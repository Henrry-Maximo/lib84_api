package com.example.apirestspringboot.service;

import com.example.apirestspringboot.dto.SupplierRecordDto;
import com.example.apirestspringboot.entity.Supplier;
import com.example.apirestspringboot.exception.SupplierNotFoundException;
import com.example.apirestspringboot.repository.SupplierRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public Supplier create(SupplierRecordDto dto) {
        var supplier = new Supplier();
        BeanUtils.copyProperties(dto, supplier);
        return this.supplierRepository.save(supplier);
    }

    public Page<Supplier> getAll(Pageable pageable) {
        return this.supplierRepository.findAll(pageable);
    }

    public Supplier getById(UUID id) {
        return this.supplierRepository.findById(id).orElseThrow(() -> new SupplierNotFoundException(id));
    }

    public Supplier update(Supplier supplier, SupplierRecordDto dto) {
        BeanUtils.copyProperties(dto, supplier);
        return this.supplierRepository.save(supplier);
    }

    public Supplier getByName(String name) {
        return this.supplierRepository.findByNameContainingIgnoreCase(name).orElseThrow(() -> new SupplierNotFoundException(null));
    }

    public void delete(Supplier supplier) {
        this.supplierRepository.deleteById(supplier.getId());
    }
}
