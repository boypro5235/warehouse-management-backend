package com.example.WebQlyKho.controller;

import com.example.WebQlyKho.dto.request.DeliveryInfoRequest;
import com.example.WebQlyKho.dto.response.APIResponse;
import com.example.WebQlyKho.entity.DeliveryInfo;
import com.example.WebQlyKho.service.DeliveryInfoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/delivery_info")
public class DeliveryInfoController {
    @Autowired
    private DeliveryInfoService deliveryInfoService;

    @PostMapping
    public ResponseEntity<Object> createDeliveryInfo(@RequestBody DeliveryInfoRequest deliveryInfo, HttpServletRequest request) {
        DeliveryInfo createdDeliveryInfo = deliveryInfoService.createDeliveryInfo(deliveryInfo, request);
        return APIResponse.responseBuilder(createdDeliveryInfo, "Create delivery info successfully", HttpStatus.OK);
    }

    @PutMapping("/{deliveryId}")
    public ResponseEntity<Object> updateDeliveryInfo(@PathVariable Integer deliveryId, @RequestBody DeliveryInfoRequest deliveryInfo, HttpServletRequest request) {
        DeliveryInfo updatedDeliveryInfo = deliveryInfoService.updateDeliveryInfo(deliveryId, deliveryInfo, request);
        return APIResponse.responseBuilder(updatedDeliveryInfo, "Update delivery info successfully", HttpStatus.OK);
    }

    @DeleteMapping("/{deliveryId}")
    public ResponseEntity<Object> deleteDeliveryInfo(@PathVariable Integer deliveryId) {
        deliveryInfoService.deleteDeliveryInfo(deliveryId);
        return APIResponse.responseBuilder(null, "Delete delivery info successfully", HttpStatus.OK);
    }

    @GetMapping("/{deliveryId}")
    public ResponseEntity<Object> getDeliveryInfoById(@PathVariable Integer deliveryId) {
        DeliveryInfo deliveryInfo = deliveryInfoService.getDeliveryInfoById(deliveryId);
        return APIResponse.responseBuilder(deliveryInfo, "Fetch delivery info successfully", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Object> getAllDeliveryInfos() {
        List<DeliveryInfo> deliveryInfos = deliveryInfoService.getAllDeliveryInfos();
        return APIResponse.responseBuilder(deliveryInfos, "Fetch deliveries info successfully", HttpStatus.OK);
    }
}
