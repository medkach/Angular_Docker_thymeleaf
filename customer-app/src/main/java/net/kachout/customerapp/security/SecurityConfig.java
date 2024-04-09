package net.kachout.customerapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.*;
import java.net.URI;
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    public   ClientRegistrationRepository clientRegistrationRepository;

    public SecurityConfig(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }
    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler) throws Exception {
        return httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(Customizer.withDefaults())
                .authorizeHttpRequests(ar->ar.requestMatchers("/","/oauth2Login/**","/webjars/**","/h2-console/**","/customers/**").permitAll())
                .authorizeHttpRequests(a->a.anyRequest().authenticated())
                .headers(h->h.frameOptions(fo->fo.disable()))
                .csrf(csrf->csrf.ignoringRequestMatchers("/h2-console/**"))
//                .oauth2Login(al->
//                        al.loginPage("/oauth2Login")
//                                .defaultSuccessUrl("/")
//                )
                .oauth2Login(Customizer.withDefaults())
                .logout((logout) -> logout
                                //logout.addLogoutHandler(clearSiteData)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler(oidcLogoutSuccessHandler)
                        .logoutSuccessUrl("/").permitAll()

                )
//                .logout(logout -> logout
//                        .logoutSuccessUrl("http://localhost:8080/realms/realm1/protocol/openid-connect/logout"))
                .exceptionHandling(eh->eh.accessDeniedPage("/notAutorized"))
                .build();
    }
@Bean
     OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler() {
        final OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri(URI.create("http://localhost:8081/login").toString()/*"{baseUrl}?logoutsuccess=true"*/);
        return oidcLogoutSuccessHandler;
    }
    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            final Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            authorities.forEach((authority) -> {
                if (authority instanceof OidcUserAuthority oidcAuth) {
                    mappedAuthorities.addAll(mapAuthorities(oidcAuth.getIdToken().getClaims()));
                    //System.out.println(oidcAuth.getAttributes());
                } else if (authority instanceof OAuth2UserAuthority oauth2Auth) {
                    mappedAuthorities.addAll(mapAuthorities(oauth2Auth.getAttributes()));
                }
            });
            return mappedAuthorities;
        };
    }
    private List<SimpleGrantedAuthority> mapAuthorities(final Map<String, Object> attributes) {
        final Map<String, Object> realmAccess = ((Map<String, Object>)attributes.getOrDefault("realm_access", Collections.emptyMap()));
        final Collection<String> roles = ((Collection<String>)realmAccess.getOrDefault("roles", Collections.emptyList()));
        return roles.stream()
                .map((role) -> new SimpleGrantedAuthority(role))
                .toList();
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
