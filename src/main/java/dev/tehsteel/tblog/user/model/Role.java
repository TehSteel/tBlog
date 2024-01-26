package dev.tehsteel.tblog.user.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
	ADMIN("ROLE_ADMIN"),
	USER("ROLE_USER");

	private final String authority;

	public String getAsAuthority() {
		return authority;
	}
}
