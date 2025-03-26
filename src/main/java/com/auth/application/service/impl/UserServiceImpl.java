package com.auth.application.service.impl;

import com.auth.application.config.JwtTokenUtil;
import com.auth.application.model.*;
import com.auth.application.repository.RoleRepository;
import com.auth.application.repository.UserRepository;
import com.auth.application.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;



    // Lưu user mới vào database (encode password trước khi lưu)
    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // API tạo user mới
    @Override
    public ResponseEntity<User> createUser(User user) {
        // Check trùng username
        if (findUserByUsername(user.getUsername()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(null); // hoặc trả custom responseObject
        }

        // Check trùng email
        if (findUserByEmail(user.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(null);
        }

        // Check trùng sđt
        if (userRepository.findByMsisdn(user.getMsisdn()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(null);
        }

        User createdUser = saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // Cập nhật password cho user
    public void updatePassword(User user, String newPassword) {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password must not be empty");
        }
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    // Lưu role (chưa implement)
    @Override
    public Role saveRole(Role role) {
        return null;
    }

    // Thêm role cho user (chưa implement)
    @Override
    public void addToUser(String username, String rolename) {

    }

    // Lấy tất cả user
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Lấy user theo ID
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    // Cập nhật user theo ID
    @Override
    public Object updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        // Check nếu cập nhật email và email đã tồn tại
        if (!existingUser.getEmail().equals(updatedUser.getEmail())
                && findUserByEmail(updatedUser.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseObject("ERROR", "Email already exists", ""));
        }

        // Check nếu cập nhật msisdn và msisdn đã tồn tại
        if (!existingUser.getMsisdn().equals(updatedUser.getMsisdn())
                && userRepository.findByMsisdn(updatedUser.getMsisdn()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseObject("ERROR", "MSISDN already exists", ""));
        }

        // Cập nhật các thông tin khác
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setStatus(updatedUser.isStatus());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setMsisdn(updatedUser.getMsisdn());

        userRepository.save(existingUser);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Update user successfully", existingUser)
        );
    }

    // Xóa user theo ID
    @Override
    public ResponseEntity<ResponseObject> deleteUser(Long id) {
        boolean exists = userRepository.existsById(id);
        if (exists) {
            userRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "User deleted successfully", ""));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseObject("ERROR", "User not found", ""));
    }

    // Tìm user theo email
    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Tìm user theo username
    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    // Load user cho Spring Security (login)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), new ArrayList<>());
    }
}