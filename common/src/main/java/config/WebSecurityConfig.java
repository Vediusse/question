package config;

import exception.JwtAccessDeniedHandler;
import exception.JwtAuthenticationEntryPoint;
import filter.CustomUserDetailsService;
import filter.JwtTokenFilter;
import filter.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .requestMatchers(skipPaths()).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JwtTokenFilter(jwtTokenProvider, skipPaths()), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                .accessDeniedHandler(new JwtAccessDeniedHandler());

        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public RequestMatcher skipPaths() {
        return new OrRequestMatcher(
                new AntPathRequestMatcher("/users/auth", HttpMethod.POST.name()),
                new AntPathRequestMatcher("/users/login", HttpMethod.POST.name()),
                new AntPathRequestMatcher("/users/users", HttpMethod.GET.name()),
                new AntPathRequestMatcher("/users/users/**", HttpMethod.GET.name()),
                new AntPathRequestMatcher("/answers/**", HttpMethod.GET.name()),
                new AntPathRequestMatcher("/answers", HttpMethod.GET.name()),
                new AntPathRequestMatcher("/comments", HttpMethod.GET.name()),
                new AntPathRequestMatcher("/comments/**", HttpMethod.GET.name()),
                new AntPathRequestMatcher("/questions/**", HttpMethod.GET.name()),
                new AntPathRequestMatcher("/api-docs", HttpMethod.GET.name()),
                new AntPathRequestMatcher("/api-docs/**", HttpMethod.GET.name()),
                new AntPathRequestMatcher("/swagger-ui/**", HttpMethod.GET.name()),
                new AntPathRequestMatcher("/v3/api-docs/**", HttpMethod.GET.name()),
                new AntPathRequestMatcher("/questions", HttpMethod.GET.name())
        );
    }
}
