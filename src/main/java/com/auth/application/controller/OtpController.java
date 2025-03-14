package com.auth.application.controller;

import com.auth.application.config.JwtTokenUtil;
import com.auth.application.model.OTPTokenRequest;
import com.auth.application.model.User;
import com.auth.application.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/token")
    public ResponseEntity<String> getToken(@RequestBody OTPTokenRequest otpTokenRequest, @RequestHeader("Authorization") String authenticate, HttpServletResponse response) {
        String token = new String(Base64.getDecoder().decode(authenticate.substring(6)));
        String[] jwtToken = token.split(":");
        String decodeUsername = jwtToken[0];
        String decodePassword = jwtToken[1];
        User findUserName = userServiceImpl.findUserByUsername(decodeUsername);

        if (findUserName != null && decodePassword.equals(findUserName.getPassword())) {
            //Copy UserName tử database sang UserDetails
            final UserDetails userDetails = new org.springframework.security.core.userdetails.User(decodeUsername, decodePassword, new ArrayList<>());

            final String jwt = jwtTokenUtil.generateToken(userDetails);

            ResponseCookie cookie = ResponseCookie.from("JWT_TOKEN", jwt)
                    .httpOnly(true)   // Chỉ HTTP, không cho JavaScript truy cập
                    .secure(true)    // Đổi thành `true` nếu dùng HTTPS
                    .path("/")        // Cookie có hiệu lực trên toàn bộ ứng dụng
                    .maxAge(Duration.ofHours(1)) // Hết hạn sau 1 giờ
                    .sameSite("None")  // Nếu gọi API từ frontend khác domain
                    .build();

            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            // **🔹 5. Trả về Token trong Response**
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "Authenticated successfully");
            responseBody.put("token", jwt);

            return ResponseEntity.status(HttpStatus.OK).body(jwt);


        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");

        }



    }

    @GetMapping("/test")
    public String test(){
        return "hello";
    }
}
