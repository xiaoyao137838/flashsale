package com.example.flashsaleproject.utils;


import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderVM {
	
	private Long id;
	
	@NotNull
	@Size(min = 1, max = 50)
	private String orderNo;
	
	@NotNull
	private Integer status = 0;
	
	@NotNull
	private Long flashSaleId;
	
	@NotNull
	private BigDecimal amount;
	
	@NotNull
	private Long userId;
	
	private long createdAt;
	
	private long paiedAt;
}
