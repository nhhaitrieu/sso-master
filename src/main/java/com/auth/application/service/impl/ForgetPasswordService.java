package com.auth.application.service.impl;

import com.auth.application.config.JwtTokenUtil;
import com.auth.application.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
public class ForgetPasswordService {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private MailService mailService;


    // Gửi mail có reset token
    public void processForgetPassword(String email) {
        log.info("Email request: [{}]", email);
        User user = userServiceImpl.findUserByEmail(email.trim());
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }
        String resetToken = jwtTokenUtil.generateResetPasswordToken(user.getEmail());
        String otp = jwtTokenUtil.getOtpFromToken(resetToken); // lấy OTP từ token luôn

        mailService.sendMail(email, "Reset Password OTP", "Your OTP is: " + otp + "\nToken: " + resetToken);
    }

    // Reset mật khẩu
    public void resetPassword(String token, String newPassword) {
        String email = jwtTokenUtil.getUsernameFromToken(token);
        String type = jwtTokenUtil.getTypeFromToken(token);

        if (!"RESET_PASSWORD".equals(type)) {
            throw new RuntimeException("Invalid token type");
        }
        if (jwtTokenUtil.isResetTokenExpired(token)) {
            throw new RuntimeException("Token expired");
        }
        User user = userServiceImpl.findUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        userServiceImpl.updatePassword(user, newPassword);
    }
}
