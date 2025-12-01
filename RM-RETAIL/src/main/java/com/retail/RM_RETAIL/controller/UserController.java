package com.retail.RM_RETAIL.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.retail.RM_RETAIL.entity.User;
import com.retail.RM_RETAIL.repository.UserRepository;
import com.retail.RM_RETAIL.service.UserService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000") // allow React frontend
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private  UserRepository userRepository;

    // Save user
    @PostMapping
    public User addUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    // Get all users
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Delete user by ID
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "User deleted successfully";
    }
    
    // ✅ Login validation
    @PostMapping("/login")
    public String loginUser(@RequestBody User loginRequest) {
        User existingUser = userRepository.findByEmail(loginRequest.getEmail());
        if (existingUser != null && existingUser.getPassword().equals(loginRequest.getPassword())) {
            return "Login successful";
        } else {
            return "Invalid email or password";
        }
    }
}

