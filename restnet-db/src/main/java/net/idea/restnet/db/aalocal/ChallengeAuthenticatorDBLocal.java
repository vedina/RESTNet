package net.idea.restnet.db.aalocal;

import org.restlet.Context;
import org.restlet.data.ChallengeScheme;
import org.restlet.security.ChallengeAuthenticator;

import net.idea.restnet.db.aalocal.user.UserAuth;

public class ChallengeAuthenticatorDBLocal extends ChallengeAuthenticator {

	/**
	 * HTTP basic
	 * 
	 * @param context
	 * @param optional
	 * @param realm
	 */
	public ChallengeAuthenticatorDBLocal(Context context, boolean optional, String realm, String configFile) {
		this(context, optional, ChallengeScheme.HTTP_BASIC, realm, configFile);
	}

	public ChallengeAuthenticatorDBLocal(Context context, boolean optional, ChallengeScheme challengeScheme,
			String realm) {
		this(context, optional, challengeScheme, realm, null);
	}

	/**
	 * 
	 * @param context
	 * @param optional
	 * @param challengeScheme
	 * @param realm
	 */
	public ChallengeAuthenticatorDBLocal(Context context, boolean optional, ChallengeScheme challengeScheme,
			String realm, String configFile) {
		super(context, optional, challengeScheme, realm);
		setVerifier(new DBVerifier<UserAuth>(context, realm, configFile,new UserAuth()));
		setEnroler(new DbEnroller(context, realm, configFile));
	}

}
