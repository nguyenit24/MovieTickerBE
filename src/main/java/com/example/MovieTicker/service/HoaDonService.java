package com.example.MovieTicker.service;

import com.example.MovieTicker.config.MomoAPI;
import com.example.MovieTicker.config.PaymentConfig;
import com.example.MovieTicker.entity.HoaDon;
import com.example.MovieTicker.entity.Ve;
import com.example.MovieTicker.repository.HoaDonRepository;
import com.example.MovieTicker.request.CreateMomoRefundRequest;
import com.example.MovieTicker.request.CreateMomoRequest;
import com.example.MovieTicker.request.PaymentRequest;
import com.example.MovieTicker.response.CreateMomoResponse;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private HoaDonRepository hoaDonRepository;

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

    public String refundVnPay(PaymentRequest paymentRequest, HttpServletRequest request) throws IOException {
        String vnp_RequestId = PaymentConfig.getRandomNumber(8);
        String vnp_Version = PaymentConfig.vnp_Version;
        String vnp_Command = "refund";
        String vnp_TmnCode = PaymentConfig.vnp_TmnCode;
        String vnp_TransactionType = "02";
        String vnp_TxnRef = paymentRequest.getOrderId();
        long amount = paymentRequest.getAmount() * 100;
        String vnp_Amount = String.valueOf(amount);
        String vnp_OrderInfo = "Hoan tien GD OrderId:" + vnp_TxnRef;
        String vnp_TransactionNo = ""; //Assuming value of the parameter "vnp_TransactionNo" does not exist on your system.
        String vnp_TransactionDate = paymentRequest.getTransDate();
        String vnp_CreateBy = "System";

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        String vnp_IpAddr = PaymentConfig.getIpAddress(request);

        JsonObject vnp_Params = new JsonObject();

        vnp_Params.addProperty("vnp_RequestId", vnp_RequestId);
        vnp_Params.addProperty("vnp_Version", vnp_Version);
        vnp_Params.addProperty("vnp_Command", vnp_Command);
        vnp_Params.addProperty("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.addProperty("vnp_TransactionType", vnp_TransactionType);
        vnp_Params.addProperty("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.addProperty("vnp_Amount", vnp_Amount);
        vnp_Params.addProperty("vnp_OrderInfo", vnp_OrderInfo);

        if (vnp_TransactionNo != null && !vnp_TransactionNo.isEmpty()) {
            vnp_Params.addProperty("vnp_TransactionNo", "{get value of vnp_TransactionNo}");
        }

        vnp_Params.addProperty("vnp_TransactionDate", vnp_TransactionDate);
        vnp_Params.addProperty("vnp_CreateBy", vnp_CreateBy);
        vnp_Params.addProperty("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.addProperty("vnp_IpAddr", vnp_IpAddr);

        String hash_Data = String.join("|", vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode,
                vnp_TransactionType, vnp_TxnRef, vnp_Amount, vnp_TransactionNo, vnp_TransactionDate,
                vnp_CreateBy, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);

        String vnp_SecureHash = PaymentConfig.hmacSHA512(PaymentConfig.secretKey, hash_Data.toString());

        vnp_Params.addProperty("vnp_SecureHash", vnp_SecureHash);

        URL url = new URL(PaymentConfig.vnp_ApiUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(vnp_Params.toString());
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        System.out.println("nSending 'POST' request to URL : " + url);
        System.out.println("Post Data : " + vnp_Params);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();
        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();
        System.out.println(response.toString());
        return response.toString();
    }

    public HoaDon getHoaDonByVeList(List<Ve> ticketList) {
        if (ticketList == null || ticketList.isEmpty()) {
            throw new RuntimeException("Danh sách vé trống");
        }
        String maHD = ticketList.get(0).getHoaDon().getMaHD();
        Optional<HoaDon> hoaDonOpt = hoaDonRepository.findById(maHD);
        if (hoaDonOpt.isPresent()) {
            return hoaDonOpt.get();
        } else {
            throw new RuntimeException("Không tìm thấy hóa đơn với mã: " + maHD);
        }
    }
}
