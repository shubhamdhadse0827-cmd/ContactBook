package com.contact.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MyConfig {

	@Bean
	public UserDetailsService getUserDetailsService() {
		return new UserDetailsServiceImpl();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(getUserDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		
		return daoAuthenticationProvider;
	}
	
	 @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

	        http
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers("/", "/about", "/contact", "/register", "/login", "/register-send-otp",  "/register_done")
	                .permitAll()
	                .requestMatchers(
	                        "/forgot",
	                        "/send-otp",
	                        "/verify-otp",
	                        "/change-password"
	                ).permitAll()
	                .requestMatchers("/css/**", "/javascript/**", "/image/**").permitAll()
	                .requestMatchers("/user/**").hasRole("USER")
	                .requestMatchers("/admin/**").hasRole("ADMIN")
	                .anyRequest().authenticated()
	            )
	            .formLogin(form -> form
	                .loginPage("/login")
	                .loginProcessingUrl("/do_login")
	                .defaultSuccessUrl("/user/index")
//	                .failureUrl("/login-fail")
	            )
	        
	        	.logout(logout -> logout
	        	    .logoutSuccessUrl("/login?logout")
	        	);

	        return http.build();
	    }
}
