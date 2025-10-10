package com.example.MovieTicker.service;

import com.example.MovieTicker.config.MomoAPI;
import com.example.MovieTicker.config.PaymentConfig;
import com.example.MovieTicker.request.CreateMomoRefundRequest;
import com.example.MovieTicker.request.CreateMomoRequest;
import com.example.MovieTicker.request.PaymentRequest;
import com.example.MovieTicker.response.CreateMomoResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class HoaDonService {
    @Value("${momo.partnerCode}")
    private String partnerCode;
    @Value("${momo.accessKey}")
    private String accessKey;
    @Value("${momo.secretKey}")
    private String secretKey;
    @Value("${momo.returnUrl}")
    private String returnUrl;
    @Value("${momo.ipn-url}")
    private String notifyUrl;
    @Value("${momo.requestType}")
    private String requestType;

    @Autowired
    private MomoAPI momoAPI;

    public CreateMomoResponse createMoMoQR(PaymentRequest paymentRequest) {
        String orderId = paymentRequest.getOrderId();
        String orderInfo = "Thanh toan don hang: " + orderId;
        String requestId = UUID.randomUUID().toString();
        String extraData = "Khong co khuyen mai";
        long amount = paymentRequest.getAmount();

        String rawSignature = "accessKey=" + accessKey +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + notifyUrl +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + partnerCode +
                "&redirectUrl=" + returnUrl +
                "&requestId=" + requestId +
                "&requestType=" + requestType;
        String signature = PaymentConfig.hmacSHA256(secretKey, rawSignature);

        CreateMomoRequest request = CreateMomoRequest.builder()
                .partnerCode(partnerCode)
                .requestType(requestType)
                .ipnUrl(notifyUrl)
                .redirectUrl(returnUrl)
                .orderId(orderId)
                .orderInfo(orderInfo)
                .requestId(requestId)
                .extraData(extraData)
                .signature(signature)
                .amount(amount)
                .lang("vi")
                .build();

        return momoAPI.createMomoQR(request);
    }

    public CreateMomoResponse refundMomo(PaymentRequest paymentRequest, String transId) {
        String orderId = paymentRequest.getOrderId();
        long amount = paymentRequest.getAmount();
        String requestId = paymentRequest.getRequestId();
        String description = "Hoàn tiền hóa đơn" + orderId;

        String rawSignature = "partnerCode=" + partnerCode +
                "&requestId=" + requestId +
                "&orderId=" + orderId +
                "&amount=" + amount +
                "&transId=" + transId +
                "&lang=vi" +
                "&description=" + description;

        String signature = PaymentConfig.hmacSHA256(secretKey, rawSignature);

        CreateMomoRefundRequest request = CreateMomoRefundRequest.builder()
                .partnerCode(partnerCode)
                .amount(amount)
                .requestId(requestId)
                .orderId(orderId)
                .transId(transId)
                .lang("vi")
                .description(description).build();
        return momoAPI.createMomoRefund(request);
    }

    public String createVnPayRequest(PaymentRequest paymentRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String orderId = paymentRequest.getOrderId();
        long amount = paymentRequest.getAmount() * 100;

        String vnp_TxnRef = paymentRequest.getOrderId();
        String vnp_IpAddr = PaymentConfig.getIpAddress(request);

        String vnp_TmnCode = PaymentConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", PaymentConfig.vnp_Version);
        vnp_Params.put("vnp_Command", PaymentConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", "other");

        vnp_Params.put("vnp_ReturnUrl", PaymentConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = PaymentConfig.hmacSHA512(PaymentConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        return PaymentConfig.vnp_PayUrl + "?" + queryUrl;
    }

//    Map<String, String> params = new HashMap<>();
//params.put("vnp_RequestId", vnp_RequestId);
//params.put("vnp_Version", "2.1.0");
//params.put("vnp_Command", "refund");
//params.put("vnp_TmnCode", vnp_TmnCode);
//params.put("vnp_TransactionType", "02");
//params.put("vnp_TxnRef", vnp_TxnRef);
//params.put("vnp_Amount", String.valueOf(amount * 100));
//params.put("vnp_TransactionNo", vnp_TransactionNo);
//params.put("vnp_OrderInfo", "Refund ticket " + orderId);
//params.put("vnp_CreateBy", "system");
//params.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
//params.put("vnp_IpAddr", "127.0.0.1");
//
//    String data = createDataToHash(params);
//    String secureHash = hmacSHA256(vnp_HashSecret, data);
//params.put("vnp_SecureHash", secureHash);
//
//    String jsonBody = new Gson().toJson(params);


    public String refundVnPay(PaymentRequest paymentRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String orderId = paymentRequest.getOrderId();
        String transId = paymentRequest.getTransId();


        long amount = paymentRequest.getAmount() * 100;

        String vnp_TxnRef = paymentRequest.getOrderId();
        String vnp_IpAddr = PaymentConfig.getIpAddress(request);

        String vnp_TmnCode = PaymentConfig.vnp_TmnCode;

        //params.put("vnp_RequestId", vnp_RequestId);
//params.put("vnp_Version", "2.1.0");
//params.put("vnp_Command", "refund");
//params.put("vnp_TmnCode", vnp_TmnCode);
//params.put("vnp_TransactionType", "02");
//params.put("vnp_TxnRef", vnp_TxnRef);
//params.put("vnp_Amount", String.valueOf(amount * 100));
//params.put("vnp_TransactionNo", vnp_TransactionNo);
//params.put("vnp_OrderInfo", "Refund ticket " + orderId);
//params.put("vnp_CreateBy", "system");
//params.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
//params.put("vnp_IpAddr", "127.0.0.1");
//
//
//
//        Map<String, String> vnp_Params = new HashMap<>();
//        vnp_Params.put("vnp_Version", PaymentConfig.vnp_Version);
//        vnp_Params.put("vnp_Command", PaymentConfig.vnp_Command);
//        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
//        vnp_Params.put("vnp_TransactionType", "02");
//        vnp_Params.put("vnp_Amount", String.valueOf(amount));
//        vnp_Params.put("vnp_TransactionNo", "VND");
//        vnp_Params.put("vnp_Locale", "vn");
//        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
//        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
//        vnp_Params.put("vnp_OrderType", "other");
//
//        vnp_Params.put("vnp_ReturnUrl", PaymentConfig.vnp_ReturnUrl);
//        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
//
//        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//        String vnp_CreateDate = formatter.format(cld.getTime());
//        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
//
//        cld.add(Calendar.MINUTE, 15);
//        String vnp_ExpireDate = formatter.format(cld.getTime());
//        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
//
//        List fieldNames = new ArrayList(vnp_Params.keySet());
//        Collections.sort(fieldNames);
//        StringBuilder hashData = new StringBuilder();
//        StringBuilder query = new StringBuilder();
//        Iterator itr = fieldNames.iterator();
//        while (itr.hasNext()) {
//            String fieldName = (String) itr.next();
//            String fieldValue = (String) vnp_Params.get(fieldName);
//            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
//                //Build hash data
//                hashData.append(fieldName);
//                hashData.append('=');
//                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
//                //Build query
//                query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8));
//                query.append('=');
//                query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
//                if (itr.hasNext()) {
//                    query.append('&');
//                    hashData.append('&');
//                }
//            }
//        }
//        String queryUrl = query.toString();
//        String vnp_SecureHash = PaymentConfig.hmacSHA512(PaymentConfig.secretKey, hashData.toString());
//        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
//        return PaymentConfig.vnp_PayUrl + "?" + queryUrl;
//
//
        return null;
    }
}

