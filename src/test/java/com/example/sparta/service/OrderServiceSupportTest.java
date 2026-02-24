package com.example.sparta.service;

import com.example.sparta.dto.OrderLineRequest;
import com.example.sparta.entity.Order;
import com.example.sparta.entity.OrderLine;
import com.example.sparta.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class OrderServiceSupportTest {
    private Product product1;
    private Product product2;
    private Order order;

    @BeforeEach
    void setUp() {
        product1 = new Product(
                1L, "상품A", "설명1", 10000L, 10L, 0L
        );

        product2 = new Product(
                2L, "상품B", "설명2", 5000L, 20L, 0L
        );

        order = new Order(35000L);
    }

    @Test
    void 정상_주문_시_재고_감소_및_판매_횟수_증가() {
        // given
        List<Product> products = List.of(product1, product2);

        OrderLineRequest request1 = new OrderLineRequest(1L, 2L);
        OrderLineRequest request2 = new OrderLineRequest(2L, 3L);

        List<OrderLineRequest> requests = List.of(request1, request2);

        Order order = new Order(35000L);

        // when
        List<OrderLine> result =
                OrderServiceSupport.buildOrderLines(products, requests, order);

        // then
        assertThat(result).hasSize(2);

        // 재고 감소 확인
        assertThat(product1.getAmount()).isEqualTo(8L);
        assertThat(product2.getAmount()).isEqualTo(17L);

        // 판매 횟수 증가 확인
        assertThat(product1.getSaleCount()).isEqualTo(2L);
        assertThat(product2.getSaleCount()).isEqualTo(3L);

        // Order 연결 확인
        assertThat(result)
                .extracting(OrderLine::getOrder)
                .containsOnly(order);
    }

    @Test
    void 존재하지_않는_상품_요청_시_예외_발생() {
        List<Product> products = List.of(product1);

        // 요청은 2개, 상품은 1개
        OrderLineRequest request1 = new OrderLineRequest(1L, 2L);
        OrderLineRequest request2 = new OrderLineRequest(99L, 1L);

        List<OrderLineRequest> requests = List.of(request1, request2);

        Order order = new Order(20000L);

        // when & then
        assertThatThrownBy(() ->
                OrderServiceSupport.buildOrderLines(products, requests, order)
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("존재하지 않는 상품");
    }

    @Test
    void 재고_부족_시_예외_발생() {
        // when & then
        assertThatThrownBy(() -> product1.purchased(100L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("재고가 부족");
    }
}