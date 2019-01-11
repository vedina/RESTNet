package net.idea.restnet.db.aalocal;

import org.restlet.Context;
import org.restlet.security.ChallengeAuthenticator;

import net.idea.restnet.db.aalocal.user.TokenAuth;

public class ChallengeAuthenticatorTokenLocal extends ChallengeAuthenticator {

	/**
	 * 
	 * @param context
	 * @param optional
	 * @param challengeScheme
	 * @param realm
	 */
	public ChallengeAuthenticatorTokenLocal(Context context, boolean optional, String realm, String configFile) {
		super(context, optional, TokenAuth.APIKEY, realm);
		setVerifier(new DBVerifier<TokenAuth>(context, realm, configFile, new TokenAuth()));
		setEnroler(new DbEnroller(context, realm, configFile));
	}

}
