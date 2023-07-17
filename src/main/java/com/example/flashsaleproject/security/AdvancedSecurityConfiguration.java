//package com.example.flashsaleproject.security;
//
//import static org.springframework.security.config.Customizer.withDefaults;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.ProviderManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class AdvancedSecurityConfiguration {
//	
//	CustomeUserDetailsService userDetailsService;
//
//	public AdvancedSecurityConfiguration(CustomeUserDetailsService userDetailService) {
//		super();
//		this.userDetailsService = userDetailService;
//	}
//	
//	@Bean
//	public AuthenticationManager authenticationManager() {
//		var authenticationProvider = new DaoAuthenticationProvider();
//        authenticationProvider.setUserDetailsService(userDetailsService);
//        return new ProviderManager(authenticationProvider);
//	}
//	
//	@Bean
//	public PasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
//	
//	@Bean
//	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//		http.authorizeHttpRequests(
//				auth -> auth
//				.requestMatchers("/register").permitAll()
//				.anyRequest().authenticated());		
//		http.formLogin(withDefaults());
//		
//		http.csrf().disable();
//		http.headers().frameOptions().disable();
//		
//		return http.build();
//		
//	}
//}
