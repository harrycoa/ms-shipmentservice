package com.pe.appventas.msshippingservice.service;

import com.pe.appventas.msshippingservice.dto.ProviderSelector;
import com.pe.appventas.msshippingservice.dto.ShipmentOrderRequest;
import com.pe.appventas.msshippingservice.entities.Address;
import com.pe.appventas.msshippingservice.entities.Shipment;
import com.pe.appventas.msshippingservice.repositories.ShipmentRepository;
import com.pe.appventas.msshippingservice.util.enums.OrderShippingStatusEnum;
import com.pe.appventas.msshippingservice.util.enums.converter.EntityDtoConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipmentService {
    @Autowired
    private ProviderSelector providerSelector;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private EntityDtoConverter converter;

    public Shipment createShipment(ShipmentOrderRequest shipmentOrder) {

        Address address = converter.convertDtoToEntity(shipmentOrder.getShippingAddress());

        Shipment newShipment = Shipment.builder().provider(providerSelector.select())
                .orderId(shipmentOrder.getOrderId())
                .receiptName(shipmentOrder.getName())
                .receiptEmail(shipmentOrder.getReceiptEmail())
                .address(address)
                .trackingId(providerSelector.generateTrackingId())
                .price(100.00)
                .status(OrderShippingStatusEnum.WAITING_FOR_MAIL_CARRIER.name())
                .build();

        return shipmentRepository.save(newShipment);
    }

    public List<Shipment> retrieveOrdersNotDelivered() {
        return shipmentRepository.findByStatusIsNotContaining(OrderShippingStatusEnum.DELIVERED.name());
    }

    public void updateShipmentStatus(long shipmentId, String status) {
        shipmentRepository.updateStatus(shipmentId, status);
    }

    public void updateShipment(Shipment shipment) {
        shipmentRepository.save(shipment);
    }

    public Shipment retrieveShippingByTrackingId(String trackingId) {
        return shipmentRepository.findByTrackingId(trackingId);
    }

    public List<Shipment> retrieveShipmentsByStatus(String status) {
        return shipmentRepository.findByStatus(status);
    }

    public List<Shipment> retrieveAllShipments() {
        return shipmentRepository.findAll();
    }
}
