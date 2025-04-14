package personalprojects.restuarantappbackend.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import personalprojects.restuarantappbackend.dto.SignUpUser;
import personalprojects.restuarantappbackend.model.User;
import personalprojects.restuarantappbackend.repository.UserRepository;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    @Autowired
    public AuthService(UserRepository  userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(SignUpUser signUpUser) {
        if(userRepository.existsByEmail(signUpUser.getEmail())) {
            throw new RuntimeException("Email already exists");
        } else if (userRepository.existsByUsername(signUpUser.getUsername())) {
            throw new RuntimeException("Username already exists");
        } else {
            User user = new User();

            user.setUsername(signUpUser.getUsername());
            user.setEmail(signUpUser.getEmail());
            user.setPassword(passwordEncoder.encode(signUpUser.getPassword()));

            user.setEnabled(false);
            userRepository.save(user);

            System.out.println(user.getUsername() + "saved successfully. Waiting for verification");
        }
    }
}
