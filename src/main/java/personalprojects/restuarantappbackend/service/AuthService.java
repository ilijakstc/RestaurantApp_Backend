package personalprojects.restuarantappbackend.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import personalprojects.restuarantappbackend.dto.SignUpUser;
import personalprojects.restuarantappbackend.model.User;
import personalprojects.restuarantappbackend.model.VerificationToken;
import personalprojects.restuarantappbackend.repository.UserRepository;
import personalprojects.restuarantappbackend.repository.VerificationTokenRepository;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private VerificationTokenRepository verificationTokenRepository;
    private JavaMailSender mailSender;

    @Autowired
    public AuthService(UserRepository  userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(SignUpUser signUpUser) {
        if (userRepository.existsByEmail(signUpUser.getEmail())) {
            throw new RuntimeException("Email already exists");
        } else if (userRepository.existsByUsername(signUpUser.getUsername())) {
            throw new RuntimeException("Username already exists");
        } else {
            User user = new User();

            user.setUsername(signUpUser.getUsername());
            user.setEmail(signUpUser.getEmail());
            user.setPassword(passwordEncoder.encode(signUpUser.getPassword()));

            user.setEnabled(false);
            User savedUser = userRepository.save(user);
            // Create a verification token for the user
            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken(token, user);
            verificationTokenRepository.save(verificationToken);

            sendVerificationEmail(savedUser, token);

            System.out.println(user.getUsername() + "saved successfully. Waiting for verification");
        }
    }

    private void sendVerificationEmail (User user, String token){
        // Logic to send verification email
        try {
            String recipient = user.getEmail();
            String link = "http://localhost:8080/api/auth/verify?token=" + token;
            String subject = "Verify your email";
            String body = "Click the link to verify your email: " + link;

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(recipient);
            mailMessage.setSubject(subject);
            mailMessage.setText(body);

            mailSender.send(mailMessage);
        } catch (Exception e) {
            throw new RuntimeException("Error sending verification mail" + user.getEmail());
        }

    }

    public void verifyUser(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
            throw new RuntimeException("Invalid token");
        }

        if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
            verificationTokenRepository.delete(verificationToken);
            throw new RuntimeException("Token expired");
        }

        User user = verificationToken.getUser();

        if (user.getEnabled()) {
            verificationTokenRepository.delete(verificationToken);
            System.out.println("Account für User " + user.getUsername() + " war bereits aktiviert.");
            return;
        }

        user.setEnabled(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);

        System.out.println(user.getUsername() + " verified successfully");
    }
}
