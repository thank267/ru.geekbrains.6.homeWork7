package com.geekbrains.spring.web.cart;

import com.geekbrains.spring.web.api.dto.Cart;
import com.geekbrains.spring.web.api.dto.ProductDto;
import com.geekbrains.spring.web.cart.services.CartService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.CoreMatchers.is;

@SpringBootTest
@AutoConfigureMockMvc
public class CartControllerTest {

    final String cartName =  "testCart";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CartService cartService;

    @Test
    public void testAdd() throws Exception {

        ProductDto pdt = new ProductDto(1L,"test", 10);
        Mockito.doNothing().when(cartService).addToCart(cartName,pdt.getId());
        Mockito.when(cartService.getCartUuidFromSuffix(cartName)).thenReturn(cartName);
        mvc
            .perform(
                    get("/api/v1/cart/"+cartName+"/add/"+pdt.getId())
                            .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk());
        Mockito.verify(cartService, Mockito.times(1)).addToCart(cartName,pdt.getId());
        Mockito.verify(cartService, Mockito.times(1)).getCartUuidFromSuffix(cartName);
    }

    @Test
    public void testGetCart() throws Exception {

        ProductDto pdt = new ProductDto(1L,"test", 10);
        Cart cart = new Cart();
        cart.add(pdt);

        Mockito.when(cartService.getCurrentCart(cartName)).thenReturn(cart);
        Mockito.when(cartService.getCartUuidFromSuffix(cartName)).thenReturn(cartName);
        mvc
                .perform(
                        get("/api/v1/cart/"+cartName)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.totalPrice",is(cart.getTotalPrice())));
        Mockito.verify(cartService, Mockito.times(1)).getCurrentCart(cartName);
    }
}
