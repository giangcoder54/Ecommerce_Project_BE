package com.ecommerce.customer.controller;

import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.model.Voucher;
import com.ecommerce.library.repository.ShoppingCartRepository;
import com.ecommerce.library.repository.VoucherRepository;
import com.ecommerce.library.service.VoucherService;
import com.ecommerce.library.service.impl.VoucherServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class VoucherController {
    @Autowired
    private VoucherService voucherService;
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @PostMapping("/apply-voucher")
    public String applyVoucher(
            @RequestParam String voucherCode,
            @RequestParam double totalPrice,
            @RequestParam Long customerId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Retrieve the voucher from the database based on the voucher code
            Voucher voucher = voucherRepository.findByCode(voucherCode);

            // Apply the voucher and calculate the discounted price
            double discountedPrice = voucherService.applyVoucher(voucher, totalPrice);

            // Retrieve the shopping cart from the database
            ShoppingCart shoppingCart = shoppingCartRepository.findShoppingCartByCustomerId(customerId);

            if (shoppingCart == null) {
                return "redirect:/cart"; // or any other error handling mechanism
            }

            // Update the discountAmount in the shopping cart
            shoppingCart.setDiscountPrice(discountedPrice);
            double total_Price = shoppingCart.getTotalPrices() - discountedPrice;
            shoppingCart.setTotalPrices(total_Price);

            shoppingCartRepository.save(shoppingCart);

            return "redirect:/cart";
        } catch (VoucherServiceImpl.VoucherException e) {
            // Handle the voucher exception
            System.out.println(e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/"; // Replace "your-form-page" with the actual page where the form is located
        } catch (IllegalArgumentException e) {
            // Handle other exceptions
            return "redirect:/cart";
        }
    }




}