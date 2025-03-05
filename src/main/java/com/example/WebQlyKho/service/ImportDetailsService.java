package com.example.WebQlyKho.service;


import com.example.WebQlyKho.dto.request.ImportDetailRequestDto;
import com.example.WebQlyKho.entity.ImportDetails;
import com.example.WebQlyKho.entity.ImportDetailsIds;

import java.util.Map;

public interface ImportDetailsService {
    Map<String, Object> searchImportDetails(Integer khohangId, int page, int size);
    ImportDetails getImportDetailsById(ImportDetailsIds id);
    ImportDetails createImportDetails(ImportDetailRequestDto importDetailRequestDto);
    ImportDetails updateImportDetails(ImportDetailsIds id, ImportDetailRequestDto importDetailRequestDto);
    void deleteImportDetailsById(ImportDetailsIds id);
}
