package com.auth.application.controller;

import com.auth.application.config.JwtTokenUtil;
import com.auth.application.model.*;
import com.auth.application.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/otp")
public class OtpController {

    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @PostMapping(value = "/token", produces="application/xml")
    public ResponseEntity<OTPTokenResponse> getToken(@RequestBody OTPTokenRequest otpTokenRequest,HttpServletResponse response) {
        String decodeUsername = otpTokenRequest.getUserID();
        String decodePassword = otpTokenRequest.getPassword().getValue();
        log.info("Incoming OTP token request for userID: {}", decodeUsername);
        try {
            User findUserName = userServiceImpl.findUserByUsername(decodeUsername);

            if (findUserName != null && decodePassword.equals(findUserName.getPassword())) {
                log.info("User {} authenticated successfully", decodeUsername);
                //Copy UserName tử database sang UserDetails
                final UserDetails userDetails = new org.springframework.security.core.userdetails.User(decodeUsername, decodePassword, new ArrayList<>());

                final String jwt = jwtTokenUtil.generateToken(userDetails);

                ResponseCookie cookie = ResponseCookie.from("JWT_TOKEN", jwt)
                        .httpOnly(true)   // Chỉ HTTP, không cho JavaScript truy cập
                        .secure(true)    // Đổi thành `true` nếu dùng HTTPS
                        .path("/")        // Cookie có hiệu lực trên toàn bộ ứng dụng
                        .maxAge(Duration.ofHours(1)) // Hết hạn sau 1 giờ
                        .sameSite("None")  // Nếu gọi API từ frontend khác domain
                        .secure(true)
                        .build();

                response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

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

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/create", consumes = "application/xml", produces = "application/xml")
    public ResponseEntity<User> createUser(@RequestBody User user, HttpServletResponse response) {
        log.info("Creating user with userID: {}", user.getUsername());
        if (userServiceImpl.findUserByUsername(user.getUsername()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // **🔹 2. Mã hóa mật khẩu & Lưu User vào database**
        User createdUser = userServiceImpl.saveUser(user);

        // **🔹 3. Trả về Response**
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
