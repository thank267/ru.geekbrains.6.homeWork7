package com.geekbrains.spring.web.cart;

import com.geekbrains.spring.web.api.dto.Cart;
import com.geekbrains.spring.web.api.dto.OrderItemDto;
import com.geekbrains.spring.web.api.dto.ProductDto;
import com.geekbrains.spring.web.cart.services.CartService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
@TestConfiguration
@TestPropertySource(properties = {
        "utils.cart.prefix=testValue",
})
public class CartServiceTest {

    final String cartName =  "testCart";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private CartService cartService;

    @Value("${utils.cart.prefix}")
    private String cartPrefix;

    @BeforeEach
    public void setUp() {
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Mockito.doNothing().when(valueOperations).set(anyString(), anyString());
    }

    @Test
    public void cartPrefixShouldBeTestValue() {
         assertEquals("testValue", cartPrefix);
    }

    @Test
    public void testAddCart() {
        ProductDto pdt = new ProductDto(1L,"test", 10);
        OrderItemDto oi = new OrderItemDto(pdt);
        Cart cart = new Cart();
        cart.add(pdt);

        final String restPath = "http://localhost:8189/web-market-core/api/v1/products/"+pdt.getId();

        Mockito
            .when(restTemplate.getForObject(restPath, ProductDto.class))
            .thenReturn(pdt);

        Mockito
            .when(redisTemplate.hasKey(cartName))
            .thenReturn(true);

        Mockito
            .when(valueOperations.get(cartName))
            .thenReturn(cart);

        Assertions.assertNotNull(cartService);
        cartService.addToCart(cartName,pdt.getId());
        Mockito.verify(restTemplate, Mockito.times(1)).getForObject(restPath, ProductDto.class);
        Mockito.verify(redisTemplate, Mockito.times(1)).hasKey(cartName);
        Mockito.verify(valueOperations, Mockito.times(1)).get(cartName);
    }

    @Test
    public void testClearCart() {
        ProductDto pdt = new ProductDto(1L,"test", 10);
        OrderItemDto oi = new OrderItemDto(pdt);
        Cart cart = new Cart();
        cart.add(pdt);

        Mockito
                .when(redisTemplate.hasKey(cartName))
                .thenReturn(true);

        Mockito
                .when(valueOperations.get(cartName))
                .thenReturn(cart);

        cartService.clearCart(cartName);
        Mockito.verify(redisTemplate, Mockito.times(1)).hasKey(cartName);
        Mockito.verify(valueOperations, Mockito.times(1)).set(cartName, cart);
    }
}
