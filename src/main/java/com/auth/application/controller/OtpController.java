package com.auth.application.controller;

import com.auth.application.config.JwtTokenUtil;
import com.auth.application.model.*;
import com.auth.application.service.impl.OTPService;
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
    @Autowired
    private OTPService otpService;


    @PostMapping(value = "/token", produces="application/xml")
    public ResponseEntity<OTPTokenResponse> getToken(@RequestBody OTPTokenRequest otpTokenRequest,HttpServletResponse response) {
        OTPTokenResponse otpTokenResponse = otpService.processTokenRequest(otpTokenRequest, response).getBody();
        return ResponseEntity.ok(otpTokenResponse);
    }

    @PostMapping(value = "/create", produces = "application/xml")
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
