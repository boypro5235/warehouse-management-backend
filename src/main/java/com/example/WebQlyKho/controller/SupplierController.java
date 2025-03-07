package com.example.WebQlyKho.controller;

import com.example.WebQlyKho.dto.request.CreateSupplierDto;
import com.example.WebQlyKho.dto.request.DeleteRequest;
import com.example.WebQlyKho.dto.response.APIResponse;
import com.example.WebQlyKho.entity.Supplier;
import com.example.WebQlyKho.exception.SupplierNotFoundException;
import com.example.WebQlyKho.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/suppliers")
@RequiredArgsConstructor
@Slf4j
public class SupplierController {
    @Autowired
    private final SupplierService supplierService;

    @PostMapping
    public ResponseEntity<Object> createSupplier(@RequestBody @Valid CreateSupplierDto createSupplierDto, BindingResult bindingResult) {
        try {
            Map<String, String> errors = new HashMap<>();
            if (bindingResult.hasErrors()) {
                bindingResult.getFieldErrors().forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );
            }
            if (!errors.isEmpty()) {
                return APIResponse.responseBuilder(
                        errors,
                        "Validation failed",
                        HttpStatus.BAD_REQUEST
                );
            }
            Supplier supplier = supplierService.createSupplier(createSupplierDto);
            return APIResponse.responseBuilder(supplier, "Supplier created successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error creating suppliers", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping()
    public ResponseEntity<Object> searchSuppliers(@RequestParam(defaultValue = "")  String searchText,
                                                    @RequestParam(defaultValue = "1") Integer pageNo,
                                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            if(pageNo<=0&&pageSize<=0) {
                pageNo = 1;
                pageSize = 1;
            }
            Map<String, Object> mapSupplier = supplierService.searchSuppliers(searchText, pageNo, pageSize);
            return APIResponse.responseBuilder(mapSupplier, null, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting suppliers", e);
            return APIResponse.responseBuilder(null,"Error occurred while getting suppliers", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{supplierId}")
    public ResponseEntity<Object> getSupplierById(@PathVariable Integer supplierId) {
        try {
            Supplier supplier = supplierService.getSupplierById(supplierId);
            return APIResponse.responseBuilder(supplier, "Supplier with id="+supplierId+" return successfully", HttpStatus.OK);
        } catch (SupplierNotFoundException e) {
            return APIResponse.responseBuilder(null, e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error getting supplier", e);
            return APIResponse.responseBuilder(null, "Error occurred while getting supplier", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{supplierId}")
    public ResponseEntity<Object> updateSupplier(@PathVariable Integer supplierId, @RequestBody @Valid CreateSupplierDto createSupplierDto, BindingResult bindingResult) {
        try {
            Map<String, String> errors = new HashMap<>();
            if (bindingResult.hasErrors()) {
                bindingResult.getFieldErrors().forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );
            }
            if (!errors.isEmpty()) {
                return APIResponse.responseBuilder(
                        errors,
                        "Validation failed",
                        HttpStatus.BAD_REQUEST
                );
            }
            Supplier supplier = supplierService.updateSupplier(supplierId, createSupplierDto);
            return APIResponse.responseBuilder(supplier, "Supplier updated successfully", HttpStatus.OK);
        } catch (SupplierNotFoundException e) {
            return APIResponse.responseBuilder(null, e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error updating supplier", e);
            return APIResponse.responseBuilder(null, "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping()
    public ResponseEntity<Object> deleteSuppliers(@RequestBody DeleteRequest request) {
        try {
            if (request.getIds() == null || request.getIds().isEmpty()) {
                return APIResponse.responseBuilder(null, "The data sent is not in the correct format.", HttpStatus.BAD_REQUEST);
            }

            supplierService.deleteSuppliersByIds(request.getIds());
            return APIResponse.responseBuilder(
                    null,
                    "Suppliers deleted successfully",
                    HttpStatus.OK
            );
        } catch (SupplierNotFoundException e) {
            return APIResponse.responseBuilder(
                    null,
                    e.getMessage(),
                    HttpStatus.NOT_FOUND
            );
        }catch (Exception e) {
            log.error("Unexpected error during deleting update", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred while deleting the suppliers",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
