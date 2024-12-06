package com.ocP6.PayMyBuddy.configuration;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
                // Configuring secure headers
                .headers(headers -> headers
                        .contentTypeOptions(Customizer.withDefaults())  // Ajoute X-Content-Type-Options: nosniff
                        .defaultsDisabled()                             // Désactive les headers par défaut pour une configuration personnalisée
                        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; script-src 'self'"))    // Politique CSP stricte
                        .frameOptions(frame -> frame.sameOrigin())      // Autorise les iframes du même domaine
                        .httpStrictTransportSecurity(hsts -> hsts       // HTTPS forcé
                                .maxAgeInSeconds(31536000)              // 1 an
                                .includeSubDomains(true))               // Inclut les sous-domaines
                        .permissionsPolicy(policy -> policy.policy("geolocation=(), microphone=(), camera=()"))         // Restrictions navigateur
                )

                // CSRF configuration (reactivated to protect sensitive transactions)
                .csrf(csrf -> csrf.ignoringRequestMatchers("/css/**", "/login", "/register"))
                //.csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PERMIT_ALL).permitAll()
                        .anyRequest().authenticated()                               // Toutes les requêtes nécessitent une authentification
                )
                .userDetailsService(customUserDetailsService)                       // Utilise CustomUserDetailsService pour charger les utilisateurs
                .formLogin(form -> form
                        .loginPage("/login")                                        // Page de connexion PayMyBuddy
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/transfert")                            // Redirige après authentification réussie
                        .failureUrl("/login?error=true")         // Paramètre en cas d'échec
                        .permitAll()
                )
                .build();

    }
}

