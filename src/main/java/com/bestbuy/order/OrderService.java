package com.bestbuy.order;

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

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findOrderByUserId(userId);
    }

    @Transactional
    public Order createOrder(Long userId, List<OrderItem> items) {
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
        return orderRepository.save(order);
    }
}
