package com.quickcar.rent.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

import com.quickcar.rent.enums.UserRole;
import com.quickcar.rent.service.auth.CustomOAuth2AuthenticationSuccessHandler;
import com.quickcar.rent.service.jwt.UserService;
import lombok.RequiredArgsConstructor;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final UserService userService;
	private final CustomOAuth2AuthenticationSuccessHandler successHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(this.userService.userDetailsService());
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(request -> request
						.requestMatchers("/api/auth/**", "/api/admin/**", "/api/customer/**", "/api/callback/**")
						.permitAll().requestMatchers("/api/admin/**").hasAuthority(UserRole.ADMIN.name())
						.requestMatchers("/api/custome/**", "/api/callbac/**")
						.hasAnyRole(UserRole.CUSTOMER.name(), UserRole.ADMIN.name()).anyRequest().authenticated())
				.formLogin(login -> login.loginPage("/login").loginProcessingUrl("/loginto")
						.defaultSuccessUrl("/home", true).failureUrl("/login?error").permitAll())
				.oauth2Login(oauth2 -> oauth2.loginPage("/login").successHandler(successHandler)
						.failureUrl("/login?error=true"))
				.sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
				.authenticationProvider(daoAuthenticationProvider())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint()))
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/logout").permitAll());

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return new CustomAuthenticationEntryPoint();
	}
	
	 @Bean
	    public RestTemplate restTemplate() {
	        return new RestTemplate();
	    }
}
