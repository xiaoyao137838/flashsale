package com.example.flashsaleproject.models;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table
public class Authority implements Serializable {
	
	@NotNull
	@Size(max=50)
	@Id
	@Column(length=50)
	private String name;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		Authority other = (Authority) obj;
		return !(name == null || other.name == null) && Objects.equals(name, other.name);
	}

	@Override
	public String toString() {
		return "Authority [name=" + name + "]";
	}

}
