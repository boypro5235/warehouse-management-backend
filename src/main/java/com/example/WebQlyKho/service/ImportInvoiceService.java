package com.example.WebQlyKho.service;

import com.example.WebQlyKho.dto.request.ImportInvoiceRequestDto;
import com.example.WebQlyKho.entity.ImportInvoice;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

public interface ImportInvoiceService {
    Map<String, Object> searchImportInvoices(Integer supplierId, String importDate, String fromDate, String toDate, int page, int size);
    ImportInvoice getImportInvoiceById(Integer invoicesId);
    ImportInvoice createImportInvoice(ImportInvoiceRequestDto importInvoiceRequestDto, HttpServletRequest request);
    ImportInvoice updateImportInvoice(Integer invoicesId,ImportInvoiceRequestDto importInvoiceRequestDto,HttpServletRequest httpServletRequest);
    void deleteImportInvoice(List<Integer> ids);
}
