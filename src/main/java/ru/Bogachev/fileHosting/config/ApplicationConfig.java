package ru.Bogachev.fileHosting.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.Bogachev.fileHosting.service.props.MinioProps;
import ru.Bogachev.fileHosting.web.security.JwtTokenFilter;
import ru.Bogachev.fileHosting.web.security.TokenProvider;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class ApplicationConfig {
    private final TokenProvider tokenProvider;

    private final MinioProps minioProps;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioProps.getUrl())
                .credentials(
                        minioProps.getUsername(),
                        minioProps.getPassword())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http)
            throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                (request, response, authException) -> {
                                    response.setStatus(
                                            HttpStatus.UNAUTHORIZED.value()
                                    );
                                    response.getWriter()
                                            .write("Unauthorized.");
                                })
                        .accessDeniedHandler(
                                (request, response, accessDeniedException) -> {
                                    response.setStatus(
                                            HttpStatus.FORBIDDEN.value()
                                    );
                                    response.getWriter()
                                            .write("Unauthorized.");
                                })
                )
                .authorizeHttpRequests(request ->
                        request.requestMatchers("/api/v1/auth/**")
                                .permitAll()
                                .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtTokenFilter(tokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
