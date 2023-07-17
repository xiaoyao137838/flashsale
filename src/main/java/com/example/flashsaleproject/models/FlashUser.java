package com.example.flashsaleproject.models;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.hibernate.annotations.BatchSize;

import com.example.flashsaleproject.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class FlashUser extends AbstractAuditingEntity implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@Size(min = 1, max = 50)
	@Pattern(regexp=Constants.LOGIN_REGEX)
	@Column(length = 50, unique = true, nullable = false)
	private String username;
	
	@Email
	@Size(min = 5, max = 250)
	@Column(length = 250, unique = true)
	private String email;
	
	@JsonIgnore
	@NotNull
	@Size(max=60)
	@Column(name="password_hash", length=60, nullable=false)
	private String password;
	
	@Size(max=50)
	@Column(length=50)
	private String firstName;
	
	@Size(max=50)
	@Column(length=50)
	private String lastName;
	
	@Size(max = 50)
	@Column(length=50)
	private String address;
	
	@Size(max = 20)
	@Column(length=20)
	private String phone;
	
	@JsonIgnore
	@ManyToMany
	@JoinTable(
			joinColumns={@JoinColumn(name="user_id", referencedColumnName="id")},
			inverseJoinColumns={@JoinColumn(name="authority_name", referencedColumnName="name")})
	@BatchSize(size=20)
	private Set<Authority> authorities = new HashSet<>();

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		FlashUser other = (FlashUser) obj;
		return !(getId() == null || other.getId() == null) && Objects.equals(id, other.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "FlashUser [username=" + username + ", email=" + email + ", firstName=" + firstName + ", lastName="
				+ lastName + ", address=" + address + ", phone=" + phone + "]";
	}

	
}
