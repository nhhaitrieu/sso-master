package com.auth.application.controller;

import com.auth.application.config.JwtTokenUtil;
import com.auth.application.model.*;
import com.auth.application.service.impl.ForgetPasswordService;
import com.auth.application.service.impl.OTPService;
import com.auth.application.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
    @Autowired
    private ForgetPasswordService forgetPasswordService;

    @PostMapping(value = "/token", produces = "application/xml")
    public ResponseEntity<OTPTokenResponse> getToken(@RequestBody OTPTokenRequest otpTokenRequest, HttpServletResponse response) {
        OTPTokenResponse otpTokenResponse = otpService.processTokenRequest(otpTokenRequest, response).getBody();
        return ResponseEntity.ok(otpTokenResponse);
    }

    @PostMapping(value = "/create", produces = "application/xml")
    public ResponseEntity<User> createUser(@RequestBody @Valid User user) {
        return userServiceImpl.createUser(user);
    }

    @PostMapping(value = "/forget-password", produces = "application/xml")
    public ResponseEntity<ForgetPasswordResponse> forgetPassword(@RequestBody ForgetPasswordRequest request) {
        return forgetPasswordService.handleForgetPassword(request.getEmail());
    }

    @PostMapping(value = "/reset-password", produces = "application/xml")
    public ResponseEntity<ResetPasswordResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        return forgetPasswordService.handleResetPassword(request);
    }
}