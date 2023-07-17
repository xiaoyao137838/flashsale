//package com.example.flashsaleproject.security;
//
//import java.util.List;
//import java.util.Locale;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.example.flashsaleproject.jpa.UserRepository;
//import com.example.flashsaleproject.models.FlashUser;
//
//@Component("userDetailsService")
//public class CustomeUserDetailsService implements UserDetailsService {
//	
//	private final Logger logger = LoggerFactory.getLogger(getClass());
//	
//	private final UserRepository userRepository;
//
//	public CustomeUserDetailsService(UserRepository userRepository) {
//		super();
//		this.userRepository = userRepository;
//	}
//
//	@Override
//	@Transactional
//	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
//		logger.debug("Authenticating {}", username);
//		
//		if (new EmailValidator().isValid(username, null)) {
//			Optional<FlashUser> userByEmailFromDatabase = userRepository.findOneWithAuthoritiesByEmail(username);
//			
//			return userByEmailFromDatabase.map(user -> createSpringSecurityUser(username, user))
//					.get();
//		}
//		
//		String lowerUsername = username.toLowerCase(Locale.ENGLISH);
//		Optional<FlashUser> userByUsernameFromDatabase = userRepository.findOneWithAuthoritiesByUsername(lowerUsername);
//		
//		return userByUsernameFromDatabase.map(user -> createSpringSecurityUser(lowerUsername, user))
//				.get();
//	}
//
//	private org.springframework.security.core.userdetails.User createSpringSecurityUser(String username, FlashUser user) {
//		List<GrantedAuthority> authorities = user.getAuthorities().stream()
//				.map(auth -> new SimpleGrantedAuthority(auth.getName()))
//				.collect(Collectors.toList());
//		
//		return new org.springframework.security.core.userdetails.User(user.getUsername(),
//				user.getPassword(),
//				authorities);
//	}
//	
//
//}
