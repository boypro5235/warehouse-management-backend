package com.example.WebQlyKho.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinancialReportDTO {
    private String tg;
    private Double tongThu;
    private Double tongChi;
    private Double loiNhuan;

    public FinancialReportDTO(String tg, Double tongThu, Double tongChi, Double loiNhuan) {
        this.tg = tg;
        this.tongThu = tongThu;
        this.tongChi = tongChi;
        this.loiNhuan = loiNhuan;
    }
}
