package com.example.WebQlyKho.service.impl;

import com.example.WebQlyKho.dto.request.ImportDetailRequestDto;
import com.example.WebQlyKho.entity.*;
import com.example.WebQlyKho.repository.DskhohangRepository;
import com.example.WebQlyKho.repository.ImportDetailsRepository;
import com.example.WebQlyKho.repository.ImportInvoiceRepository;
import com.example.WebQlyKho.repository.ProductRepository;
import com.example.WebQlyKho.service.ImportDetailsService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ImportDetailsServiceImpl implements ImportDetailsService {

    @Autowired
    private final ImportDetailsRepository importDetailsRepository;

    @Autowired
    private final ProductRepository productRepository;

    @Autowired
    private final ImportInvoiceRepository importInvoiceRepository;

    @Autowired
    private final DskhohangRepository dskhohangRepository;

    @Override
    public Map<String, Object> searchImportDetails(Integer khohangId, int page, int size) {
        try {
            if (page > 0) {
                page = page - 1;
            }
            Pageable pageable = PageRequest.of(page, size);
            Specification<ImportDetails> specification = new Specification<ImportDetails>() {
                @Override
                public Predicate toPredicate(Root<ImportDetails> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    if (khohangId != null) {
                        predicates.add(criteriaBuilder.equal(root.get("dskhohang").get("khohangId"), khohangId));
                    }
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };

            Page<ImportDetails> importDetailsPage = importDetailsRepository.findAll(specification, pageable);
            Map<String, Object> response = new HashMap<>();
            response.put("listImportDetails", importDetailsPage.getContent());
            response.put("pageSize", importDetailsPage.getSize());
            response.put("pageNo", importDetailsPage.getNumber()+1);
            response.put("totalPage", importDetailsPage.getTotalPages());
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ImportDetails getImportDetailsById(ImportDetailsIds id) {
        return importDetailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Import Details not found"));
    }

    @Override
    public ImportDetails createImportDetails(ImportDetailRequestDto importDetailRequestDto) {
        Product product = productRepository.findById(importDetailRequestDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found for id " + importDetailRequestDto.getProductId()));

        ImportInvoice importInvoice = importInvoiceRepository.findById(importDetailRequestDto.getInvoicesId())
                .orElseThrow(() -> new RuntimeException("Import Invoice not found for id " + importDetailRequestDto.getInvoicesId()));

        Dskhohang dskhohang = dskhohangRepository.findById(importDetailRequestDto.getKhohangId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found for id " + importDetailRequestDto.getKhohangId()));

        ImportDetails importDetails = new ImportDetails();
        importDetails.setProduct(product);
        importDetails.setImportInvoice(importInvoice);
        importDetails.setQuantity(importDetailRequestDto.getQuantity());
        importDetails.setSubtotal(importDetailRequestDto.getSubtotal());
        importDetails.setVat(importDetailRequestDto.getVat());
        importDetails.setDiscount(importDetailRequestDto.getDiscount());
        importDetails.setTotalAmount(importDetailRequestDto.getTotalAmount());

        return importDetailsRepository.save(importDetails);
    }

    @Override
    public ImportDetails updateImportDetails(ImportDetailsIds id, ImportDetailRequestDto importDetailRequestDto) {
        ImportDetails importDetails = importDetailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Import Details not found"));

        Product product = productRepository.findById(importDetailRequestDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found for id " + importDetailRequestDto.getProductId()));

        ImportInvoice importInvoice = importInvoiceRepository.findById(importDetailRequestDto.getInvoicesId())
                .orElseThrow(() -> new RuntimeException("Import Invoice not found for id " + importDetailRequestDto.getInvoicesId()));

        Dskhohang dskhohang = dskhohangRepository.findById(importDetailRequestDto.getKhohangId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found for id " + importDetailRequestDto.getKhohangId()));

        importDetails.setProduct(product);
        importDetails.setImportInvoice(importInvoice);
        importDetails.setQuantity(importDetailRequestDto.getQuantity());
        importDetails.setSubtotal(importDetailRequestDto.getSubtotal());
        importDetails.setVat(importDetailRequestDto.getVat());
        importDetails.setDiscount(importDetailRequestDto.getDiscount());
        importDetails.setTotalAmount(importDetailRequestDto.getTotalAmount());

        return importDetailsRepository.save(importDetails);
    }

    @Override
    public void deleteImportDetailsById(ImportDetailsIds id) {
        importDetailsRepository.deleteById(id);
    }
}