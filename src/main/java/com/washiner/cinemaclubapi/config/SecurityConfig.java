package com.washiner.cinemaclubapi.config;

import com.washiner.cinemaclubapi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// @Configuration → declara beans do Spring
// @EnableWebSecurity → ativa configuração de segurança
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final UsuarioRepository usuarioRepository;

    // Define quais endpoints são públicos e quais precisam de autenticação
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita CSRF — não precisamos para APIs REST com JWT
                .csrf(AbstractHttpConfigurer::disable)

                // CORS — usa nossa configuração
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // STATELESS → Spring não cria sessão HTTP
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Regras de acesso
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers("/v1/auth/**").permitAll()
                        // Swagger público
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        // GET filmes → USER e ADMIN
                        .requestMatchers(HttpMethod.GET, "/v1/filmes/**").hasAnyRole("USER", "ADMIN")
                        // POST, DELETE → só ADMIN
                        .requestMatchers(HttpMethod.POST, "/v1/filmes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/v1/filmes/**").hasRole("ADMIN")
                        // qualquer outra → autenticado
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Define quais origens podem chamar a API
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Origens permitidas — só esses domínios
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",   // React
                "http://localhost:4200",   // Angular
                "http://localhost:5173"    // Vite/Vue
        ));

        // Métodos HTTP permitidos
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers permitidos
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // Ensina o Spring a buscar o usuário pelo email
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado: " + email));
    }

    // Junta UserDetailsService + PasswordEncoder
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // BCrypt → algoritmo de criptografia de senha
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager → usado no AuthService para validar login
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
