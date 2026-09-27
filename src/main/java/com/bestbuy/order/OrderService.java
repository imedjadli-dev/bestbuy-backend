package com.bestbuy.order;

import com.bestbuy.order.dto.OrderItemResponse;
import com.bestbuy.order.dto.OrderResponse;
import com.bestbuy.product.Product;
import com.bestbuy.product.ProductNotFoundException;
import com.bestbuy.product.ProductRepository;
import com.bestbuy.user.User;
import com.bestbuy.user.UserNotFoundException;
import com.bestbuy.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(
            OrderRepository orderRepository,
            UserRepository userRepository,
            ProductRepository productRepository
    ) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return mapToResponse(order);
    }

    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderRepository.findOrderByUserId(userId).stream().map(this::mapToResponse).toList();
    }

    @Transactional
    public OrderResponse createOrder(Long userId, List<OrderItem> items) {
        User user =
                userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItem item : items) {
            Product product =
                    productRepository.findById(item.getProduct().getId()).orElseThrow(() -> new ProductNotFoundException(item.getProduct().getId()));

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new InsufficientStockException(product.getName(),
                        item.getQuantity(), product.getStockQuantity());
            }

            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());

            item.setUnit_price(product.getPrice());
            item.setProduct(product);

            BigDecimal itemTotal =
                    product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            order.addOrderItem(item);
        }

        order.setTotalAmount(totalAmount);
        return mapToResponse(orderRepository.save(order));
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .unitPrice(item.getUnit_price())
                        .quantity(item.getQuantity())
                        .build()
                ).toList();

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .items(itemResponses)
                .build();
    }
}
