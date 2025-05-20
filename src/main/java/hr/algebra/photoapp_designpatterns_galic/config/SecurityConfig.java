package hr.algebra.photoapp_designpatterns_galic.config;

import hr.algebra.photoapp_designpatterns_galic.model.AuthProvider;
import hr.algebra.photoapp_designpatterns_galic.model.Role;
import hr.algebra.photoapp_designpatterns_galic.model.User;
import hr.algebra.photoapp_designpatterns_galic.repository.UserRepository;
import hr.algebra.photoapp_designpatterns_galic.service.CustomOAuth2UserService;
import hr.algebra.photoapp_designpatterns_galic.service.MyUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.Map;

@Configuration
public class SecurityConfig {
    private final MyUserDetailsService myUserDetailsService;
    private final UserRepository userRepository;

    public SecurityConfig(MyUserDetailsService myUserDetailsService, UserRepository userRepository) {
        this.myUserDetailsService = myUserDetailsService;
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/", "/login", "/external-login", "/register",
                                "/public/**", "/h2-console/**", "/thumbnails/**",
                                "/uploads/**", "/uploads/thumbnails/**", "/photos/view/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/photos/**", "/profile/**").hasAnyRole("REGISTERED", "ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(
                        exception -> exception
                                .accessDeniedPage("/error/403")
                )
                .formLogin(form -> form.defaultSuccessUrl("/photos", true))
                .logout(LogoutConfigurer::permitAll)
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/external-login")
                        .defaultSuccessUrl("/oauth2/post-login", true)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService())     // for GitHub
                                .oidcUserService(oidcUserService())   // for Google
                        )
                );
        return http.build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        return new CustomOAuth2UserService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http, PasswordEncoder encoder) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(myUserDetailsService).passwordEncoder(encoder);
        return builder.build();
    }

    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        return userRequest -> {
            OidcUser oidcUser = new OidcUserService().loadUser(userRequest);
            Map<String, Object> attributes = oidcUser.getAttributes();
            String email = (String) attributes.get("email");

            if (email == null) {
                throw new OAuth2AuthenticationException("Email not found");
            }

            User user = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setPassword(""); // no password for OAuth users
                newUser.setRole(Role.REGISTERED);
                newUser.setAuthProvider(AuthProvider.GOOGLE);
                return userRepository.save(newUser);
            });

            return new DefaultOidcUser(
                    List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                    oidcUser.getIdToken(),
                    oidcUser.getUserInfo()
            );
        };
    }
}