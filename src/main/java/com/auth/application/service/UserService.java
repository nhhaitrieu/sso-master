package com.auth.application.service;

import com.auth.application.model.ResponseObject;
import com.auth.application.model.Role;
import com.auth.application.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService extends UserDetailsService {
    User saveUser(User user);
    Role saveRole(Role role);
    void addToUser(String username, String rolename);
    List<User> getAllUsers();
    User getUserById(Long id);
    Object updateUser(Long id, User updatedUser);
    ResponseEntity<ResponseObject> deleteUser(Long id); //

    User findUserByUsername(String userName);


}
