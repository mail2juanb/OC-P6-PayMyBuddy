package com.ocP6.PayMyBuddy.configuration;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    private static final String[] PERMIT_ALL = {"/css/**", "/login", "registerPage"};

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PERMIT_ALL).permitAll()
                        .anyRequest().authenticated()                               // Toutes les requêtes nécessitent une authentification
                )
                .userDetailsService(customUserDetailsService)                       // Utilise CustomUserDetailsService pour charger les utilisateurs
                .formLogin(form -> form
                        .loginPage("/login")                                        // Page de connexion PayMyBuddy
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/transfertPage")                        // Redirige après authentification réussie
                        .failureUrl("/login?error=true")         // Paramètre en cas d'échec
                        .permitAll()
                )
                .build();

    }
}

