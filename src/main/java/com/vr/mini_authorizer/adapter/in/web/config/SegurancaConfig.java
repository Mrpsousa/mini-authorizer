package com.vr.mini_authorizer.adapter.in.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * autenticação BASIC exigida pelo contrato (login = username, senha = password).
 * é a credencial da API, sem relação com a senha do cartão.
 */
@Configuration
public class SegurancaConfig {

    /**
     * - csrf desligado: API REST sem sessão nem cookie (senão os POSTs levam 403)
     * - stateless: cada requisição manda a credencial, nada fica em sessão
     * - /error liberado: senão um erro interno viraria 401 em vez do status real
     */
    @Bean
    public SecurityFilterChain filtroDeSeguranca(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    // guarda a senha com hash (bcrypt), nunca em texto puro na memória
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * único usuário da API, lido do application.properties
     */
    @Bean
    public UserDetailsService usuarios(
            @Value("${autorizador.seguranca.usuario}") String usuario,
            @Value("${autorizador.seguranca.senha}") String senha,
            PasswordEncoder passwordEncoder) {
        return new InMemoryUserDetailsManager(
                User.withUsername(usuario)
                        .password(passwordEncoder.encode(senha))
                        .roles("API")
                        .build());
    }
}
