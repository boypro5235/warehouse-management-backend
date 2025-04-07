package com.example.WebQlyKho.repository;

import com.example.WebQlyKho.dto.FinancialReportDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ReportRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<FinancialReportDTO> getBaoCaoTaiChinhNgay(boolean chiTiet) {
        String sql = "SELECT tg, tong_thu, tong_chi, loi_nhuan FROM qlkhohang.fn_bao_cao_tai_chinh_ngay(:chiTiet)";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("chiTiet", chiTiet);

        List<Object[]> results = query.getResultList();

        List<FinancialReportDTO> reports = new ArrayList<>();
        for (Object[] row : results) {
            reports.add(new FinancialReportDTO(
                    (String) row[0],
                    ((Number) row[1]).doubleValue(),
                    ((Number) row[2]).doubleValue(),
                    ((Number) row[3]).doubleValue()
            ));
        }

        return reports;
    }
    public List<FinancialReportDTO> getBaoCaoTaiChinhThang() {
        String sql = "SELECT tg, tong_thu, tong_chi, loi_nhuan FROM qlkhohang.fn_bao_cao_tai_chinh_thang()";
        Query query = entityManager.createNativeQuery(sql);

        List<Object[]> results = query.getResultList();

        List<FinancialReportDTO> reports = new ArrayList<>();
        for (Object[] row : results) {
            reports.add(new FinancialReportDTO(
                    (String) row[0],
                    ((Number) row[1]).doubleValue(),
                    ((Number) row[2]).doubleValue(),
                    ((Number) row[3]).doubleValue()
            ));
        }

        return reports;
    }

    public List<FinancialReportDTO> getBaoCaoTaiChinhNam() {
        Query query = entityManager.createNativeQuery("SELECT tg, tong_thu, tong_chi, loi_nhuan FROM qlkhohang.fn_bao_cao_tai_chinh_nam()");
        List<Object[]> results = query.getResultList();

        List<FinancialReportDTO> reports = new ArrayList<>();
        for (Object[] row : results) {
            reports.add(new FinancialReportDTO(
                    (String) row[0],
                    ((Number) row[1]).doubleValue(),
                    ((Number) row[2]).doubleValue(),
                    ((Number) row[3]).doubleValue()
            ));
        }
        return reports;
    }

}
