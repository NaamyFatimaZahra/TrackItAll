package org.trackitall.trackitall.supply.service;

import org.trackitall.trackitall.supply.dto.SupplyOrderRequestDTO;
import org.trackitall.trackitall.supply.dto.SupplyOrderRequestDTO;
import org.trackitall.trackitall.supply.dto.SupplyOrderRequestSimpleDTO;
import org.trackitall.trackitall.supply.dto.SupplyOrderResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ISupplyOrderService {

    SupplyOrderResponseDTO createSupplyOrder(SupplyOrderRequestDTO supplyOrderDTO);

    void deleteSupplyOrder(Long id);

    Page<SupplyOrderResponseDTO> getAllSupplyOrders(Pageable pageable);

    SupplyOrderResponseDTO updateOrderStatus(Long id, String status);

    SupplyOrderResponseDTO getSupplyOrderById(Long id);
}