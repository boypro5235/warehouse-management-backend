package com.example.WebQlyKho.controller;

import com.example.WebQlyKho.dto.request.ImportDetailRequestDto;
import com.example.WebQlyKho.dto.response.APIResponse;
import com.example.WebQlyKho.entity.ImportDetails;
import com.example.WebQlyKho.entity.ImportDetailsIds;
import com.example.WebQlyKho.service.ImportDetailsService;
import jakarta.annotation.Nullable;
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
@RequestMapping("api/import-details")
@Slf4j
public class ImportDetailsController {
    @Autowired
    private final ImportDetailsService importDetailsService;

    @GetMapping
    public ResponseEntity<Object> searchImportDetails(
            @RequestParam @Nullable Integer khohangId,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            // Đảm bảo pageNo và pageSize hợp lệ
            if (pageNo <= 0) pageNo = 1;
            if (pageSize <= 0) pageSize = 1;

            // Gọi service
            Map<String, Object> response = importDetailsService.searchImportDetails(khohangId, pageNo, pageSize);

            return APIResponse.responseBuilder(response, "List import details", HttpStatus.OK);
        }catch (Exception e) {
            log.error("Error searching import details", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/{productId}/{invoicesId}")
    public ResponseEntity<Object> getImportDetailsById(
            @PathVariable Integer productId,
            @PathVariable Integer invoicesId) {
        try {
            ImportDetailsIds id = new ImportDetailsIds(productId, invoicesId);
            ImportDetails importDetails = importDetailsService.getImportDetailsById(id);
            return APIResponse.responseBuilder(importDetails, "Import details found", HttpStatus.OK);
        } catch (RuntimeException e) {
            return APIResponse.responseBuilder(null, e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error fetching import details", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PostMapping
    public ResponseEntity<Object> createImportDetails(@RequestBody @Valid ImportDetailRequestDto importDetailRequestDto , BindingResult bindingResult) {
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
            ImportDetails newImportDetails = importDetailsService.createImportDetails(importDetailRequestDto);
            return APIResponse.responseBuilder(newImportDetails, "Import details created", HttpStatus.OK);
        } catch (RuntimeException e) {
            return APIResponse.responseBuilder(null, e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error creating import details", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PutMapping("/{productId}/{invoicesId}")
    public ResponseEntity<Object> updateImportDetails(
            @PathVariable Integer productId,
            @PathVariable Integer invoicesId,
            @RequestBody @Valid ImportDetailRequestDto importDetailRequestDto,
            BindingResult bindingResult) {
        try {
            ImportDetailsIds id = new ImportDetailsIds(productId, invoicesId);
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
            ImportDetails updatedImportDetails = importDetailsService.updateImportDetails(id, importDetailRequestDto);
            return APIResponse.responseBuilder(updatedImportDetails, "Import details updated", HttpStatus.OK);
        } catch (RuntimeException e) {
            return APIResponse.responseBuilder(null, e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error updating import details", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @DeleteMapping("/{productId}/{invoicesId}")
    public ResponseEntity<Object> deleteImportDetailsById(
            @PathVariable Integer productId,
            @PathVariable Integer invoicesId) {
        try {
            ImportDetailsIds id = new ImportDetailsIds(productId, invoicesId);
            importDetailsService.deleteImportDetailsById(id);
            return APIResponse.responseBuilder(null, "Import details deleted", HttpStatus.OK);
        }  catch (Exception e) {
            log.error("Error deleting import details", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
