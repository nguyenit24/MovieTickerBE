package com.example.MovieTicker.controller;


import com.example.MovieTicker.request.PaymentRequest;
import com.example.MovieTicker.response.ApiResponse;
import com.example.MovieTicker.response.CreateMomoResponse;
import com.example.MovieTicker.service.HoaDonService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
                    "Tạo thanh toán thành công",
                    paymentUrl
            );
        } catch (Exception e) {
            return new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(),
                    "Tạo thanh toán thất bại",
                    e.getMessage()
            );
        }

    }

    @GetMapping("/vn_pay/payment_info")
    public ApiResponse<?> getPaymentInfoVnPay(
            @RequestParam("vnp_TransactionNo") String transNo,
            @RequestParam("vnp_PayDate") String transDate,
            @RequestParam("vnp_ResponseCode") String responseCode
    ) {
        if (responseCode.equals("00")) {
            Map<String, Object> data = new HashMap<>();
            data.put("transactionNo", transNo);
            data.put("transactionDate", transDate);
            data.put("responseCode", responseCode);
            return new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Thanh toán thành công",
                    data
            );
        }
        return new ApiResponse<>(
                HttpStatus.NOT_FOUND.value(),
                "Thanh toán thất bại",
                "Đã xảy ra lỗi"
        );
    }

        /*
        Thông số mẫu
      {
            "amount": 200000,
            "transId": "15197974",
            "transDate": "20251010142421",
            "orderId": "123567",
            "transType": "02"
        }
        "02": Hoàn toàn bộ
        "03": Hoàn một phần
     */
    @PostMapping("/vn_pay/refund")
    public ApiResponse<?> getPaymentRefundVnPay(
            @RequestBody PaymentRequest paymentRequest,
            HttpServletRequest request
    ) throws IOException {
        String paymentUrl = invoiceService.refundVnPay(paymentRequest, request);
        JsonObject response = JsonParser.parseString(paymentUrl).getAsJsonObject();
        String responseCode = response.get("vnp_ResponseCode").getAsString();
        String message = response.get("vnp_Message").getAsString();
        return switch (responseCode) {
            case "00" -> new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Hoàn tiền thành công",
                    message
            );
            case "94" -> new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Hoàn tiền thất bại",
                    "Hóa đơn đã được hoàn trước đó"
            );
            case "93" -> new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Hoàn tiền thất bại",
                    "Số tiền hoàn vượt quá số tiền giao dịch"
            );
            default -> new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(),
                    "Hoàn tiền thất bại",
                    message
            );
        };
    }

    @PostMapping("/momo/create")
    public ApiResponse<?> createMomo(@RequestBody PaymentRequest paymentRequest) {
        try {
            return new ApiResponse<>(
                    HttpStatus.CREATED.value(),
                    "Tạo thanh toán thành công",
                    invoiceService.createMoMoQR(paymentRequest)
            );
        }
        catch (Exception e) {
            return new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(),
                    "Tạo thanh toán thất bại",
                    e.getMessage()
            );
        }

    }

    @GetMapping("/momo/payment_info")
    public ApiResponse<?> getPaymentInfoMomo(
            @RequestParam("transId") String transNo,
            @RequestParam("responseTime") String transDate,
            @RequestParam("requestId") String requestId,
            @RequestParam("resultCode") Integer resultCode,
            @RequestParam("message") String message
    ) throws IOException {
        if (resultCode == 0) {
            Map<String, Object> data = new HashMap<>();
            data.put("transactionNo", transNo);
            data.put("transactionDate", transDate);
            data.put("requestId", requestId);
            data.put("responseCode", resultCode);
            return new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Thanh toán thành công",
                    data
            );
        }
        return new ApiResponse<>(
                HttpStatus.NOT_FOUND.value(),
                "Thanh toán thất bại",
                message
        );
    }

    /*
        Thông số mẫu
        {
          "amount": 200000,
          "requestId": "268cae90-1bd2-466c-98ed-2159f82c8fde",
          "transId": "4590889874",
          "transDate": "20251010142421"
        }
     */

    @PostMapping("/momo/refund")
    public ApiResponse<?> getPaymentRefundMomo(
            @RequestBody PaymentRequest paymentRequest
    ) {
        CreateMomoResponse response = invoiceService.refundMomo(paymentRequest);
        if (response.getResultCode() == 0) {
            return new  ApiResponse<> (
                    HttpStatus.OK.value(),
                    "Hoàn tiền thành công",
                    response.getMessage()
                    );
        }
        return new ApiResponse<>(
                HttpStatus.NO_CONTENT.value(),
                "Hoàn tiền thất bại",
                response.getMessage()
        );
    }
}
