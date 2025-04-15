package personalprojects.restuarantappbackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import personalprojects.restuarantappbackend.dto.SignUpUser;
import personalprojects.restuarantappbackend.service.AuthService;

@RestController
public class AuthController {

    private AuthService authServive;

    @PostMapping("/api/auth/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpUser signUpUser) {
        // Logic to register a new user
        try {
            authServive.registerUser(signUpUser);

            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error registering user: " + e.getMessage());
        }
    }

    @GetMapping("/api/auth/verify")
    public ResponseEntity<?> verifyUser(@RequestParam("token") String token) {
        // Logic to verify the user
        try {
            authServive.verifyUser(token);
            return ResponseEntity.ok("User verified successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error verifying user: " + e.getMessage());
        }
    }
}
