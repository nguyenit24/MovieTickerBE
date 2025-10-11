package com.example.MovieTicker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    public void sendPassWordResetEmail(String to, String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Yêu cầu đặt lại mật khẩu MovieTicker");
        message.setText("Chào bạn,\n\n" +
                "Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn. " +
                "Vui lòng sử dụng mã OTP dưới đây để đặt lại mật khẩu:\n\n" +
                "Mã OTP: " + otp + "\n\n" +
                "Mã OTP này sẽ hết hạn trong 3 phút. Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\n" +
                "Trân trọng,\n" +
                "Đội ngũ MovieTicker");
        mailSender.send(message);
    }
}
