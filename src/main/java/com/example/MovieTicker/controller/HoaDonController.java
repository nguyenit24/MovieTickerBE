package com.example.MovieTicker.controller;

import com.example.MovieTicker.Model.ResponseModel;
import com.example.MovieTicker.config.PaymentConfig;
import com.example.MovieTicker.request.PaymentRequest;
import com.example.MovieTicker.response.ApiResponse;
import com.example.MovieTicker.response.CreateMomoResponse;
import com.example.MovieTicker.service.HoaDonService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/payment")
public class HoaDonController {
    @Autowired
    private HoaDonService invoiceService;

    @PostMapping("/vn_pay/create")
    public ApiResponse<?> createPayment(@RequestBody PaymentRequest paymentRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String paymentUrl = invoiceService.createVnPayRequest(paymentRequest, request, response);
        return new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Tao thanh toan thanh cong",
                paymentUrl
        );
    }

    @GetMapping("/vn_pay/payment_info")
    public ApiResponse<?> getPaymentInfo(
            @RequestParam("vnp_Amount") String amount,
            @RequestParam("vnp_BankCode") String bankCode,
            @RequestParam("vnp_OrderInfo") String orderInfo,
            @RequestParam("vnp_ResponseCode") String response_Code
    ) throws IOException {
        if (response_Code.equals("00")) {
            return new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Tao thanh toan thanh cong",
                    null
            );
        }
        return new ApiResponse<>(
                HttpStatus.NOT_FOUND.value(),
                "Tao thanh toan that bai",
                null
        );
    }

    @PostMapping("/momo/create")
    public CreateMomoResponse createMomo(@RequestBody PaymentRequest paymentRequest) {
        return invoiceService.createMoMoQR(paymentRequest);
    }

    @GetMapping("/ipn_handler")
    public ApiResponse<?> ipnHandler(@RequestParam Map<String, String> request) {
        int resultCode = Integer.parseInt(request.get("resultCode"));
        if (resultCode == 0) {
            return new ApiResponse<>(
                    HttpStatus.CREATED.value(),
                    "Thanh cong",
                    null
            );
        }
        return new ApiResponse<>(
                HttpStatus.NOT_FOUND.value(),
                "Co loi xay ra",
                null
        );
    }
}
