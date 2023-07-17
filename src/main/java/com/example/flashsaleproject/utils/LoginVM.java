package com.example.flashsaleproject.utils;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginVM {

	@NotNull
	@Size(min = 1, max = 50)
	@Pattern(regexp=Constants.LOGIN_REGEX)
	private String username;

	@NotNull
	@Size(max=60)
	private String password;
}
