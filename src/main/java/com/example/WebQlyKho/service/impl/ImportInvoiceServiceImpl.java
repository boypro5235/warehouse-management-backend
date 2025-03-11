package com.example.WebQlyKho.service.impl;

import com.example.WebQlyKho.dto.request.ImportInvoiceRequestDto;
import com.example.WebQlyKho.entity.ImportInvoice;
import com.example.WebQlyKho.repository.ImportInvoiceRepository;
import com.example.WebQlyKho.repository.SupplierRepository;
import com.example.WebQlyKho.repository.UserRepository;
import com.example.WebQlyKho.service.ImportInvoiceService;
import com.example.WebQlyKho.utils.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportInvoiceServiceImpl implements ImportInvoiceService {
    private final ImportInvoiceRepository importInvoiceRepository;
    @Autowired
    private final SupplierRepository supplierRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public Map<String, Object> searchImportInvoices(Integer supplierId, String importDate, String fromDate, String toDate, int page, int size) {
        if (page > 0) {
            page = page - 1;
        }
        Pageable pageable = PageRequest.of(page, size);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (importDate != null && !importDate.isEmpty()) {
            importDate = validateDate(importDate, formatter);  // This will throw IllegalArgumentException if invalid
        }
        if (fromDate != null && !fromDate.isEmpty()) {
            fromDate = validateDate(fromDate, formatter);
        }
        if (toDate != null && !toDate.isEmpty()) {
            toDate = validateDate(toDate, formatter);
        }

        String finalImportDate = importDate;
        String finalFromDate = fromDate;
        String finalToDate = toDate;
        Specification<ImportInvoice> specification = (Root<ImportInvoice> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (finalImportDate != null && !finalImportDate.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("importDate"), LocalDate.parse(finalImportDate)));
            }

            if (finalFromDate != null && finalToDate != null) {
                predicates.add(criteriaBuilder.between(
                        root.get("importDate"),
                        LocalDate.parse(finalFromDate),
                        LocalDate.parse(finalToDate)
                ));
            } else if (finalFromDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("importDate"), LocalDate.parse(finalFromDate)));
            } else if (finalToDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("importDate"), LocalDate.parse(finalToDate)));
            }

            if (supplierId != null) {
                predicates.add(criteriaBuilder.equal(root.get("supplier").get("supplierId"), supplierId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<ImportInvoice> pageImportInvoice = importInvoiceRepository.findAll(specification, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("listImportInvoices", pageImportInvoice.getContent());
        response.put("pageSize", pageImportInvoice.getSize());
        response.put("pageNo", pageImportInvoice.getNumber() + 1);
        response.put("totalPage", pageImportInvoice.getTotalPages());
        return response;
    }

    /**
     * Validate date format (yyyy-MM-dd)
     */
    private String validateDate(String date, DateTimeFormatter formatter) {
        try {
            return LocalDate.parse(date, formatter).toString();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + date + ". Please use yyyy-MM-dd", e);
        }
    }



    @Override
    public ImportInvoice getImportInvoiceById(Integer invoicesId) {
        return importInvoiceRepository.findById(invoicesId).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hóa đơn nhập hàng với id = " + invoicesId));
    }

    @Override
    public ImportInvoice createImportInvoice(ImportInvoiceRequestDto importInvoiceRequestDto, HttpServletRequest request) {
        try {
            ImportInvoice importInvoice = new ImportInvoice();
            importInvoice.setSupplier(supplierRepository.findById(importInvoiceRequestDto.getSupplierId()).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nhà cung cấp với id = " + importInvoiceRequestDto.getSupplierId())));
            try {
                importInvoice.setImportDate(LocalDate.parse(importInvoiceRequestDto.getImportDate()));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid import date format: " + importInvoiceRequestDto.getImportDate());
            }
            importInvoice.setTotalAmount(importInvoiceRequestDto.getTotalAmount());
            importInvoice.setVat(importInvoiceRequestDto.getVat());
            importInvoice.setDiscount(importInvoiceRequestDto.getDiscount());
            importInvoice.setFinalAmount(importInvoiceRequestDto.getFinalAmount());
            importInvoice.setStatus(true);
            importInvoice.setCreatedAt(LocalDateTime.now());
            importInvoice.setUpdatedAt(LocalDateTime.now());
            importInvoice.setUser(userRepository.findById(jwtTokenProvider.getUserIdFromToken(request)).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy user")));
            return importInvoiceRepository.save(importInvoice);
        } catch (Exception e) {
            log.error("Error creating import invoice", e);
            return null;
        }
    }

    @Override
    public ImportInvoice updateImportInvoice(Integer invoicesId, ImportInvoiceRequestDto importInvoiceRequestDto) {
//        try {
            ImportInvoice importInvoice = importInvoiceRepository.findById(invoicesId).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hóa đơn nhập hàng với id = " + invoicesId));
            importInvoice.setSupplier(supplierRepository.findById(importInvoiceRequestDto.getSupplierId()).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nhà cung cấp với id = " + importInvoiceRequestDto.getSupplierId())));
            try {
                importInvoice.setImportDate(LocalDate.parse(importInvoiceRequestDto.getImportDate()));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid import date format: " + importInvoiceRequestDto.getImportDate());
            }
            importInvoice.setTotalAmount(importInvoiceRequestDto.getTotalAmount());
            importInvoice.setVat(importInvoiceRequestDto.getVat());
            importInvoice.setDiscount(importInvoiceRequestDto.getDiscount());
            importInvoice.setFinalAmount(importInvoiceRequestDto.getFinalAmount());
            importInvoice.setUpdatedAt(LocalDateTime.now());
            return importInvoiceRepository.save(importInvoice);
//        } catch (Exception e) {
//            log.error("Error updating import invoice", e);
//            return null;
//        }
    }

    @Override
    public void deleteImportInvoice(List<Integer> ids) {
        List<ImportInvoice> importInvoicesToDelete = importInvoiceRepository.findAllById(ids);

        List<Integer> existingIds = importInvoicesToDelete.stream()
                .map(ImportInvoice::getInvoicesId)
                .toList();

        List<Integer> notFoundIds = ids.stream()
                .filter(id -> !existingIds.contains(id))
                .toList();

        if (!notFoundIds.isEmpty()) {
            throw new EntityNotFoundException("Import invoices not found for ids: " + notFoundIds);
        }

        importInvoiceRepository.deleteAll(importInvoicesToDelete);
    }
}
