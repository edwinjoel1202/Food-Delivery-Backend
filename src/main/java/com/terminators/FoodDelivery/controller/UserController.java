package com.terminators.FoodDelivery.controller;

import com.terminators.FoodDelivery.model.User;
import com.terminators.FoodDelivery.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PutMapping("/me/coordinates")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<User> updateCoordinates(@RequestBody CoordinatesRequest coordinates,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        User updatedUser = userService.updateUserCoordinates(
                userDetails.getUsername(),
                coordinates.getLatitude(),
                coordinates.getLongitude()
        );
        return ResponseEntity.ok(updatedUser);
    }

    public static class CoordinatesRequest {
        private Double latitude;
        private Double longitude;

        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
    }
}