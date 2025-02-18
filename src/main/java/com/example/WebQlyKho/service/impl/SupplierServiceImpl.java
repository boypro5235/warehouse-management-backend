package com.example.WebQlyKho.service.impl;

import com.example.WebQlyKho.dto.request.CreateSupplierDto;
import com.example.WebQlyKho.entity.Supplier;
import com.example.WebQlyKho.exception.SupplierNotFoundException;
import com.example.WebQlyKho.repository.SupplierRepository;
import com.example.WebQlyKho.service.SupplierService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;

    @Override
    public Map<String, Object> searchSuppliers(String searchText,  int page, int size) {
        try {
            if (page > 0) {
                page = page - 1;
            }
            Pageable pageable = PageRequest.of(page, size);
            Specification<Supplier> specification = new Specification<Supplier>() {
                @Override
                public Predicate toPredicate(Root<Supplier> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    // Add search by name
                    predicates.add(criteriaBuilder.like(root.get("supplierName"), "%" + searchText + "%"));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };

            Page<Supplier> pageSupplier = supplierRepository.findAll(specification, pageable);
            Map<String, Object> mapSupplier = new HashMap<>();
            mapSupplier.put("listSupplier", pageSupplier.getContent());
            mapSupplier.put("pageSize", pageSupplier.getSize());
            mapSupplier.put("pageNo", pageSupplier.getNumber() + 1);
            mapSupplier.put("totalPage", pageSupplier.getTotalPages());
            return mapSupplier;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Supplier createSupplier(CreateSupplierDto createSupplierDto) {
        try{
            Supplier supplier = new Supplier();
            supplier.setSupplierName(createSupplierDto.getSupplierName());
            supplier.setContactInfo(createSupplierDto.getContactInfo());
            return supplierRepository.save(supplier);
        } catch (Exception e) {
            log.error("Error creating supplier", e);
            return null;
        }
    }

    @Override
    public Supplier updateSupplier(Integer supplierId, CreateSupplierDto createSupplierDto) {
        try {
            Supplier supplier = supplierRepository.findById(supplierId).orElseThrow(() -> new SupplierNotFoundException("Supplier not found for id: " + supplierId));
            supplier.setSupplierName(createSupplierDto.getSupplierName());
            supplier.setContactInfo(createSupplierDto.getContactInfo());
            return supplierRepository.save(supplier);
        } catch (Exception e) {
            log.error("Error updating supplier", e);
            return null;
        }
    }

    @Override
    public void deleteSuppliersByIds(List<Integer> ids) {
        List<Supplier> suppliersToDelete = supplierRepository.findAllById(ids);

        List<Integer> existingIds = suppliersToDelete.stream()
                .map(Supplier::getSupplierId)
                .toList();

        List<Integer> notFoundIds = ids.stream()
                .filter(id -> !existingIds.contains(id))
                .toList();

        if (!notFoundIds.isEmpty()) {
            throw new SupplierNotFoundException("Suppliers not found for ids: " + notFoundIds);
        }

        suppliersToDelete.forEach(supplier -> supplier.setStatus(false));
        supplierRepository.saveAll(suppliersToDelete);
    }

    @Override
    public Supplier getSupplierById(Integer supplierId) {
        return supplierRepository.findById(supplierId).orElseThrow(() -> new SupplierNotFoundException("Supplier not found for id: " + supplierId));
    }
}
