package com.example.WebQlyKho.service.impl;

import com.example.WebQlyKho.dto.request.CreateDskhohangDto;
import com.example.WebQlyKho.entity.Dskhohang;
import com.example.WebQlyKho.repository.DskhohangRepository;
import com.example.WebQlyKho.service.DskhohangService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DskhohangServiceImpl implements DskhohangService {
    private final DskhohangRepository dskhohangRepository;

    @Override
    public Map<String, Object> searchDskhohangs(String searchText, int page, int size) {
        try {
            if (page > 0) {
                page = page - 1;
            }
            Pageable pageable = PageRequest.of(page, size);
            Specification<Dskhohang> specification = new Specification<>() {
                @Override
                public Predicate toPredicate(Root<Dskhohang> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    // Add search by name
                    predicates.add(criteriaBuilder.like(root.get("name"), "%" + searchText + "%"));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };

            Page<Dskhohang> pageDskhohang = dskhohangRepository.findAll(specification, pageable);
            Map<String, Object> mapDskhohang = new HashMap<>();
            mapDskhohang.put("listDskhohang", pageDskhohang.getContent());
            mapDskhohang.put("pageSize", pageDskhohang.getSize());
            mapDskhohang.put("pageNo", pageDskhohang.getNumber() + 1);
            mapDskhohang.put("totalPage", pageDskhohang.getTotalPages());
            return mapDskhohang;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Dskhohang createDskhohang(CreateDskhohangDto createDskhohangDto){
        try{
            Dskhohang dskhohang = new Dskhohang();
            dskhohang.setName(createDskhohangDto.getName());
            dskhohang.setAddress(createDskhohangDto.getAddress());
            dskhohang.setContact(createDskhohangDto.getContact());
            return dskhohangRepository.save(dskhohang);
        } catch (Exception e) {
            log.error("Error creating khohang", e);
            return null;
        }
    }

    @Override
    public Dskhohang updateDskhohang( Integer khohangId,CreateDskhohangDto createDskhohangDto) {
        try {
            Dskhohang dskhohang = dskhohangRepository.findById(khohangId).orElseThrow(() -> new EntityNotFoundException("Khohang not found for id: " + khohangId));
            dskhohang.setName(createDskhohangDto.getName());
            dskhohang.setAddress(createDskhohangDto.getAddress());
            dskhohang.setContact(createDskhohangDto.getContact());
            dskhohang.setUpdatedAt(LocalDateTime.now());
            return dskhohangRepository.save(dskhohang);
        } catch (Exception e) {
            log.error("Error updating khohang", e);
            return null;
        }
    }

    @Override
    public void deleteDskhohangsByIds(List<Integer> ids) {
        List<Dskhohang> khohangsToDelete = dskhohangRepository.findAllById(ids);

        List<Integer> existingIds = khohangsToDelete.stream()
                .map(Dskhohang::getKhohangId)
                .toList();

        List<Integer> notFoundIds = ids.stream()
                .filter(id -> !existingIds.contains(id))
                .toList();

        if (!notFoundIds.isEmpty()) {
            throw new EntityNotFoundException("Khohangs not found for ids: " + notFoundIds);
        }

        khohangsToDelete.forEach(dskhohang -> dskhohang.setStatus(false));
        dskhohangRepository.saveAll(khohangsToDelete);
    }

    @Override
    public Dskhohang getDskhohangById(Integer khohangId) {
        return dskhohangRepository.findById(khohangId).orElseThrow(() -> new EntityNotFoundException("Khohang not found for id: " + khohangId));
    }
}
