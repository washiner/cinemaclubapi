package com.washiner.cinemaclubapi.config;

import com.washiner.cinemaclubapi.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// OncePerRequestFilter → roda UMA vez por requisição
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal
            (HttpServletRequest request,
             HttpServletResponse response,
             FilterChain filterChain)
            throws ServletException, IOException {

        // Pega o header Authorization da requisição
        // Formato esperado: "Bearer xxxxx.yyyyy.zzzzz"
        String authHeader = request.getHeader("Authorization");

        // Se não tem header ou não começa com "Bearer " → passa direto
        // Endpoints públicos (login, register) caem aqui
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        // Remove o "Bearer " e fica só com o token
        // "Bearer xxxxx.yyyyy.zzzzz" → "xxxxx.yyyyy.zzzzz"
        String token = authHeader.substring(7);


        //🔑 Raciocínio:
        //Tem header Authorization?
        //  NÃO → endpoint público → passa direto
        //  SIM → extrai o token → continua validando

        // Extrai o email do token
        // try-catch → se token expirado não estoura 500


        String email = null;
        try {
            email = jwtService.extrairEmail(token);
        } catch (Exception e) {
            // Token inválido ou expirado → passa sem autenticar
            // Spring Security devolve 401 automaticamente
            filterChain.doFilter(request, response);
            return;
        }

        // Se tem email E ainda não está autenticado nessa requisição
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Busca o usuário no banco pelo email
            var usuario = usuarioRepository.findByEmail(email)
                    .orElse(null);

            // Se usuário existe E token é válido → autentica
            if (usuario != null && jwtService.tokenValido(token, usuario)) {

                // Cria o objeto de autenticação do Spring Security
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                usuario,
                                null,
                                usuario.getAuthorities()
                        );

                // Adiciona detalhes da requisição
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Registra no SecurityContext — Spring sabe quem está autenticado
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Sempre chama no final — passa para o próximo filtro
        filterChain.doFilter(request, response);
    }
}

