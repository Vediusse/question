package filter;

import exception.CustomAuthenticationException;
import io.jsonwebtoken.SignatureException;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import response.ResponseError;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private final RequestMatcher skipPaths;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider, RequestMatcher skipPaths) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.skipPaths = skipPaths;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = jwtTokenProvider.resolveToken(request);


        if ("/questions/paginated".equals(request.getRequestURI())) {
            if(token != null && jwtTokenProvider.validateToken(token)){
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
                filterChain.doFilter(request, response);
                return;
            }
            filterChain.doFilter(request, response);
            return;
        }

        if (skipPaths.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
                filterChain.doFilter(request, response);
                return;
            }else {
                ResponseError error = new ResponseError("Ошибка аутентификации ", HttpStatus.UNAUTHORIZED);
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(new ObjectMapper().writeValueAsString(error));
                return;
            }
        } catch (CustomAuthenticationException e) {
            ResponseError error = new ResponseError("Ошибка аутентификации: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(error));
            return;
        } catch (Exception e) {
            ResponseError error = new ResponseError("Ошибка при обработке JWT токена: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(error));
            return;
        }

    }
}
