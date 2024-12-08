package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public UserDetailsService detailsService() {

		UserDetails user = User.withUsername("sagar").password(encoder().encode("12345")).roles("USER").build();

		UserDetails admin = User.withUsername("admin").password(encoder().encode("12345")).roles("ADMIN").build();

		return new InMemoryUserDetailsManager(admin, user);

	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter)
			throws Exception {

		// Disable CSRF as it's not necessary when using JWT (stateless)
		http.csrf(csrf -> csrf.disable())

				// Set up authorization rules
				.authorizeHttpRequests(authz -> authz.requestMatchers("/home/authenticate").permitAll() // Allow access
																										// to the
																										// authentication
																										// endpoint
						.requestMatchers("/home/getall", "/home/get/**").hasRole("USER") // Only users with USER role
																							// can access /home/getall
						// .requestMatchers("/home/get/**").hasRole("ADMIN") // Only users with ADMIN
						// role can access /home/get/{id}
						.requestMatchers("/home/post", "/home/put").hasRole("ADMIN") // Admin-only endpoints for POST
																						// and PUT
						.anyRequest().authenticated() // All other requests must be authenticated
				)

				// Stateless session management, suitable for JWT tokens
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// Enable basic authentication (if needed for other purposes)
				.httpBasic();

		// Add JWT filter before UsernamePasswordAuthenticationFilter
		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

		// Build the security filter chain
		return http.build();
	}

}
