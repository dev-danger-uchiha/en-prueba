package com.proyecto.budgetmap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request,
                    HttpServletResponse response,
                    Authentication authentication) throws IOException, ServletException {

                var roles = authentication.getAuthorities().toString();

                if (roles.contains("ADMIN")) {
                    response.sendRedirect("/admin/dashboard");
                } else if (roles.contains("MODERADOR")) {
                    response.sendRedirect("/moderador/dashboard");
                } else if (roles.contains("PROPIETARIO_ESTABLECIMIENTO")) {
                    response.sendRedirect("/establecimiento/dashboard");
                } else if (roles.contains("CLIENTE")) {
                    response.sendRedirect("/cliente/dashboard");
                } else {
                    response.sendRedirect("/");
                }
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // Habilitar CSRF con configuración segura
                .csrf(csrf -> csrf
                    .ignoringRequestMatchers("/api/**") // Solo para APIs REST si las tienes
                )
                
                // Configuración de autorización
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas
                        .requestMatchers(
                            "/", 
                            "/index", 
                            "/auth/**", 
                            "/css/**", 
                            "/js/**",
                            "/images/**",
                            "/error"
                        ).permitAll()
                        
                        // Rutas por rol
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/moderador/**").hasRole("MODERADOR")
                        .requestMatchers("/establecimiento/**").hasRole("PROPIETARIO_ESTABLECIMIENTO")
                        .requestMatchers("/cliente/**").hasRole("CLIENTE")
                        
                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )
                
                // Configuración de login
                .formLogin(login -> login
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .successHandler(successHandler())
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )
                
                // Configuración de logout
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .logoutSuccessUrl("/?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                
                // Manejo de acceso denegado
                .exceptionHandling(ex -> ex
                    .accessDeniedPage("/auth/login?error=access-denied")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}