package com.geekbrains.spring.web.core.dto;

import com.geekbrains.spring.web.api.dto.OrderItemDto;
import lombok.Data;
import java.util.List;

@Data
public class OrderDto {
    private Long id;
    private String username;
    private List<OrderItemDto> items;
    private Integer totalPrice;
    private String address;
    private String phone;
}
