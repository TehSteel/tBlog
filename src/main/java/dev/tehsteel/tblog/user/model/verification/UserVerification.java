package dev.tehsteel.tblog.user.model.verification;

import lombok.Data;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Data
public final class UserVerification {
	private final long userId;
	private final UUID code = UUID.randomUUID();
	private final UserActionType userActionType;
	private final Date codeExpiration = new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));


	public boolean isCodeExpired() {
		final Date currentDate = new Date();
		return currentDate.after(codeExpiration);
	}
}
