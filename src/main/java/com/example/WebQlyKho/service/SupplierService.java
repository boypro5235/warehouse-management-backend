package com.example.WebQlyKho.service;
import com.example.WebQlyKho.dto.request.CreateSupplierDto;
import com.example.WebQlyKho.entity.Supplier;

import java.util.List;
import java.util.Map;

public interface SupplierService {
    Map<String, Object> searchSuppliers(String searchText, int page, int size);

    Supplier createSupplier(CreateSupplierDto createSupplierDto);

    Supplier updateSupplier(Integer supplierId, CreateSupplierDto createSupplierDto);

    Supplier getSupplierById(Integer supplierId);

    void deleteSuppliersByIds(List<Integer> ids);
}
