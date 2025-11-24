package rayka.sos.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import rayka.sos.service.AutenticacaoService;

import java.io.IOException;

@Component
@AllArgsConstructor
public class AutenticacaoFilter extends OncePerRequestFilter {
    private TokenServiceJWT tokenServiceJWT;
    private AutenticacaoService autenticacaoService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String tokenJWT = recuperarToken(request);
        System.out.println("TokenJWT: " + tokenJWT);
        // Se não existir token na requisição o SecurityConfig deverá bloquear
        if (tokenJWT != null) {
            String subject = this.tokenServiceJWT.getSubject(tokenJWT);
            System.out.println("Login da req: " + subject);

            UserDetails userDetails = this.autenticacaoService.loadUserByUsername(subject);
            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null) {
            return token.replace("Bearer", "").trim();
        }
        return null;
    }
}
