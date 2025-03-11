package com.example.WebQlyKho.controller;
import com.example.WebQlyKho.dto.request.DeleteRequest;
import com.example.WebQlyKho.dto.request.ImportInvoiceRequestDto;
import com.example.WebQlyKho.dto.response.APIResponse;
import com.example.WebQlyKho.entity.ImportInvoice;
import com.example.WebQlyKho.service.ImportInvoiceService;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
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
@RequiredArgsConstructor
@RequestMapping("api/import-invoices")
@Slf4j
public class ImportInvoiceController {
    @Autowired
    private ImportInvoiceService importInvoiceService;

    @GetMapping
    public ResponseEntity<Object> searchImportInvoices(@RequestParam @Nullable Integer supplierId,
                                                       @RequestParam @Nullable String importDate,
                                                       @RequestParam @Nullable String fromDate,
                                                       @RequestParam @Nullable String toDate,
                                                       @RequestParam(defaultValue = "1") int pageNo,
                                                       @RequestParam(defaultValue = "10") int pageSize) {
        try {
            // Đảm bảo pageNo và pageSize hợp lệ
            if (pageNo <= 0) pageNo = 1;
            if (pageSize <= 0) pageSize = 1;

            // Gọi service
            Map<String, Object> response = importInvoiceService.searchImportInvoices(supplierId, importDate, fromDate, toDate, pageNo, pageSize);

            return APIResponse.responseBuilder(response, "List import invoices", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return APIResponse.responseBuilder(null, e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception e) {
            log.error("Error searching import invoices", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PostMapping
    public ResponseEntity<Object> createImportInvoice(@RequestBody @Valid ImportInvoiceRequestDto importInvoiceRequestDto, BindingResult bindingResult, HttpServletRequest request) {
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
            ImportInvoice importInvoice = importInvoiceService.createImportInvoice(importInvoiceRequestDto, request);
            return APIResponse.responseBuilder(importInvoice, "Import invoice created successfully", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return APIResponse.responseBuilder(null, e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return APIResponse.responseBuilder(null, e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception e) {
            log.error("Error creating import invoice", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/{invoicesId}")
    public ResponseEntity<Object> getImportInvoiceById(@PathVariable Integer invoicesId) {
        try {
            ImportInvoice importInvoice = importInvoiceService.getImportInvoiceById(invoicesId);
            return APIResponse.responseBuilder(importInvoice, "Import invoice detail", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return APIResponse.responseBuilder(null, e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error getting import invoice", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PutMapping("/{invoicesId}")
    public ResponseEntity<Object> updateImportInvoice(@PathVariable Integer invoicesId, @RequestBody @Valid ImportInvoiceRequestDto importInvoiceRequestDto, BindingResult bindingResult) {
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
            ImportInvoice importInvoice = importInvoiceService.updateImportInvoice(invoicesId, importInvoiceRequestDto);
            return APIResponse.responseBuilder(importInvoice, "Import invoice updated successfully", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return APIResponse.responseBuilder(null, e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return APIResponse.responseBuilder(null, e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error updating import invoice", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteImportInvoice(@RequestBody DeleteRequest request) {
        try {
            if (request.getIds() == null || request.getIds().isEmpty()) {
                return APIResponse.responseBuilder(null, "The data sent is not in the correct format.", HttpStatus.BAD_REQUEST);
            }

            importInvoiceService.deleteImportInvoice(request.getIds());
            return APIResponse.responseBuilder(
                    null,
                    "Import invoice deleted successfully",
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("Error deleting import invoice", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
