package personalprojects.restuarantappbackend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import personalprojects.restuarantappbackend.service.UserDetailsServiceImpl;
import personalprojects.restuarantappbackend.utils.JwtUtils;

import java.util.Arrays;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        httpSecurity // Beginne mit dem httpSecurity Objekt
                .csrf(csrf -> csrf.disable()) // CSRF deaktivieren
                .cors(Customizer.withDefaults()) // CORS aktivieren (benötigt CorsConfigurationSource Bean)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // STATELESS Sessions
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // Auth-Pfade erlauben
                        .anyRequest().authenticated() // Alle anderen Anfragen erfordern Authentifizierung
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Deinen Filter als Bean einfügen

        return httpSecurity.build(); // EINMAL am Ende build() aufrufen
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager getAuthenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtils jwtUtil, UserDetailsServiceImpl userDetailsService) {
        // Hier würden Abhängigkeiten für den Filter injiziert, falls nötig
        // z.B. return new JwtAuthenticationFilter();
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService); // Passe dies an, falls der Filter Dependencies braucht
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:DeineFrontendPort", "deine-frontend-domain.com")); // Erlaube dein Frontend
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true); // Wenn Cookies/Auth-Header mitgesendet werden
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Wende auf alle Pfade an
        return source;
    }
}
