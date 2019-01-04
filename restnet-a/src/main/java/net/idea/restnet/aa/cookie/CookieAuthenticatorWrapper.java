package net.idea.restnet.aa.cookie;

import org.restlet.Context;

public class CookieAuthenticatorWrapper extends CookieAuthenticator {

	public CookieAuthenticatorWrapper(Context context, boolean optional, String realm, byte[] encryptSecretKey) {
		super(context, optional, realm, encryptSecretKey);

	}

	public CookieAuthenticatorWrapper(Context context, String realm, byte[] encryptSecretKey) {
		super(context, realm, encryptSecretKey);

	}
/*
	@Override
	public String formatCredentials(ChallengeResponse challenge) throws GeneralSecurityException {
		if (challenge == null)
			return null;
		else
			return super.formatCredentials(challenge);
	}
*/
}
