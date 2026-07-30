package com.example.demo.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {
    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity? ->
            web!!.ignoring()
                .requestMatchers(
                    "/swagger-ui/**", "/v3/api-docs*/**",
                    "/h2-console/**",
                    "/scalar/**", "/health/**"
                )
        }
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity, jwtTokenService: JwtTokenService): SecurityFilterChain? {
        http // REST API->CSRF無効
            .csrf { it.disable() } // セッションを使わない

            .sessionManagement { session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            }
            .exceptionHandling { exception ->
                exception
                    .authenticationEntryPoint(AuthenticationEntryPoint { request: HttpServletRequest?, response: HttpServletResponse?, authException: AuthenticationException? ->
                        response!!.sendError(
                            401,
                            "Unauthorized"
                        )
                    }
                    )
                    .accessDeniedHandler(AccessDeniedHandler { request: HttpServletRequest?, response: HttpServletResponse?, accessDeniedException: AccessDeniedException? ->
                        response!!.sendError(
                            401,
                            "Unauthorized"
                        )
                    }
                    )
            } // 認可設定

            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/auth/login").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/books/{id}", "/api/books/search", "/api/books/openbd")
                    .permitAll()
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenService),
                UsernamePasswordAuthenticationFilter::class.java
            )
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun userDetailsService(
        passwordEncoder: PasswordEncoder,
        @Value("\${app.auth.username}") username: String,
        @Value("\${app.auth.password}") password: String?
    ): UserDetailsService {
        val user = User.withUsername(username)
            .password(passwordEncoder.encode(password))
            .roles("USER")
            .build()
        return InMemoryUserDetailsManager(user)
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager? {
        return authenticationConfiguration.getAuthenticationManager()
    }
}
