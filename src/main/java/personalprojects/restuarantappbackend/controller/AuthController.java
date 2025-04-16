package personalprojects.restuarantappbackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import personalprojects.restuarantappbackend.dto.JwtResponse;
import personalprojects.restuarantappbackend.dto.LoginRequest;
import personalprojects.restuarantappbackend.dto.SignUpUser;
import personalprojects.restuarantappbackend.service.AuthService;
import personalprojects.restuarantappbackend.utils.JwtUtils;

import javax.naming.AuthenticationException;

@RestController
public class AuthController {

    private AuthService authServive;

    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;

    public AuthController(AuthService authServive, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authServive = authServive;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

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

    @PostMapping("/api/auth/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Logic to authenticate the user
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
            authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            String jwt = jwtUtils.generateTokenFromUser(authenticationToken.toString());
            JwtResponse jwtResponse = new JwtResponse(jwt, "Bearer");

            return ResponseEntity.ok("Logged in successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}
