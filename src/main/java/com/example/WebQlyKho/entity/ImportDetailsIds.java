package com.example.WebQlyKho.entity;

import java.io.Serializable;
import java.util.Objects;

public class ImportDetailsIds implements Serializable {
    private Integer product;
    private Integer importInvoice;

    public ImportDetailsIds() {}

    public ImportDetailsIds(Integer productId, Integer invoicesId) {
        this.product = productId;
        this.product = invoicesId;
    }

    public Integer getProductId() {
        return product;
    }

    public void setProductId(Integer productId) {
        this.product = productId;
    }

    public Integer getInvoicesId() {
        return importInvoice;
    }

    public void setInvoicesId(Integer invoicesId) {
        this.importInvoice = invoicesId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImportDetailsIds that = (ImportDetailsIds) o;
        return Objects.equals(product, that.product) &&
                Objects.equals(importInvoice, that.importInvoice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, importInvoice);
    }
}
