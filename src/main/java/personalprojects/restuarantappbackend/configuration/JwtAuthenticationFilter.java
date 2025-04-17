package personalprojects.restuarantappbackend.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import personalprojects.restuarantappbackend.service.UserDetailsServiceImpl;
import personalprojects.restuarantappbackend.utils.JwtUtils;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 1. JWT aus dem Request extrahieren
            String jwt = parseJwt(request);

            // 2. Prüfen, ob ein Token vorhanden ist UND ob er gültig ist
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // 3. Benutzernamen aus dem Token holen
                String username = jwtUtils.getUsernameFromJwtToken(jwt);

                // 4. UserDetails aus dem UserDetailsService laden
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 5. Authentication Token erstellen (WENN noch keine Authentifizierung im Context ist)
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Erstelle das Authentication-Objekt, das Spring Security versteht
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, // Principal
                            null,        // Credentials (Passwort wird nicht benötigt/verwendet)
                            userDetails.getAuthorities()); // Berechtigungen/Rollen

                    // Setze zusätzliche Details aus dem Request (z.B. IP-Adresse, Session-ID)
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 6. Setze die Authentifizierung in den SecurityContextHolder
                    // Dies markiert den Benutzer als "eingeloggt" für DIESEN Request
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("Authentication successful for user: {}");
                } else {
                    logger.debug("SecurityContext already contained Authentication for user: {}");
                }
            } else {
                // Dieser Fall tritt ein, wenn kein Token da ist ODER der Token ungültig ist.
                if (jwt == null) {
                    logger.debug("No JWT token found in request header.");
                } else {
                    // Validierung ist fehlgeschlagen (Fehler wurde schon in jwtUtils.validateJwtToken geloggt)
                    logger.debug("JWT Token validation failed.");
                }
                // Hier muss KEINE Authentifizierung gesetzt werden.
                // Spring Security wird den Zugriff auf geschützte Ressourcen verweigern,
                // da SecurityContextHolder.getContext().getAuthentication() null bleibt.
            }
        } catch (Exception e) {
            // Fängt unerwartete Fehler während des Prozesses ab
            logger.error("Cannot set user authentication: {}");
            // Es ist wichtig, den SecurityContext im Fehlerfall zu leeren
            SecurityContextHolder.clearContext();
        }

        // 7. IMMER die Filterkette fortsetzen!
        // Übergibt Request und Response an den nächsten Filter oder an den Controller.
        // Ohne diesen Aufruf wird die Anfrage blockiert.
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        // Prüft, ob der Header existiert und mit "Bearer " beginnt
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            // Extrahiert den Token-Teil (nach "Bearer ")
            return headerAuth.substring(7);
        }

        // Kein gültiger Bearer-Token gefunden
        return null;
    }

}
