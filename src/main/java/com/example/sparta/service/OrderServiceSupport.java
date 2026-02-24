package com.example.sparta.service;

import com.example.sparta.dto.OrderLineRequest;
import com.example.sparta.entity.Order;
import com.example.sparta.entity.OrderLine;
import com.example.sparta.entity.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderServiceSupport {
    // 스프링 프레임 워크에 의존하지 않는 상태이므로 POJO 테스트로 비즈니스 로직을 검증한다
    // @SpringBootTest 필요없고, Mock도 필요없으므로 빠르고 실제 객체의 흐름을 검증할 수 있다는 장점
    public static List<OrderLine> buildOrderLines(
            List<Product> products,
            List<OrderLineRequest> orderLineRequests,
            Order order
    ) {
        if (products.size() != orderLineRequests.size()) {
            throw new RuntimeException("존재하지 않는 상품은 주문할 수 없습니다!");
        }

        List<OrderLine> orderLines = new ArrayList<>();
        for (Product product : products) {
            for (OrderLineRequest olr : orderLineRequests) {
                if (Objects.equals(product.getId(), olr.getProductId())) {
                    product.purchased(olr.getAmount());
                    orderLines.add(new OrderLine(order, product, olr.getAmount()));
                }
            }
        }
        return orderLines;
    }
}
