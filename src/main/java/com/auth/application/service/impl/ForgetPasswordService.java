package com.auth.application.service.impl;

import com.auth.application.config.JwtTokenUtil;
import com.auth.application.model.ForgetPasswordResponse;
import com.auth.application.model.ResetPasswordRequest;
import com.auth.application.model.ResetPasswordResponse;
import com.auth.application.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    private OTPService otpService;

    // Xử lý logic gửi token reset password
    public ResponseEntity<ForgetPasswordResponse> handleForgetPassword(String email) {
        try {
            processForgetPassword(email); // tạo token và gửi email
            return ResponseEntity.ok(new ForgetPasswordResponse("SUCCESS", "Reset token sent to your email"));
        } catch (Exception e) {
            log.error("Error in forgetPassword: ", e);
            return ResponseEntity.badRequest().body(new ForgetPasswordResponse("FAILURE", e.getMessage()));
        }
    }

    // Xử lý logic reset mật khẩu khi người dùng gửi OTP và token
    public ResponseEntity<ResetPasswordResponse> handleResetPassword(ResetPasswordRequest request) {
        try {
            // Kiểm tra token hợp lệ trước khi trích xuất dữ liệu
            if (jwtTokenUtil.isResetTokenExpired(request.getToken())) {
                throw new RuntimeException("Reset token has expired");
            }

            String email = jwtTokenUtil.getUsernameFromToken(request.getToken());
            String otpFromToken = jwtTokenUtil.getOtpFromToken(request.getToken());

            if (email == null || otpFromToken == null) {
                throw new RuntimeException("Invalid reset token");
            }

            // Kiểm tra OTP nhập vào có hợp lệ không
            if (!otpFromToken.equals(request.getOtp())) {
                log.warn("OTP mismatch: expected = {}, received = {}", otpFromToken, request.getOtp());
                throw new RuntimeException("Invalid OTP");
            }

            // Reset password nếu hợp lệ
            resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(new ResetPasswordResponse("SUCCESS", "Password has been reset successfully"));

        } catch (Exception e) {
            log.error("Error in resetPassword: ", e);
            return ResponseEntity.badRequest().body(new ResetPasswordResponse("FAILURE", e.getMessage()));
        }
    }

    // Hàm xử lý quên mật khẩu: tìm user và sinh reset token
    public void processForgetPassword(String email) {
        User user = userServiceImpl.findUserByEmail(email);
        if (user == null) {
            log.warn("Forget password request for non-existing email: {}", email);
            return; // Tránh trả về lỗi để không tiết lộ email tồn tại hay không
        }
        // ✅ Tạo OTP mới
        String otp = otpService.generateOTP(email);
        log.info("Generated OTP for email {}: {}", email, otp);

        // ✅ Tạo reset token
        String resetToken = jwtTokenUtil.generateResetPasswordToken(email, otp);
        log.info("Reset token generated for email: {}", email);

        // ✅ Gửi OTP qua email
        String subject = "Reset your password";
        String body = "Here is your OTP: " + otp + "\nYour password reset token: " + resetToken;
        mailService.sendMail(email, subject, body);

        log.info("Reset token and OTP sent to email: {}", email);
        // TODO: Gửi mail chứa resetToken ở đây nếu cần
    }

    // Hàm chính reset mật khẩu
    public void resetPassword(String token, String newPassword) {
        String email = jwtTokenUtil.getUsernameFromToken(token);

        if (jwtTokenUtil.isResetTokenExpired(token)) {
            throw new RuntimeException("Reset token expired");
        }

        // Lấy OTP từ hệ thống (OTPService)
        String storedOtp = otpService.getOTP(email);
        if (storedOtp == null || !storedOtp.equals(jwtTokenUtil.getOtpFromToken(token))) {
            throw new RuntimeException("Invalid OTP");
        }

        User user = userServiceImpl.findUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // ✅ Cập nhật mật khẩu mới
        userServiceImpl.updatePassword(user, newPassword);

        // ✅ Xóa OTP sau khi dùng
        otpService.clearOTP(email);
    }


}
