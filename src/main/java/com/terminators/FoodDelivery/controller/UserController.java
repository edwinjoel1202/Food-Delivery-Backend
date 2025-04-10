package com.terminators.FoodDelivery.controller;

import com.terminators.FoodDelivery.model.User;
import com.terminators.FoodDelivery.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateCurrentUser(@RequestBody User updatedUser,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.updateUser(updatedUser, userDetails.getUsername());
        return ResponseEntity.ok(user);
    }
}