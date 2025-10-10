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
    public ApiResponse<?> createPaymentVnPay(@RequestBody PaymentRequest paymentRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String paymentUrl = invoiceService.createVnPayRequest(paymentRequest, request, response);
        try {
            return new ApiResponse<>(
                    HttpStatus.CREATED.value(),
                    "Tao thanh toan thanh cong",
                    paymentUrl
            );
        } catch (Exception e) {
            return new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(),
                    "Tao thanh toan that bai",
                    e.getMessage()
            );
        }

    }

    @GetMapping("/vn_pay/payment_info")
    public ApiResponse<?> getPaymentInfoVnPay(
            @RequestParam("vnp_TransactionNo") String transNo,
            @RequestParam("vnp_PayDate") String transDate,
            @RequestParam("vnp_ResponseCode") String responseCode
    ) throws IOException {
        if (responseCode.equals("00")) {
            Map<String, Object> data = new HashMap<>();
            data.put("transactionNo", transNo);
            data.put("transactionDate", transDate);
            data.put("responseCode", responseCode);
            return new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Tao thanh toan thanh cong",
                    data
            );
        }
        return new ApiResponse<>(
                HttpStatus.NOT_FOUND.value(),
                "Tao thanh toan that bai",
                null
        );
    }

    @GetMapping("/vn_pay/refund")
    public ApiResponse<?> getPaymentRefundVnPay(
            @RequestBody PaymentRequest paymentRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        String paymentUrl = invoiceService.refundVnPay(paymentRequest, request);
        try {
            return new ApiResponse<>(
                    HttpStatus.CREATED.value(),
                    "Hoan tien thanh cong",
                    paymentUrl
            );
        } catch (Exception e) {
            return new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(),
                    "Hoan tien that bai",
                    e.getMessage()
            );
        }
    }

    @PostMapping("/momo/create")
    public ApiResponse<?> createMomo(@RequestBody PaymentRequest paymentRequest) {
        try {
            return new ApiResponse<>(
                    HttpStatus.CREATED.value(),
                    "Tao thanh toan thanh cong",
                    invoiceService.createMoMoQR(paymentRequest)
            );
        }
        catch (Exception e) {
            return new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(),
                    "Tao thanh toan that bai",
                    e.getMessage()
            );
        }

    }

    @GetMapping("/momo/payment_info")
    public ApiResponse<?> getPaymentInfoMomo(
            @RequestParam("transId") String transNo,
            @RequestParam("responseTime") String transDate,
            @RequestParam("requestId") String requestId,
            @RequestParam("resultCode") Integer resultCode
    ) throws IOException {
        if (resultCode == 0) {
            Map<String, Object> data = new HashMap<>();
            data.put("transactionNo", transNo);
            data.put("transactionDate", transDate);
            data.put("requestId", requestId);
            data.put("responseCode", resultCode);
            return new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Thanh toan thanh cong",
                    data
            );
        }
        return new ApiResponse<>(
                HttpStatus.NOT_FOUND.value(),
                "Thanh toan that bai",
                null
        );
    }
}
