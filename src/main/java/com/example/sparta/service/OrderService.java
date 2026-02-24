package com.example.sparta.service;

import com.example.sparta.dto.OrderLineRequest;
import com.example.sparta.entity.Order;
import com.example.sparta.entity.OrderLine;
import com.example.sparta.entity.Product;
import com.example.sparta.repository.OrderLineRepository;
import com.example.sparta.repository.OrderRepository;
import com.example.sparta.repository.ProductRepository;
import com.example.sparta.dto.OrderCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderLineRepository orderLineRepository;


    @Transactional
    public Order create(OrderCreateRequest request) {
        // 주문 생성
        Order order = orderRepository.save(new Order(request.getTotalPrice()));

        // 주문 요청한 상품의 ID 리스트
        List<Long> productIds = request.getOrderLines().stream()
                .map(OrderLineRequest::getProductId)
                .toList();

        // 주문 요청한 상품 리스트 조회(OrderServiceSupport로 저장할 데이터 빌드)
        List<Product> products = productRepository.findByIdIn(productIds);
        List<OrderLine> orderLineList = OrderServiceSupport.buildOrderLines(products, request.getOrderLines(), order);
        orderLineRepository.saveAll(orderLineList);
        return order;

        // 이렇게 되면 다른 클래스에 의존하지 않는 상태가 되어, 기본적인 CRUD만으로 구성 됐기에 테스트 할 필요성은 없어짐
    }
}