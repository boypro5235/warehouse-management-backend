package com.example.WebQlyKho.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrderDetailsIds implements Serializable {

    private Integer productId;
    private Integer orderId;

    public OrderDetailsIds() {}

    public OrderDetailsIds(Integer productsId, Integer orderId) {
        this.productId = productsId;
        this.orderId = orderId;
    }

    public Integer getProductsId() {
        return productId;
    }

    public void setProductsId(Integer productsId) {
        this.productId = productsId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDetailsIds that = (OrderDetailsIds) o;
        return Objects.equals(productId, that.productId) &&
                Objects.equals(orderId, that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, orderId);
    }
}

