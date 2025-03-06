package com.auth.application.controller;

import com.auth.application.dto.request.UserCreationRequest;
import com.auth.application.model.User;
import com.auth.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/users")
    User createUser(@RequestBody UserCreationRequest request){
        return userService.createRequest(request);
    }

    @GetMapping("/getUser")
    List<User> getUser(){
        return userService.getUser();
    }

    @GetMapping("/getUser/{userId}")
    User getUser(@PathVariable("userId") Long userId){
        return userService.getUser(userId);
    }

}
