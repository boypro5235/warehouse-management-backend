package com.example.WebQlyKho.service;

import com.example.WebQlyKho.dto.request.DeliveryInfoRequest;
import com.example.WebQlyKho.entity.DeliveryInfo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface DeliveryInfoService {
    DeliveryInfo createDeliveryInfo(DeliveryInfoRequest deliveryInfo, HttpServletRequest request);
    DeliveryInfo updateDeliveryInfo(Integer deliveryId, DeliveryInfoRequest DeliveryInfoRequest, HttpServletRequest request);
    void deleteDeliveryInfo(Integer deliveryId);
    DeliveryInfo getDeliveryInfoById(Integer deliveryId);
    List<DeliveryInfo> getAllDeliveryInfos();
}
