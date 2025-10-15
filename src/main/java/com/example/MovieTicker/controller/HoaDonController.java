package com.example.MovieTicker.controller;

import java.time.LocalDateTime;

import com.example.MovieTicker.entity.HoaDon;
import com.example.MovieTicker.entity.Ve;
import com.example.MovieTicker.enums.InvoiceStatus;
import com.example.MovieTicker.enums.TicketStatus;
import com.example.MovieTicker.repository.HoaDonRepository;
import com.example.MovieTicker.request.PaymentRequest;
import com.example.MovieTicker.response.ApiResponse;
import com.example.MovieTicker.response.CreateMomoResponse;
import com.example.MovieTicker.response.HoaDonResponse;
import com.example.MovieTicker.service.HoaDonService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/payment")
public class HoaDonController {
    @Autowired
    private HoaDonService invoiceService;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    @PostMapping("/vn_pay/create")
    public ApiResponse<?> createPaymentVnPay(@RequestBody PaymentRequest paymentRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Validate hóa đơn trước khi tạo payment
            invoiceService.validateInvoiceForPayment(paymentRequest.getOrderId());
            
            String paymentUrl = invoiceService.createVnPayRequest(paymentRequest, request, response);
            return new ApiResponse<>(
                    HttpStatus.CREATED.value(),
                    "Tạo thanh toán thành công",
                    paymentUrl
            );
        } catch (Exception e) {
            return new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "Tạo thanh toán thất bại: " + e.getMessage(),
                    null
            );
        }

    }

    @GetMapping("/vn_pay/payment_info")
    public void getPaymentInfoVnPay(
            @RequestParam("vnp_TransactionNo") String transNo,
            @RequestParam("vnp_PayDate") String transDate,
            @RequestParam("vnp_ResponseCode") String responseCode,
            @RequestParam("vnp_TxnRef") String orderId,
            HttpServletResponse response
    ) throws IOException {
        // Luôn cố gắng redirect về frontend, kể cả khi updatePaymentStatus gặp lỗi
        String redirectUrl;
        try {
            // Cập nhật trạng thái hóa đơn và vé
            invoiceService.updatePaymentStatus(orderId, transNo, transDate, responseCode);

            if ("00".equals(responseCode)) {
                // thành công
                redirectUrl = String.format(
                        "%s/payment/result?orderId=%s&status=SUCCESS&transactionNo=%s&transactionDate=%s",
                        frontendBaseUrl, orderId, transNo, transDate
                );
            } else {
                // thất bại
                redirectUrl = String.format(
                        "%s/payment/result?orderId=%s&status=FAILED&responseCode=%s",
                        frontendBaseUrl, orderId, responseCode
                );
            }
        } catch (Exception e) {
            String msg = e.getMessage() == null ? "error" : e.getMessage();
            String encoded = URLEncoder.encode(msg, StandardCharsets.UTF_8);
            HoaDon hoaDon = invoiceService.getHoaDonByMaHD(orderId);
            if (hoaDon != null && hoaDon.getResponseCode() != null && hoaDon.getResponseCode().equals("00")) {
                redirectUrl = String.format(
                        "%s/payment/result?orderId=%s&status=SUCCESS&transactionNo=%s&transactionDate=%s&message=%s",
                        frontendBaseUrl, orderId, hoaDon.getTransactionNo(), hoaDon.getTransactionDate(), encoded
                );
            } else {
                 hoaDon.setTrangThai(InvoiceStatus.CANCELLED.getCode());
                if (hoaDon.getVes() != null) {
                    for (Ve ve : hoaDon.getVes()) {
                        ve.setTrangThai(TicketStatus.CANCELLED.getCode());
                    }
                }
                hoaDonRepository.save(hoaDon);
                redirectUrl = String.format(
                        "%s/payment/result?orderId=%s&status=FAILED&message=%s",
                        frontendBaseUrl, orderId, encoded
                );
            }
        }
        try {
            if (!response.isCommitted()) {
                response.sendRedirect(redirectUrl);
                return;
            }
        } catch (Exception ex) {
            // nếu sendRedirect lỗi, sẽ gửi fallback HTML bên dưới
            // log nếu cần

            ex.printStackTrace();
        }

        // Fallback: nếu không thể redirect (response committed), ghi 1 trang HTML có meta refresh và link
        try {
            response.setContentType("text/html;charset=UTF-8");
            String html = "<html><head><meta http-equiv=\"refresh\" content=\"0;url=" + redirectUrl + "\" /></head>"
                    + "<body>Redirecting... If you are not redirected, <a href=\"" + redirectUrl + "\">click here</a>.</body></html>";
            response.getWriter().write(html);
            response.getWriter().flush();
        } catch (IOException ignored) {
            // nếu vẫn lỗi, không thể làm gì hơn
        }
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
            // Validate hóa đơn trước khi tạo payment
            invoiceService.validateInvoiceForPayment(paymentRequest.getOrderId());
            
            return new ApiResponse<>(
                    HttpStatus.CREATED.value(),
                    "Tạo thanh toán thành công",
                    invoiceService.createMoMoQR(paymentRequest)
            );
        }
        catch (Exception e) {
            return new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "Tạo thanh toán thất bại: " + e.getMessage(),
                    null
            );
        }

    }

    @GetMapping("/momo/payment_info")
    public ApiResponse<?> getPaymentInfoMomo(
            @RequestParam("transId") String transNo,
            @RequestParam("responseTime") String transDate,
            @RequestParam("requestId") String requestId,
            @RequestParam("resultCode") Integer resultCode,
            @RequestParam("message") String message,
            @RequestParam("orderId") String orderId
    ) throws IOException {
        // Cập nhật trạng thái hóa đơn và vé
        invoiceService.updatePaymentStatus(orderId, transNo, transDate, resultCode.toString());
        
        if (resultCode == 0) {
            Map<String, Object> data = new HashMap<>();
            data.put("transactionNo", transNo);
            data.put("transactionDate", transDate);
            data.put("requestId", requestId);
            data.put("responseCode", resultCode);
            data.put("orderId", orderId);
            data.put("status", "SUCCESS");
            return new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Thanh toán thành công",
                    data
            );
        }
        
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("resultCode", resultCode);
        errorData.put("orderId", orderId);
        errorData.put("status", "FAILED");
        errorData.put("message", message);
        return new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                "Thanh toán thất bại",
                errorData
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

    // Endpoint để FE check trạng thái thanh toán
    @GetMapping("/status/{orderId}")
    public ApiResponse<?> checkPaymentStatus(@PathVariable String orderId) {
        try {
            HoaDon hoaDon = invoiceService.getHoaDonByMaHD(orderId);
            if (hoaDon != null) {
                Map<String, Object> data = new HashMap<>();
                data.put("orderId", orderId);
                data.put("status", hoaDon.getTrangThai());
                data.put("transactionNo", hoaDon.getTransactionNo());
                data.put("transactionDate", hoaDon.getTransactionDate());
                data.put("responseCode", hoaDon.getResponseCode());
                
                String paymentStatus = "PENDING";
                if ("PAID".equals(hoaDon.getTrangThai()) && "00".equals(hoaDon.getResponseCode())) {
                    paymentStatus = "SUCCESS";
                } else if (hoaDon.getResponseCode() != null && !"00".equals(hoaDon.getResponseCode())) {
                    paymentStatus = "FAILED";
                } else if ("CANCELLED".equals(hoaDon.getTrangThai())) {
                    paymentStatus = "CANCELLED";
                } else if ("EXPIRED".equals(hoaDon.getTrangThai())) {
                    paymentStatus = "EXPIRED";
                }
                data.put("paymentStatus", paymentStatus);
                
                return new ApiResponse<>(
                        HttpStatus.OK.value(),
                        "Lấy trạng thái thành công",
                        data
                );
            }
            
            return new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(),
                    "Không tìm thấy hóa đơn",
                    null
            );
        } catch (Exception e) {
            return new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Lỗi khi lấy trạng thái thanh toán",
                    e.getMessage()
            );
        }
    }
    
    /**
     * API hủy hóa đơn manual khi user out ra không thanh toán
     */
    @PostMapping("/cancel/{maHD}")
    public ApiResponse<?> cancelInvoice(@PathVariable String maHD) {
        try {
            invoiceService.cancelInvoiceManual(maHD);
            return new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Hủy hóa đơn thành công",
                    null
            );
        } catch (Exception e) {
            return new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "Hủy hóa đơn thất bại: " + e.getMessage(),
                    null
            );
        }
    }
    
 

    @GetMapping("/all")
    public ApiResponse<?> getAllInvoices() {
        try {
            List<HoaDon> invoices = invoiceService.getAllHoaDon();
            return new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Lấy danh sách hóa đơn thành công",
                    invoices
            );
        } catch (Exception e) {
            return new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Lỗi khi lấy danh sách hóa đơn: " + e.getMessage(),
                    null
            );
        }
    }


    @GetMapping("/detail/{maHD}")
    public ApiResponse<?> getHoaDonDetail(@PathVariable String maHD) {
        try {
            HoaDonResponse hoaDonDetail = invoiceService.getHoaDonResponseByMaHD(maHD);
            return new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Lấy chi tiết hóa đơn thành công",
                    hoaDonDetail
            );
        } catch (Exception e) {
            return new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(),
                    "Lỗi khi lấy chi tiết hóa đơn: " + e.getMessage(),
                    null
            );
        }
    }

   
    @GetMapping("/my-invoices")
    public ApiResponse<?> getMyInvoices() {
        try {
            List<HoaDonResponse> hoaDonList = invoiceService.getMyInvoices();
            return new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Lấy danh sách hóa đơn của bạn thành công",
                    hoaDonList
            );
        } catch (Exception e) {
            return new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Lỗi khi lấy danh sách hóa đơn: " + e.getMessage(),
                    null
            );
        }
    }

    /**
     * API tìm kiếm và lọc hóa đơn với phân trang
     * GET /api/payment/search?tenKhachHang=Nguyen&nam=2025&thang=10&trangThai=PAID&page=1&size=10
     */
    @GetMapping("/search")
    public ApiResponse<?> searchHoaDon(
            @RequestParam(required = false) String tenKhachHang,
            @RequestParam(required = false) Integer nam,
            @RequestParam(required = false) Integer thang,
            @RequestParam(required = false) String trangThai,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            if (page < 1) page = 1;
            if (size < 1) size = 10;
            
            Map<String, Object> result = invoiceService.searchHoaDon(
                tenKhachHang, nam, thang, trangThai, page, size
            );
            
            return new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Tìm kiếm hóa đơn thành công",
                    result
            );
        } catch (Exception e) {
            return new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Lỗi khi tìm kiếm hóa đơn: " + e.getMessage(),
                    null
            );
        }
    }
}
