package org.example.edumanager.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .securityContext(sc -> sc
                        .securityContextRepository(new HttpSessionSecurityContextRepository())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/api/auth/**", "/student/login-page", "/staff/login-page", "/css/**", "/js/**")
                        .permitAll()
                        .requestMatchers("/student/**").hasRole("STUDENT")
                        .requestMatchers("/staff/**").hasRole("STAFF")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutSuccessUrl("/")
                );

        return http.build();
    }
}
