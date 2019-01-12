package net.idea.restnet.db.aalocal;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.User;

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
		setVerifier(new DBVerifier<TokenAuth>(context, configFile, realm, new TokenAuth()) {
			@Override
		    public int verify(Request request, Response response) {
		        int result = RESULT_VALID;

		        if (request.getChallengeResponse() == null) {
		            result = RESULT_MISSING;
		        } else {
		            String identifier = getIdentifier(request, response);
		            char[] secret = getSecret(request, response);

		            if (verify(identifier, secret)) {
		                request.getClientInfo().setUser(new User(query.getFieldname()));
		            } else {
		                result = RESULT_INVALID;
		            }
		        }

		        return result;
		    }		
			//this should  be hidden in a Helper class
			@Override
			protected char[] getSecret(Request request, Response response) {
				ChallengeResponse cr = request.getChallengeResponse();
				cr.setSecret(cr.getRawValue().toCharArray());
				return cr.getSecret();
			}
			@Override
			protected String getIdentifier(Request request, Response response) {
				//with plain random token we don't know this before the db lookup
				return null;
			}
			@Override
			protected boolean processQueryResult(String identifier) {
				//lookup was done, have to pass it back to the verifier
				query.setFieldname(identifier);
				return super.processQueryResult(identifier);
			}
	
		});
		setEnroler(new DbEnroller(context, configFile, realm));
		setRechallenging(false);
	}

}
