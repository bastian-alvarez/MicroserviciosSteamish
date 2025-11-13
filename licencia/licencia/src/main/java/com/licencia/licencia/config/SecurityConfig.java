package com.licencia.licencia.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api-docs/**",
            "/api/licencias/**",
            "/internal/licencias/**").permitAll()
        .anyRequest().authenticated())
      .httpBasic(Customizer.withDefaults());
    return http.build();
  }

  // Usuarios en memoria (demo)
  @Bean
  public UserDetailsService users() {
    UserDetails admin = User.withUsername("admin").password("{noop}admin").roles("ADMIN").build();
    UserDetails user  = User.withUsername("user").password("{noop}user").roles("USER").build();
    return new InMemoryUserDetailsManager(admin, user);
  }
}