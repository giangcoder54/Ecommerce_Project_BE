package com.ecommerce.customer.controller;

import com.ecommerce.library.dto.CheckoutDto;
import com.ecommerce.library.model.CartItem;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Order;
import com.ecommerce.library.model.OrderDetail;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.repository.OrderRepository;
import com.ecommerce.library.repository.ShoppingCartRepository;
import com.ecommerce.library.service.CustomerService;
import com.ecommerce.library.service.OrderService;
import com.ecommerce.library.service.ProductService;
import com.ecommerce.library.service.ShoppingCartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/checkout")
    public String checkout(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);

        model.addAttribute("customer", customer);
        ShoppingCart cart = customer.getShoppingCart();
        model.addAttribute("cart", cart);

        return "checkout";
    }

    @PostMapping("/check-out")
    public String handleCheckout(CheckoutDto checkoutDTO, Principal principal) {
        if(principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);
        ShoppingCart cart = shoppingCartRepository.findShoppingCartByCustomerId(customer.getId());
        if (cart == null) {
            return "redirect:/cart";
        }

        Order newOrder = createOrderFromCart(cart, checkoutDTO);
        orderRepository.save(newOrder);
        shoppingCartService.clearCart(customer);

        return "redirect:/";
    }

    private Order createOrderFromCart(ShoppingCart cart, CheckoutDto checkoutDTO) {
        Order order = new Order();
        order.setOrderDate(new Date());
        order.setDeliveryDate(checkoutDTO.getDeliveryDate());
        order.setOrderStatus("PENDING");
        order.setCustomer(cart.getCustomer());
        order.setNotes(checkoutDTO.getNotes());

        List<OrderDetail> orderDetails = new ArrayList<>();
        double totalPrice = 0;

        for (CartItem cartItem : cart.getCartItem()) {
            Product product = productService.getProductById(cartItem.getProduct().getId());
            if (product == null) {
                continue; // or handle error
            }

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setProduct(product);
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setUnitPrice(product.getCostPrice());
            orderDetail.setTotalPrice(cartItem.getQuantity() * product.getCostPrice());
            orderDetail.setOrder(order);

            totalPrice += orderDetail.getTotalPrice();
            orderDetails.add(orderDetail);
        }

        order.setTotalPrice(totalPrice);
        order.setOrderDetailList(orderDetails);
        return order;
    }

    @GetMapping("/order")
    public String order(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);
        List<Order> orderList = customer.getOrders();
        if (orderList.isEmpty()) {
            model.addAttribute("check", "No orders found.");
        } else {
            model.addAttribute("orders", orderList);
        }
        return "order";
    }

    @GetMapping("/save-order")
    public String saveOrder(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);
        ShoppingCart cart = customer.getShoppingCart();
        orderService.saveOrder(cart);
        return "redirect:/order";
    }

    @PostMapping("/order/accept")
    public String acceptOrder(@RequestParam("orderId") Long orderId) {
        orderService.acceptOrder(orderId);
        return "redirect:/order";
    }

    @PostMapping("/order/cancel")
    public String cancelOrder(@RequestParam("orderId") Long orderId) {
        orderService.cancelOrder(orderId);
        return "redirect:/order";
    }

}
