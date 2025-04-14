package personalprojects.restuarantappbackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
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
}
