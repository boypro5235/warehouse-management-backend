package com.example.WebQlyKho.service.impl;

import com.example.WebQlyKho.dto.request.DeliveryInfoRequest;
import com.example.WebQlyKho.entity.DeliveryInfo;
import com.example.WebQlyKho.repository.DeliveryInfoRepository;
import com.example.WebQlyKho.repository.UserRepository;
import com.example.WebQlyKho.service.DeliveryInfoService;
import com.example.WebQlyKho.utils.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeliveryInfoServiceimpl implements DeliveryInfoService {

    @Autowired
    private DeliveryInfoRepository deliveryInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public DeliveryInfo createDeliveryInfo(DeliveryInfoRequest deliveryInfo, HttpServletRequest request) {
        DeliveryInfo deliveryInfo1 = DeliveryInfo.builder()
                .deliveryStatus(deliveryInfo.getDeliveryStatus())
                .status(deliveryInfo.isStatus())
                .user(userRepository.findById(jwtTokenProvider.getUserIdFromToken(request))
                        .orElseThrow(() -> new EntityNotFoundException("User not found")))
                .createdAt(LocalDateTime.now())
                .build();
        return deliveryInfoRepository.save(deliveryInfo1);
    }

    @Override
    public DeliveryInfo updateDeliveryInfo(Integer deliveryId, DeliveryInfoRequest deliveryInfo, HttpServletRequest request) {
        DeliveryInfo existingDeliveryInfo = deliveryInfoRepository.findById(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("DeliveryInfo not found with id: " + deliveryId));

        existingDeliveryInfo.setDeliveryStatus(deliveryInfo.getDeliveryStatus());
        existingDeliveryInfo.setStatus(deliveryInfo.isStatus());
        existingDeliveryInfo.setUpdatedAt(LocalDateTime.now());
        existingDeliveryInfo.setUser(userRepository.findById(jwtTokenProvider.getUserIdFromToken(request))
                .orElseThrow(() -> new EntityNotFoundException("User not found")));

        return deliveryInfoRepository.save(existingDeliveryInfo);
    }

    public void deleteDeliveryInfo(Integer deliveryId) {
        if (!deliveryInfoRepository.existsById(deliveryId)) {
            throw new EntityNotFoundException("DeliveryInfo not found with id: " + deliveryId);
        }
        deliveryInfoRepository.deleteById(deliveryId);
    }

    public DeliveryInfo getDeliveryInfoById(Integer deliveryId) {
        return deliveryInfoRepository.findById(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("DeliveryInfo not found with id: " + deliveryId));
    }

    public List<DeliveryInfo> getAllDeliveryInfos() {
        return deliveryInfoRepository.findAll();
    }
}
