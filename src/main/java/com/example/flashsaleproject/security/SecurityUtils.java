//package com.example.flashsaleproject.security;
//
//import java.util.Optional;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import com.example.flashsaleproject.jpa.UserRepository;
//import com.example.flashsaleproject.models.FlashUser;
//
//@Component
//public final class SecurityUtils {
//	
//	private static UserRepository userRepository;
//	
//	public SecurityUtils(UserRepository userRepository) {
//		SecurityUtils.userRepository = userRepository;
//	}
//	
//	public static Optional<String> getCurrentUsername() {
//		SecurityContext securityContext = SecurityContextHolder.getContext();
//		Authentication authentication = securityContext.getAuthentication();
//		Object principleObject = authentication.getPrincipal();
//		
//		return Optional.ofNullable(principleObject)
//				.map(principle -> {
//					if (principle instanceof UserDetails userDetails) {
//						return userDetails.getUsername();
//					} else if (principle instanceof String username) {
//						return username;
//					}
//					return null;
//				});
//		
//	}
//	
//	public static Optional<FlashUser> getCurrentUser() {
//		
//		Optional<String> usernameOptional = getCurrentUsername();
//
//		return userRepository.findOneWithAuthoritiesByUsername(usernameOptional.get());
//		
//	}
//
//}
