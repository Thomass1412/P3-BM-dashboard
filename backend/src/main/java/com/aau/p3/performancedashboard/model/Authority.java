package com.aau.p3.performancedashboard.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an authority in the system.
 * Implements the GrantedAuthority interface.
 */
@Document(collection = "authority")
public class Authority implements GrantedAuthority {

	private static final long serialVersionUID = 1L;

	@Id
	@Getter
	private String id;

	@NotNull
	@Size(max = 50)
	@Setter
	@Getter
	private String name;

	/**
	 * Checks if this authority is equal to the given object.
	 *
	 * @param o The object to compare with.
	 * @return True if the objects are equal, false otherwise.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Authority authority = (Authority) o;

		return !(name != null ? !name.equals(authority.name) : authority.name != null);
	}

	/**
	 * Returns the authority name.
	 *
	 * @return The authority name.
	 */
	@Override
	public String getAuthority() {
		return name;
	}
}