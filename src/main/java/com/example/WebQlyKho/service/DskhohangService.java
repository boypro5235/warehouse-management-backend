package com.example.WebQlyKho.service;

import com.example.WebQlyKho.dto.request.CreateDskhohangDto;
import com.example.WebQlyKho.entity.Dskhohang;

import java.util.List;
import java.util.Map;

public interface DskhohangService {
    Map<String, Object> searchDskhohangs(String searchText, int page, int size);
    Dskhohang createDskhohang(CreateDskhohangDto createDskhohangDto);
    Dskhohang updateDskhohang( Integer khohangId,CreateDskhohangDto createDskhohangDto);
    Dskhohang getDskhohangById(Integer khohangId);
    void deleteDskhohangsByIds(List<Integer> ids);
}
