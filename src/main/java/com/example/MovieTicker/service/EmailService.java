package com.example.MovieTicker.service;

import com.example.MovieTicker.entity.PasswordResetToken;
import com.example.MovieTicker.response.HoaDonResponse;
import com.example.MovieTicker.response.VeResponse;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    public void sendPasswordResetEmail(String to, PasswordResetToken token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Yêu Cầu Đặt Lại Mật Khẩu");
        String resetUrl = frontendBaseUrl + "/reset-password?token=" + token.getToken();
        String emailBody = "Chào bạn,\n\n"
                + "Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.\n"
                + "Vui lòng nhấp vào đường link dưới đây để đặt lại mật khẩu:\n"
                + resetUrl + "\n\n"
                + "Nếu bạn không yêu cầu việc này, vui lòng bỏ qua email này.\n\n"
                + "Trân trọng,\n"
                + "Đội ngũ Cinema.";
        message.setText(emailBody);
        mailSender.send(message);
    }

    public void sendSuccessInvoiceEmail(String to, HoaDonResponse invoiceDetails) {
        System.out.println("=== START SENDING EMAIL ===");
        System.out.println("To: " + to);
        System.out.println("Invoice: " + (invoiceDetails != null ? invoiceDetails.getMaHD() : "null"));
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setFrom("noreply@cinema.com");
            helper.setSubject("🎬 Hóa Đơn Mua Vé - Cinema");
            
            System.out.println("Building email HTML...");
            String htmlContent = buildEmailHTML(invoiceDetails);
            helper.setText(htmlContent, true);
            System.out.println("✓ HTML content built successfully");

            // Attach QR codes
            if (invoiceDetails.getDanhSachVe() != null && !invoiceDetails.getDanhSachVe().isEmpty()) {
                System.out.println("Attaching QR codes for " + invoiceDetails.getDanhSachVe().size() + " tickets");
                int index = 0;
                for (VeResponse ve : invoiceDetails.getDanhSachVe()) {
                    System.out.println("  - Ticket " + index + ": " + ve.getMaVe());
                    if (ve.getQrCodeUrl() != null && !ve.getQrCodeUrl().isEmpty()) {
                        System.out.println("    QR URL: " + ve.getQrCodeUrl());
                        attachQRCode(helper, ve.getQrCodeUrl(), "qr" + index);
                    } else {
                        System.out.println("    ⚠ No QR code URL");
                    }
                    index++;
                }
            } else {
                System.out.println("⚠ No tickets found in invoice");
            }

            System.out.println("Sending email...");
            mailSender.send(message);
            System.out.println("✓ Email sent successfully to: " + to);
            System.out.println("=== EMAIL SENT SUCCESSFULLY ===\n");
        } catch (MessagingException e) {
            System.err.println("✗ MessagingException: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Không thể gửi email cho: " + to, e);
        } catch (Exception e) {
            System.err.println("✗ Failed to send email: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Không thể gửi email cho: " + to, e);
        }
    }

    // ============= BUILD HTML EMAIL =============
    private String buildEmailHTML(HoaDonResponse invoice) {
        System.out.println("  Building HTML for invoice: " + invoice.getMaHD());
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>")
            .append("<html><head><meta charset='UTF-8'></head>")
            .append("<body style='margin:0;padding:0;background:#f5f5f5;font-family:Arial,sans-serif'>")
            .append("<div style='max-width:600px;margin:30px auto;background:#fff;border-radius:10px;overflow:hidden;box-shadow:0 2px 10px rgba(0,0,0,0.1)'>");

        // Header
        html.append("<div style='background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);padding:30px;text-align:center'>")
            .append("<h1 style='color:#fff;margin:0;font-size:28px'>🎬 Cinema</h1>")
            .append("<p style='color:#fff;margin:10px 0 0;opacity:0.9'>Cảm ơn bạn đã đặt vé</p>")
            .append("</div>");

        // Invoice Info
        html.append("<div style='padding:30px'>")
            .append("<h2 style='color:#333;margin:0 0 20px;font-size:20px'>Thông tin hóa đơn</h2>")
            .append("<table style='width:100%;border-collapse:collapse'>")
            .append(buildInfoRow("Mã hóa đơn", invoice.getMaHD()))
            .append(buildInfoRow("Khách hàng", invoice.getTenNguoiDung() != null ? invoice.getTenNguoiDung() : invoice.getTenKhachHang()))
            .append(buildInfoRow("Email", invoice.getEmailNguoiDung() != null ? invoice.getEmailNguoiDung() : invoice.getEmailKhachHang()))
            .append(buildInfoRow("Số điện thoại", invoice.getSoDienThoai() != null ? invoice.getSoDienThoai() : invoice.getSdtKhachHang()))
            .append(buildInfoRow("Ngày lập", invoice.getNgayLap()))
            .append(buildInfoRow("Thanh toán", invoice.getPhuongThucThanhToan()))
            .append("</table>");

        // Total
        System.out.println("  Total amount type: " + (invoice.getTongTien() != null ? invoice.getTongTien().getClass().getName() : "null"));
        System.out.println("  Total amount value: " + invoice.getTongTien());
        
        String formattedTotal;
        try {
            Object tongTien = invoice.getTongTien();
            if (tongTien instanceof Double || tongTien instanceof Float) {
                formattedTotal = String.format("%,.0f", ((Number) tongTien).doubleValue());
            } else if (tongTien instanceof Number) {
                formattedTotal = String.format("%,d", ((Number) tongTien).longValue());
            } else if (tongTien != null) {
                // If it's String or BigDecimal, parse it
                formattedTotal = String.format("%,.0f", Double.parseDouble(tongTien.toString()));
            } else {
                formattedTotal = "0";
            }
        } catch (Exception e) {
            System.err.println("  ✗ Error formatting total: " + e.getMessage());
            formattedTotal = invoice.getTongTien() != null ? invoice.getTongTien().toString() : "0";
        }
        
        html.append("<div style='margin:20px 0;padding:15px;background:#f8f9fa;border-radius:8px;text-align:right'>")
            .append("<span style='font-size:16px;color:#666'>Tổng tiền:</span> ")
            .append("<span style='font-size:24px;color:#667eea;font-weight:bold'>")
            .append(formattedTotal)
            .append(" VNĐ</span></div>");

        // Tickets
        if (invoice.getDanhSachVe() != null && !invoice.getDanhSachVe().isEmpty()) {
            html.append("<h2 style='color:#333;margin:30px 0 20px;font-size:20px'>Thông tin vé</h2>");
            
            int qrIndex = 0;
            for (VeResponse ve : invoice.getDanhSachVe()) {
                html.append(buildTicketCard(ve, qrIndex++));
            }
            System.out.println("  Added " + invoice.getDanhSachVe().size() + " ticket cards to HTML");
        }

        // Footer
        html.append("<div style='margin-top:30px;padding-top:20px;border-top:1px solid #eee'>")
            .append("<p style='color:#666;font-size:14px;line-height:1.6;margin:0'>")
            .append("Vui lòng mang QR code này đến quầy để đổi vé.<br>")
            .append("Đến trước giờ chiếu 15 phút để có chỗ ngồi tốt nhất.</p>")
            .append("</div>");

        html.append("</div>") // End padding
            .append("<div style='text-align:center;padding:20px;color:#999;font-size:12px'>")
            .append("© 2024 Cinema. All rights reserved.")
            .append("</div>");

        html.append("</div></body></html>");
        
        System.out.println("  HTML built, length: " + html.length() + " characters");
        return html.toString();
    }

    // ============= BUILD TICKET CARD =============
    private String buildTicketCard(VeResponse ve, int qrIndex) {
        StringBuilder card = new StringBuilder();
        LocalDateTime thoiGianChieu = ve.getThoiGianChieu();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedTime = thoiGianChieu.format(formatter);

        card.append("<div style='border:2px dashed #667eea;border-radius:10px;padding:20px;margin:15px 0;background:#fafbff'>")
            // Sử dụng flexbox: căn đều 2 bên, canh giữa theo trục dọc
            .append("<div style='display:flex;justify-content:space-between;align-items:center;'>")
            
            // Bên trái: thông tin vé
            .append("<div style='flex:1; padding-right:20px;'>")
            .append("<h3 style='color:#667eea;margin:0 0 15px;font-size:18px'>").append(escapeHtml(ve.getTenPhim())).append("</h3>")
            .append("<p style='margin:5px 0;color:#666'><b>Mã vé:</b> ").append(escapeHtml(ve.getMaVe())).append("</p>")
            .append("<p style='margin:5px 0;color:#666'><b>Ghế:</b> <span style='display:inline-block;padding:4px 12px;background:#667eea;color:#fff;border-radius:4px;font-weight:bold'>")
            .append(escapeHtml(ve.getTenGhe())).append("</span></p>")
            .append("<p style='margin:5px 0;color:#666'><b>Suất chiếu:</b> ").append(escapeHtml(formattedTime)).append("</p>")
            .append("</div>");

        // Bên phải: QR Code
        if (ve.getQrCodeUrl() != null && !ve.getQrCodeUrl().isEmpty()) {
            card.append("<div style='flex-shrink:0; text-align:center;'>")
                .append("<img src='cid:qr").append(qrIndex).append("' ")
                .append("alt='QR Code' style='width:120px;height:120px;border:3px solid #667eea;border-radius:8px;background:#fff;padding:8px;display:block;margin:auto;'/>")
                .append("</div>");
        }

        card.append("</div></div>");

        return card.toString();
    }
    // ============= ESCAPE HTML =============
    private String escapeHtml(Object value) {
        if (value == null) return "";
        return value.toString()
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;");
    }

    // ============= BUILD INFO ROW =============
    private String buildInfoRow(String label, Object value) {
        String displayValue = (value != null) ? escapeHtml(value) : "";
        return "<tr><td style='padding:8px 0;color:#666;width:40%'>" + escapeHtml(label) + ":</td>" +
            "<td style='padding:8px 0;color:#333;font-weight:500'>" + displayValue + "</td></tr>";
    }

    // ============= ATTACH QR CODE =============
    private void attachQRCode(MimeMessageHelper helper, String qrCodeUrl, String contentId) {
        System.out.println("    Attaching QR [" + contentId + "]...");
        System.out.println("    URL type: " + getUrlType(qrCodeUrl));
        
        try {
            // Base64 Data URI
            if (qrCodeUrl.startsWith("data:image")) {
                System.out.println("    Processing as Base64 data URI");
                int commaIndex = qrCodeUrl.indexOf(",");
                if (commaIndex == -1) {
                    System.err.println("    ✗ Invalid data URI format (no comma found)");
                    return;
                }
                
                String base64 = qrCodeUrl.substring(commaIndex + 1);
                System.out.println("    Base64 length: " + base64.length());
                
                byte[] bytes = Base64.getDecoder().decode(base64);
                System.out.println("    Decoded bytes: " + bytes.length);
                
                helper.addInline(contentId, new ByteArrayResource(bytes), "image/png");
                System.out.println("    ✓ QR attached as inline resource (Base64)");
                return;
            }
            
            // HTTP/HTTPS URL
            if (qrCodeUrl.startsWith("http://") || qrCodeUrl.startsWith("https://")) {
                System.out.println("    Processing as HTTP URL");
                URI uri = new URI(qrCodeUrl);
                HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setRequestMethod("GET");
                
                System.out.println("    Connecting to: " + uri);
                int responseCode = conn.getResponseCode();
                System.out.println("    Response code: " + responseCode);
                
                if (responseCode == 200) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    InputStream in = conn.getInputStream();
                    byte[] buffer = new byte[4096];
                    int n;
                    int totalBytes = 0;
                    while ((n = in.read(buffer)) != -1) {
                        out.write(buffer, 0, n);
                        totalBytes += n;
                    }
                    in.close();
                    System.out.println("    Downloaded: " + totalBytes + " bytes");
                    
                    helper.addInline(contentId, new ByteArrayResource(out.toByteArray()), "image/png");
                    System.out.println("    ✓ QR attached as inline resource (HTTP)");
                    return;
                } else {
                    System.err.println("    ✗ HTTP request failed with code: " + responseCode);
                    return;
                }
            }
            
            // Local File Path
            System.out.println("    Processing as local file path");
            File file = new File(qrCodeUrl);
            System.out.println("    Trying path: " + file.getAbsolutePath());
            
            if (!file.exists()) {
                System.out.println("    File not found, trying with user.dir");
                file = new File(System.getProperty("user.dir"), qrCodeUrl);
                System.out.println("    Trying path: " + file.getAbsolutePath());
            }
            
            if (file.exists()) {
                if (file.canRead()) {
                    System.out.println("    File found and readable, size: " + file.length() + " bytes");
                    helper.addInline(contentId, file);
                    System.out.println("    ✓ QR attached as file resource");
                } else {
                    System.err.println("    ✗ File exists but cannot be read: " + file.getAbsolutePath());
                }
            } else {
                System.err.println("    ✗ QR file not found: " + file.getAbsolutePath());
            }
            
        } catch (URISyntaxException e) {
            System.err.println("    ✗ Invalid URI: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("    ✗ IO Error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("    ✗ Failed to attach QR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ============= GET URL TYPE =============
    private String getUrlType(String url) {
        if (url == null) return "null";
        if (url.startsWith("data:image")) return "Base64 Data URI";
        if (url.startsWith("http://") || url.startsWith("https://")) return "HTTP URL";
        return "Local File Path";
    }
}