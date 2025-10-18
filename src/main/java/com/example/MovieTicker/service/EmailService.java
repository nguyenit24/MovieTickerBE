package com.example.MovieTicker.service;

import com.example.MovieTicker.entity.PasswordResetToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    public void sendPasswordResetEmail(String to, PasswordResetToken token){
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
}
