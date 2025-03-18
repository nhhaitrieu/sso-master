package com.auth.application.controller;

import com.auth.application.config.JwtTokenUtil;
import com.auth.application.model.ResponseObject;
import com.auth.application.model.User;
import com.auth.application.service.UserService;
import com.auth.application.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user/api/v1")
public class UserController {


    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // API tạo User mới
    @PostMapping("/insert")
    public ResponseEntity<ResponseObject> createUser(@RequestBody User user, @RequestHeader("Authorization") String authHeader) {
        log.info("Creating user with userID: {}", user.getUsername());
        if (userServiceImpl.findUserByUsername(user.getUsername()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // **🔹 2. Mã hóa mật khẩu & Lưu User vào database**
        User createdUser = userServiceImpl.saveUser(user);

        // **🔹 3. Trả về Response**
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // API lấy danh sách tất cả Users
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userServiceImpl.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // API lấy thông tin User theo ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userServiceImpl.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // API lấy thông tin User theo Username
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userServiceImpl.findUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update/{id}")
    public Object updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return (ResponseEntity<ResponseObject>) userServiceImpl.updateUser(id, updatedUser);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseObject> deleteUser(@PathVariable Long id) {
        return userServiceImpl.deleteUser(id);
    }




}
