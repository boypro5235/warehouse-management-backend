package com.example.WebQlyKho.ImportInvoice.controller;

import com.example.WebQlyKho.controller.ImportInvoiceController;
import com.example.WebQlyKho.dto.request.DeleteRequest;
import com.example.WebQlyKho.dto.request.ImportInvoiceRequestDto;
import com.example.WebQlyKho.entity.ImportInvoice;
import com.example.WebQlyKho.service.ImportInvoiceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ImportInvoiceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ImportInvoiceService importInvoiceService;

    @InjectMocks
    private ImportInvoiceController importInvoiceController;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private HttpServletRequest request;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(importInvoiceController).build();
    }

    @Test
    void searchImportInvoices_ShouldReturnOkResponse() {
        when(importInvoiceService.searchImportInvoices(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonMap("data", "mocked response"));

        ResponseEntity<Object> response = importInvoiceController.searchImportInvoices(null, null, null, null, 1, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void searchImportInvoices_ShouldSetPageNoToOne_WhenPageNoIsZeroOrNegative() {
        when(importInvoiceService.searchImportInvoices(any(), any(), any(), any(), eq(1), anyInt()))
                .thenReturn(new HashMap<>());

        ResponseEntity<Object> response = importInvoiceController.searchImportInvoices(null, null, null, null, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(importInvoiceService).searchImportInvoices(any(), any(), any(), any(), eq(1), anyInt());
    }

    @Test
    void searchImportInvoices_ShouldSetPageSizeToOne_WhenPageSizeIsZeroOrNegative() {
        when(importInvoiceService.searchImportInvoices(any(), any(), any(), any(), anyInt(), eq(1)))
                .thenReturn(new HashMap<>());

        ResponseEntity<Object> response = importInvoiceController.searchImportInvoices(null, null, null, null, 1, 0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(importInvoiceService).searchImportInvoices(any(), any(), any(), any(), anyInt(), eq(1));
    }

    @Test
    void searchImportInvoices_ShouldReturnBadRequest_WhenIllegalArgumentExceptionThrown() {
        when(importInvoiceService.searchImportInvoices(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenThrow(new IllegalArgumentException("Invalid date range"));

        ResponseEntity<Object> response = importInvoiceController.searchImportInvoices(null, null, null, null, 1, 10);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Invalid date range"));
    }

    @Test
    void searchImportInvoices_ShouldReturnInternalServerError_WhenUnexpectedExceptionThrown() {
        when(importInvoiceService.searchImportInvoices(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<Object> response = importInvoiceController.searchImportInvoices(null, null, null, null, 1, 10);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("An unexpected error occurred"));
    }

    @Test
    void createImportInvoice_ShouldReturnOkResponse() {
        ImportInvoice importInvoice = new ImportInvoice();

        when(bindingResult.hasErrors()).thenReturn(false);
        when(importInvoiceService.createImportInvoice(any(ImportInvoiceRequestDto.class), any(HttpServletRequest.class)))
                .thenReturn(importInvoice);

        ResponseEntity<Object> response = importInvoiceController.createImportInvoice(new ImportInvoiceRequestDto(), bindingResult, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void createImportInvoice_ShouldReturnBadRequestForValidationErrors() throws Exception {
        ImportInvoiceRequestDto invalidDto = new ImportInvoiceRequestDto(); // Invalid data

        mockMvc.perform(post("/api/import-invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createImportInvoice_ShouldReturnNotFound_WhenEntityNotFoundExceptionThrown() {
        ImportInvoiceRequestDto requestDto = new ImportInvoiceRequestDto();

        when(importInvoiceService.createImportInvoice(any(ImportInvoiceRequestDto.class), any(HttpServletRequest.class)))
                .thenThrow(new EntityNotFoundException("Invoice not found"));

        ResponseEntity<Object> response = importInvoiceController.createImportInvoice(requestDto, bindingResult, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Invoice not found"));
    }

    @Test
    void createImportInvoice_ShouldReturnBadRequest_WhenIllegalArgumentExceptionThrown() {
        ImportInvoiceRequestDto requestDto = new ImportInvoiceRequestDto();

        when(importInvoiceService.createImportInvoice(any(ImportInvoiceRequestDto.class), any(HttpServletRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        ResponseEntity<Object> response = importInvoiceController.createImportInvoice(requestDto, bindingResult, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Invalid data"));
    }

    @Test
    void createImportInvoice_ShouldReturnInternalServerError_WhenUnexpectedExceptionThrown() {
        ImportInvoiceRequestDto requestDto = new ImportInvoiceRequestDto();

        when(importInvoiceService.createImportInvoice(any(ImportInvoiceRequestDto.class), any(HttpServletRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<Object> response = importInvoiceController.createImportInvoice(requestDto, bindingResult, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("An unexpected error occurred"));
    }

    @Test
    void getImportInvoiceById_ShouldReturnOkResponse() {
        ImportInvoice importInvoice = new ImportInvoice();
        when(importInvoiceService.getImportInvoiceById(anyInt())).thenReturn(importInvoice);

        ResponseEntity<Object> response = importInvoiceController.getImportInvoiceById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getImportInvoiceById_ShouldReturnNotFound_WhenInvoiceDoesNotExist() {
        int invoiceId = 999; // ID that doesn't exist

        when(importInvoiceService.getImportInvoiceById(invoiceId))
                .thenThrow(new EntityNotFoundException("Import invoice not found"));

        ResponseEntity<Object> response = importInvoiceController.getImportInvoiceById(invoiceId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Import invoice not found"));
    }

    @Test
    void getImportInvoiceById_ShouldReturnInternalServerError_OnUnexpectedError() {
        int invoiceId = 1; // Valid ID, but force an exception

        when(importInvoiceService.getImportInvoiceById(invoiceId))
                .thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<Object> response = importInvoiceController.getImportInvoiceById(invoiceId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("An unexpected error occurred"));
    }


    @Test
    void updateImportInvoice_ShouldReturnOkResponse() {
        ImportInvoice importInvoice = new ImportInvoice();

        when(bindingResult.hasErrors()).thenReturn(false);
        when(importInvoiceService.updateImportInvoice(anyInt(), any(ImportInvoiceRequestDto.class), any(HttpServletRequest.class)))
                .thenReturn(importInvoice);

        ResponseEntity<Object> response = importInvoiceController.updateImportInvoice(1, new ImportInvoiceRequestDto(), bindingResult, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateImportInvoice_ShouldReturnBadRequest_WhenValidationFails() {
        ImportInvoiceRequestDto requestDto = new ImportInvoiceRequestDto();

        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("importInvoiceRequestDto", "field", "must not be empty")
        ));

        ResponseEntity<Object> response = importInvoiceController.updateImportInvoice(1, requestDto, bindingResult, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Validation failed"));
    }

    @Test
    void updateImportInvoice_ShouldReturnNotFound_WhenEntityNotFoundExceptionThrown() {
        ImportInvoiceRequestDto requestDto = new ImportInvoiceRequestDto();

        when(bindingResult.hasErrors()).thenReturn(false);
        when(importInvoiceService.updateImportInvoice(anyInt(), any(ImportInvoiceRequestDto.class), any(HttpServletRequest.class)))
                .thenThrow(new EntityNotFoundException("Invoice not found"));

        ResponseEntity<Object> response = importInvoiceController.updateImportInvoice(1, requestDto, bindingResult, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Invoice not found"));
    }

    @Test
    void updateImportInvoice_ShouldReturnBadRequest_WhenIllegalArgumentExceptionThrown() {
        ImportInvoiceRequestDto requestDto = new ImportInvoiceRequestDto();

        when(bindingResult.hasErrors()).thenReturn(false);
        when(importInvoiceService.updateImportInvoice(anyInt(), any(ImportInvoiceRequestDto.class), any(HttpServletRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        ResponseEntity<Object> response = importInvoiceController.updateImportInvoice(1, requestDto, bindingResult, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Invalid data"));
    }

    @Test
    void updateImportInvoice_ShouldReturnInternalServerError_WhenUnexpectedExceptionThrown() {
        ImportInvoiceRequestDto requestDto = new ImportInvoiceRequestDto();

        when(bindingResult.hasErrors()).thenReturn(false);
        when(importInvoiceService.updateImportInvoice(anyInt(), any(ImportInvoiceRequestDto.class), any(HttpServletRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<Object> response = importInvoiceController.updateImportInvoice(1, requestDto, bindingResult, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("An unexpected error occurred"));
    }


    @Test
    void deleteImportInvoice_ShouldReturnOkResponse() {
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.setIds(Collections.singletonList(1));

        ResponseEntity<Object> response = importInvoiceController.deleteImportInvoice(deleteRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteImportInvoice_ShouldReturnInternalServerError_OnUnexpectedError() {
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.setIds(Collections.singletonList(1));

        doThrow(new RuntimeException("Unexpected error")).when(importInvoiceService).deleteImportInvoice(anyList());

        ResponseEntity<Object> response = importInvoiceController.deleteImportInvoice(deleteRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("An unexpected error occurred"));
    }

    @Test
    void deleteImportInvoice_ShouldReturnBadRequest_WhenIdsAreNull() {
        DeleteRequest nullRequest = new DeleteRequest();
        nullRequest.setIds(null); // Explicitly setting to null

        ResponseEntity<Object> response = importInvoiceController.deleteImportInvoice(nullRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("The data sent is not in the correct format."));
    }

    @Test
    void deleteImportInvoice_ShouldReturnBadRequest_WhenIdsAreEmpty() {
        DeleteRequest emptyRequest = new DeleteRequest();
        emptyRequest.setIds(Collections.emptyList()); // Empty list

        ResponseEntity<Object> response = importInvoiceController.deleteImportInvoice(emptyRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("The data sent is not in the correct format."));
    }

}
