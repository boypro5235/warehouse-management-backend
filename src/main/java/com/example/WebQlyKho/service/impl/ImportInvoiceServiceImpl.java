package com.example.WebQlyKho.service.impl;

import com.example.WebQlyKho.dto.request.ImportDetailRequestDto;
import com.example.WebQlyKho.dto.request.ImportInvoiceRequestDto;
import com.example.WebQlyKho.entity.ImportDetails;
import com.example.WebQlyKho.entity.ImportInvoice;
import com.example.WebQlyKho.repository.*;
import com.example.WebQlyKho.service.ImportInvoiceService;
import com.example.WebQlyKho.service.ProductService;
import com.example.WebQlyKho.repository.ImportInvoiceRepository;
import com.example.WebQlyKho.repository.SupplierRepository;
import com.example.WebQlyKho.repository.UserRepository;
import com.example.WebQlyKho.utils.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportInvoiceServiceImpl implements ImportInvoiceService {
    private final ImportInvoiceRepository importInvoiceRepository;
    @Autowired
    private final SupplierRepository supplierRepository;

    @Autowired
    private final DskhohangRepository dskhohangRepository;

    @Autowired
    private final ProductRepository productRepository;

    @Autowired
    private final ImportDetailsRepository importDetailsRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final JwtTokenProvider jwtTokenProvider;

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
        ImportInvoice invoice = new ImportInvoice();
        invoice.setSupplier(supplierRepository.findById(importInvoiceRequestDto.getSupplierId()).orElseThrow( ()->
                new EntityNotFoundException("Không tìm thấy nhà cung cấp với id = " + importInvoiceRequestDto.getSupplierId())));
        invoice.setImportDate(LocalDate.now());
        invoice.setTotalAmount(importInvoiceRequestDto.getTotalAmount());
        invoice.setFinalAmount(importInvoiceRequestDto.getFinalAmount());
        invoice.setVat(importInvoiceRequestDto.getVat());
        invoice.setDiscount(importInvoiceRequestDto.getDiscount());
        invoice.setDskhohang(dskhohangRepository.findById(importInvoiceRequestDto.getKhohangId()).orElseThrow(()->
                new EntityNotFoundException("Không tìm thấy kho hàng với id = " + importInvoiceRequestDto.getKhohangId())));
        invoice.setUser(userRepository.findById(jwtTokenProvider.getUserIdFromToken(request)).orElseThrow(()->
                new EntityNotFoundException("Không tìm thấy user với id = " + jwtTokenProvider.getUserIdFromToken(request))));
        ImportInvoice savedInvoice = importInvoiceRepository.save(invoice);

        for (ImportDetailRequestDto detailsRequest : importInvoiceRequestDto.getImportDetails()) {
            ImportDetails details = new ImportDetails();
            details.setImportInvoice(savedInvoice);
            details.setProduct(productRepository.findById(detailsRequest.getProductId()).orElseThrow(()->
                    new EntityNotFoundException("Không tìm thấy sản phẩm với id = " + detailsRequest.getProductId())));
            details.setQuantity(detailsRequest.getQuantity());
            details.setVat(detailsRequest.getVat());
            details.setDiscount(detailsRequest.getDiscount());
            details.setSubtotal(detailsRequest.getSubtotal());
            details.setTotalAmount(detailsRequest.getTotalAmount());
            importDetailsRepository.save(details);
        }
        return savedInvoice;
    }

    @Override
    @Transactional
    public ImportInvoice updateImportInvoice(Integer invoiceId, ImportInvoiceRequestDto request, HttpServletRequest httpServletRequest) {
        ImportInvoice invoice = importInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found"));

        // Update invoice details
        invoice.setSupplier(supplierRepository.findById(request.getSupplierId()).orElseThrow(()->
                new EntityNotFoundException("Không tìm thấy nhà cung cấp với id = " + request.getSupplierId())));
        invoice.setDskhohang(dskhohangRepository.findById(request.getKhohangId()).orElseThrow(()->
                new EntityNotFoundException("Không tìm thấy kho hàng với id = " + request.getKhohangId())));
        invoice.setTotalAmount(request.getTotalAmount());
        invoice.setFinalAmount(request.getFinalAmount());
        invoice.setVat(request.getVat());
        invoice.setDiscount(request.getDiscount());
        invoice.setUpdatedAt(LocalDateTime.now());
        invoice.setUser(userRepository.findById(jwtTokenProvider.getUserIdFromToken(httpServletRequest)).orElseThrow(()->
                new EntityNotFoundException("Không tìm thấy user với id = " + jwtTokenProvider.getUserIdFromToken(httpServletRequest))));

        // Fetch current ImportDetails from the database
        List<ImportDetails> existingDetails = invoice.getImportDetails();

        // Map existing ImportDetails by Product ID for easy lookup
        Map<Integer, ImportDetails> existingDetailsMap = existingDetails.stream()
                .collect(Collectors.toMap(d -> d.getProduct().getProductId(), d -> d));

        // Track updated and new ImportDetails
        List<ImportDetails> updatedDetailsList = new ArrayList<>();

        for (ImportDetailRequestDto detailsRequest : request.getImportDetails()) {
            if (existingDetailsMap.containsKey(detailsRequest.getProductId())) {
                // Update existing ImportDetail
                ImportDetails existingDetail = existingDetailsMap.get(detailsRequest.getProductId());
                existingDetail.setQuantity(detailsRequest.getQuantity());
                existingDetail.setVat(detailsRequest.getVat());
                existingDetail.setDiscount(detailsRequest.getDiscount());
                existingDetail.setSubtotal(detailsRequest.getSubtotal());
                existingDetail.setTotalAmount(detailsRequest.getTotalAmount());
                updatedDetailsList.add(existingDetail);
                // Remove from the map to track processed records
                existingDetailsMap.remove(detailsRequest.getProductId());
            } else {
                // Create new ImportDetail
                ImportDetails newDetail = new ImportDetails();
                newDetail.setImportInvoice(invoice);
                newDetail.setProduct(productRepository.findById(detailsRequest.getProductId()).orElseThrow(()->
                        new EntityNotFoundException("Không tìm thấy sản phẩm với id = " + detailsRequest.getProductId())));
                newDetail.setQuantity(detailsRequest.getQuantity());
                newDetail.setVat(detailsRequest.getVat());
                newDetail.setDiscount(detailsRequest.getDiscount());
                newDetail.setSubtotal(detailsRequest.getSubtotal());
                newDetail.setTotalAmount(detailsRequest.getTotalAmount());
                updatedDetailsList.add(newDetail);
            }
        }
        // Delete any remaining ImportDetails that were not included in the request
        for (ImportDetails toDelete : existingDetailsMap.values()) {
            importDetailsRepository.delete(toDelete);
        }
        // Set updated import details
        invoice.setImportDetails(updatedDetailsList);

        return importInvoiceRepository.save(invoice);
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

        importInvoicesToDelete.forEach(invoice -> invoice.setStatus(false));
        importInvoiceRepository.saveAll(importInvoicesToDelete);    }
}
