package com.example.demo.test.Controller;

import com.example.demo.test.Model.User;
import com.example.demo.test.Serivce.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Tag(name = "Users")
public class UserController {

    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create user (public)")
    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        log.debug("HTTP POST /users body name={}, city={}, companyName={}", user.getName(), user.getCity(), user.getCompanyName());
        User saved = userService.create(user);
        return ResponseEntity.ok(saved);
    }

    @Operation(summary = "List users (requires JWT)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<User>> list() {
        log.debug("HTTP GET /users");
        return ResponseEntity.ok(userService.findAll());
    }

    @Operation(summary = "Get user by id (requires JWT)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        log.debug("HTTP GET /users/{}", id);
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}


