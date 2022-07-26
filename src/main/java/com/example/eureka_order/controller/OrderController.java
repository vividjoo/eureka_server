package com.example.eureka_order.controller;

import com.example.eureka_order.dto.OrderDto;
import com.example.eureka_order.jpa.OrderEntity;
import com.example.eureka_order.jpa.OrderRepository;
import com.example.eureka_order.service.OrderService;
import com.example.eureka_order.vo.RequestOrder;
import com.example.eureka_order.vo.ResponseOrder;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order_service")
public class OrderController {

    Environment env;
    OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService, Environment env) {
        this.orderService = orderService;
        this.env = env;
    }

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's working in Order Service on PORT %s", env.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId, @RequestBody RequestOrder orderDetails) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = mapper.map(orderDetails, OrderDto.class);

        orderDto.setUserId(userId);

        OrderDto createOrder = orderService.createOrder(orderDto);

        ResponseOrder responseOrder = mapper.map(createOrder, ResponseOrder.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);

    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("userId") String userId) {

        Iterable<OrderEntity> orderList = orderService.getOrdersByUserId(userId);
        List<ResponseOrder> result = new ArrayList<>();

        orderList.forEach((e) -> {
            System.out.println("e: " +e);
            result.add(new ModelMapper().map(e, ResponseOrder.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);

    }

}
