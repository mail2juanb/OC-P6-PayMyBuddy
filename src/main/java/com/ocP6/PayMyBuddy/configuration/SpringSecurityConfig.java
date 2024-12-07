package com.ocP6.PayMyBuddy.configuration;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
//@EnableWebSecurity Apparemment pas utile de le préciser --
@RequiredArgsConstructor
public class SpringSecurityConfig {


    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    private static final String[] PERMIT_ALL = {"/css/**", "/login", "register"};

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // CSRF configuration (reactivated to protect sensitive transactions)
                .csrf(csrf -> csrf.ignoringRequestMatchers("/css/**", "/login", "/register"))
                //.csrf(AbstractHttpConfigurer::disable)

                // Authorisation management
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PERMIT_ALL).permitAll()                    // Autoriser les endpoints publics
                        .anyRequest().authenticated()                               // Requiert une authentification pour le reste
                )
                // Authentication configuration
                .userDetailsService(customUserDetailsService)                   // Service pour charger les utilisateurs
                .formLogin(form -> form
                        .loginPage("/login")                                    // Page de connexion personnalisée
                        .usernameParameter("email")                             // Paramètre pour l'email
                        .passwordParameter("password")                          // Paramètre pour le mot de passe
                        .defaultSuccessUrl("/transfert")                        // Rediriger après authentification réussie
                        .failureUrl("/login?error=true")     // Rediriger après échec
                        .permitAll()                                            // Accessible à tous
                )

                // Session configuration
                .sessionManagement(session -> session
                        .maximumSessions(1)                         // Limite à une session par utilisateur
                        .maxSessionsPreventsLogin(true)             // Bloque la connexion si une session existe déjà
                )

                .build();

    }
}

