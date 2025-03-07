package com.example.WebQlyKho.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrderDetailsIds implements Serializable {

    private Integer product;
    private Integer order;

    public OrderDetailsIds() {}

    public OrderDetailsIds(Integer productsId, Integer orderId) {
        this.product = productsId;
        this.order = orderId;
    }

    public Integer getProductsId() {
        return product;
    }

    public void setProductsId(Integer productsId) {
        this.product = productsId;
    }

    public Integer getOrderId() {
        return order;
    }

    public void setOrderId(Integer orderId) {
        this.order = orderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDetailsIds that = (OrderDetailsIds) o;
        return Objects.equals(product, that.product) &&
                Objects.equals(order, that.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, order);
    }
}

