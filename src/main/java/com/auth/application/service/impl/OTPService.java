package com.auth.application.service.impl;


import com.auth.application.config.JwtTokenUtil;
import com.auth.application.model.OTPTokenRequest;
import com.auth.application.model.OTPTokenResponse;
import com.auth.application.model.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class OTPService {
    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Map<String, String> otpStorage = new HashMap<>();


    public ResponseEntity<OTPTokenResponse> processTokenRequest(OTPTokenRequest otpTokenRequest, HttpServletResponse response) {
        String decodeUsername = otpTokenRequest.getUserID();
        String decodePassword = otpTokenRequest.getPassword().getValue();
        log.info("Incoming OTP token request for userID: {}", decodeUsername);
        try {
            User findUserName = userServiceImpl.findUserByUsername(decodeUsername);

            if (findUserName != null && passwordEncoder.matches(decodePassword, findUserName.getPassword())) {
                log.info("User {} authenticated successfully", decodeUsername);
                //Copy UserName tử database sang UserDetails
                final UserDetails userDetails = new org.springframework.security.core.userdetails.User(decodeUsername, decodePassword, new ArrayList<>());

                final String jwt = jwtTokenUtil.generateToken(userDetails);

                log.info("JWT added to cookie: {}", jwt);
                jwtTokenUtil.addJwtToCookie(response, jwt);

                // **🔹 5. Trả về Token trong Response**
                Map<String, String> responseBody = new HashMap<>();
                responseBody.put("message", "Authenticated successfully");
                responseBody.put("token", jwt);
                OTPTokenResponse otpTokenResponse = new OTPTokenResponse();
                otpTokenResponse.setUserID(decodeUsername);
                otpTokenResponse.setAuthenticationDate(new Date().toString());

                log.info("Returning success response with JWT for user: {}", decodeUsername);
                return ResponseEntity.status(HttpStatus.OK).body(otpTokenResponse);

            } else {
                log.warn("Authentication failed for user: {}", decodeUsername);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception ex) {
            log.error("Exception while processing OTP token request for user {}: {}", decodeUsername, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    // Tạo OTP và lưu vào cache/memory
    public String generateOTP(String email) {
        String otp = String.valueOf((int)((Math.random() * 900000) + 100000)); // Random 6 digits
        otpStorage.put(email, otp);
        return otp;
    }

    // Validate OTP
    public boolean validateOTP(String email, String otp) {
        String storedOtp = otpStorage.get(email);
        return storedOtp != null && storedOtp.equals(otp);
    }

    // Xóa OTP sau khi dùng
    public void clearOTP(String email) {
        otpStorage.remove(email);
    }

    // Lấy OTP từ storage để kiểm tra
    public String getOTP(String email) {
        return otpStorage.get(email);
    }
}
