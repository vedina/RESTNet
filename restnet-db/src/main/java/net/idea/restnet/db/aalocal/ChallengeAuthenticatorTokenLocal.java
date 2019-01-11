package net.idea.restnet.db.aalocal;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.Form;
import org.restlet.security.ChallengeAuthenticator;

import net.idea.restnet.db.aalocal.user.TokenAuth;
import net.idea.restnet.db.aalocal.user.UserAuth;

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
		setVerifier(new DBVerifier<TokenAuth>(context,  configFile, realm, new TokenAuth()));
		setEnroler(new DbEnroller(context, realm, configFile));
	}

	@Override
	protected boolean authenticate(Request request, Response response) {

		ChallengeResponse cr = request.getChallengeResponse();
		cr.setSecret(cr.getRawValue().toCharArray());
		cr.setIdentifier(null);
		boolean ok = super.authenticate(request, response);
		return ok;
	}

}
