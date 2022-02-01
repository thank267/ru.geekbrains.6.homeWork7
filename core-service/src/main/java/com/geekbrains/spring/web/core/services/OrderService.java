package com.geekbrains.spring.web.core.services;

import com.geekbrains.spring.web.api.dto.Cart;
import com.geekbrains.spring.web.api.dto.StringResponse;
import com.geekbrains.spring.web.api.exceptions.ResourceNotFoundException;
import com.geekbrains.spring.web.core.dto.OrderDetailsDto;
import com.geekbrains.spring.web.core.entities.Order;
import com.geekbrains.spring.web.core.entities.OrderItem;
import com.geekbrains.spring.web.core.repositories.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final RestTemplate cartTemplate;
    private final OrdersRepository ordersRepository;
    private final ProductsService productsService;

    @Transactional
    public void createOrder(String username, OrderDetailsDto orderDetailsDto) {
        final String basePath = "http://localhost:8187/web-market-cart/api/v1/cart/";
        StringResponse cartKey = cartTemplate.getForObject(basePath+"generate", StringResponse.class);
        Cart currentCart = cartTemplate.getForObject(basePath+cartKey.getValue(), Cart.class);
        Order order = new Order();
        order.setAddress(orderDetailsDto.getAddress());
        order.setPhone(orderDetailsDto.getPhone());
        order.setUsername(username);
        order.setTotalPrice(currentCart.getTotalPrice());
        List<OrderItem> items = currentCart.getItems().stream()
                .map(o -> {
                    OrderItem item = new OrderItem();
                    item.setOrder(order);
                    item.setQuantity(o.getQuantity());
                    item.setPricePerProduct(o.getPricePerProduct());
                    item.setPrice(o.getPrice());
                    item.setProduct(productsService.findById(o.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product not found")));
                    return item;
                }).collect(Collectors.toList());
        order.setItems(items);
        ordersRepository.save(order);
        cartTemplate.getForObject(basePath+cartKey.getValue()+"/clear", Cart.class);
    }

    public List<Order> findOrdersByUsername(String username) {
        return ordersRepository.findAllByUsername(username);
    }
}
