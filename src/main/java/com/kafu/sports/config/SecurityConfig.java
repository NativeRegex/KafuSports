package com.kafu.sports.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 1. ALL PUBLIC PAGES (Anyone can view)
                .requestMatchers(
                    "/", "/login", "/error",
                    "/league", "/fixtures", "/results",
                    "/teams", "/teams/**",
                    "/players", "/stats",
                    "/css/**", "/js/**", "/images/**", "/webjars/**"
                ).permitAll()
                
                // 2. ADMIN PAGES (Only logged-in admin can access)
                .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
                
                // 3. EVERYTHING ELSE (Requires login, just in case)
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/admin", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails admin = User.withDefaultPasswordEncoder()
            .username("admin")
            .password("kafu2026")
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(admin);
    }
}
