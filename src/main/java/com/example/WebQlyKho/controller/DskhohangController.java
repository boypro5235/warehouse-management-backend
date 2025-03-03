package com.example.WebQlyKho.controller;

import com.example.WebQlyKho.dto.request.CreateDskhohangDto;
import com.example.WebQlyKho.dto.request.DeleteRequest;
import com.example.WebQlyKho.dto.response.APIResponse;
import com.example.WebQlyKho.entity.Dskhohang;
import com.example.WebQlyKho.service.DskhohangService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/dskhohangs")
@Slf4j
public class DskhohangController {
    @Autowired
    private DskhohangService dskhohangService;

    @PostMapping
    public ResponseEntity<Object> createDskhohang(@RequestBody @Valid CreateDskhohangDto createDskhohangDto, BindingResult bindingResult) {
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
            Dskhohang dskhohang = dskhohangService.createDskhohang(createDskhohangDto);
            return APIResponse.responseBuilder(dskhohang, "Khohang created successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error creating khohang", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping()
    public ResponseEntity<Object> searchDskhohangs(@RequestParam(defaultValue = "")  String searchText,
                                                   @RequestParam(defaultValue = "1") Integer pageNo,
                                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            if(pageNo<=0&&pageSize<=0) {
                pageNo = 1;
                pageSize = 1;
            }
            Map<String, Object> mapDskhohang = dskhohangService.searchDskhohangs(searchText, pageNo, pageSize);
            return APIResponse.responseBuilder(mapDskhohang, null, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting khohangs", e);
            return APIResponse.responseBuilder(null,"Error occurred while getting khohangs", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{khohangId}")
    public ResponseEntity<Object> getKhohangbyId(@PathVariable Integer khohangId) {
        try {
            Dskhohang dskhohang = dskhohangService.getDskhohangById(khohangId);
            return APIResponse.responseBuilder(dskhohang, "Khohang with id= "+khohangId+" return successfully", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return APIResponse.responseBuilder(null, e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error getting khohang", e);
            return APIResponse.responseBuilder(null, "Error occurred while getting khohang", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{khohangId}")
    public ResponseEntity<Object> updateKhohang(@PathVariable Integer khohangId, @RequestBody @Valid CreateDskhohangDto createDskhohangDto, BindingResult bindingResult) {
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
            Dskhohang dskhohang = dskhohangService.updateDskhohang(khohangId, createDskhohangDto);
            return APIResponse.responseBuilder(dskhohang, "Khohang updated successfully", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return APIResponse.responseBuilder(null, e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error updating khohang", e);
            return APIResponse.responseBuilder(null, "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping()
    public ResponseEntity<Object> deleteKhohangs(@RequestBody DeleteRequest request) {
        try {
            if (request.getIds() == null || request.getIds().isEmpty()) {
                return APIResponse.responseBuilder(null, "The data sent is not in the correct format.", HttpStatus.BAD_REQUEST);
            }

            dskhohangService.deleteDskhohangsByIds(request.getIds());
            return APIResponse.responseBuilder(
                    null,
                    "Khohangs deleted successfully",
                    HttpStatus.OK
            );
        } catch (EntityNotFoundException e) {
            return APIResponse.responseBuilder(
                    null,
                    e.getMessage(),
                    HttpStatus.NOT_FOUND
            );
        }catch (Exception e) {
            log.error("Unexpected error during deleting update", e);
            return APIResponse.responseBuilder(
                    null,
                    "An unexpected error occurred while deleting the khohangs",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
