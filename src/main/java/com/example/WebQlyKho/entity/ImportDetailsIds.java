package com.example.WebQlyKho.entity;

import java.io.Serializable;
import java.util.Objects;

public class ImportDetailsIds implements Serializable {
    private Integer productId;
    private Integer invoicesId;

    public ImportDetailsIds() {}

    public ImportDetailsIds(Integer productId, Integer invoicesId) {
        this.productId = productId;
        this.invoicesId = invoicesId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getInvoicesId() {
        return invoicesId;
    }

    public void setInvoicesId(Integer invoicesId) {
        this.invoicesId = invoicesId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImportDetailsIds that = (ImportDetailsIds) o;
        return Objects.equals(productId, that.productId) &&
                Objects.equals(invoicesId, that.invoicesId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, invoicesId);
    }
}
