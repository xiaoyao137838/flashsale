package com.example.flashsaleproject.models;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FlashOrder {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Nonnull
	@Size(min = 1, max = 50)
	@Column(length = 50, unique = true, nullable = false)
	private String orderNo;
	
	@Nonnull
	@Column(nullable = false)
	private Integer status = 0;
	
	@Nonnull
	@JoinColumn(nullable = false)
	@ManyToOne(fetch=FetchType.LAZY)
	private FlashSale flashSale;
	
	@Nonnull
	@Column(nullable = false)
	private BigDecimal amount;
	
	@Nonnull
	@JoinColumn(nullable = false)
	@ManyToOne(fetch=FetchType.LAZY)
	private FlashUser user;
	
	private Date createdAt;
	
	private Date paiedAt;

	@Override
	public String toString() {
		return "FlashOrder [orderNo=" + orderNo + ", status=" + status + ", flashSale=" + flashSale.getName() + ", amount="
				+ amount + ", user=" + user.getUsername() + ", createdAt=" + createdAt + ", paiedAt=" + paiedAt + "]";
	}

}
