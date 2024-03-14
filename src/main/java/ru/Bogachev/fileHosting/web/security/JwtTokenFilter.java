package ru.Bogachev.fileHosting.web.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@AllArgsConstructor
public class JwtTokenFilter extends GenericFilter {

    private final TokenProvider tokenProvider;

    @Override
    @SneakyThrows
    public void doFilter(
            final ServletRequest servletRequest,
            final ServletResponse servletResponse,
            final FilterChain chain
    ) {
        String bearerToken = ((HttpServletRequest) servletRequest)
                .getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken = bearerToken.substring(7);
        }

        if (bearerToken != null && tokenProvider
                .validateToken(bearerToken)
        ) {
            try {
                Authentication authentication = tokenProvider.
                        getAuthentication(bearerToken);
                if (authentication != null) {
                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                }
            } catch (RuntimeException ignored) {
            }
        }
        chain.doFilter(servletRequest, servletResponse);
    }
}
