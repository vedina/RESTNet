package net.idea.restnet.aa.cookie;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Status;
import org.restlet.security.ChallengeAuthenticator;

public class MultiChallengeAuthenticator extends ChallengeAuthenticator {

	public MultiChallengeAuthenticator(Context context, ChallengeScheme challengeScheme, String realm) {
		super(context, challengeScheme, realm);
	}
	
	public MultiChallengeAuthenticator(Context context, boolean optional, ChallengeScheme challengeScheme, String realm) {
		super(context, optional, challengeScheme, realm);
	}

	private volatile boolean multiAuthenticating= false;
	public boolean isMultiAuthenticating() {
		return multiAuthenticating;
	}

	public void setMultiAuthenticating(boolean multiAuthenticating) {
		this.multiAuthenticating = multiAuthenticating;
	}
	


    @Override
    protected int beforeHandle(Request request, Response response) {
        if (isMultiAuthenticating()
                || !request.getClientInfo().isAuthenticated()) {
            if (authenticate(request, response)) {
                return authenticated(request, response);
            } else if (isOptional()) {
                response.setStatus(Status.SUCCESS_OK);
                return CONTINUE;
            } else {
                return unauthenticated(request, response);
            }
        } else {
            return CONTINUE;
        }
    }

    @Override
    protected int doHandle(Request request, Response response) {
        final int result = CONTINUE;

        if (getNext() != null) {
            getNext().handle(request, response);

            // Re-associate the response to the current thread
            Response.setCurrent(response);

            // Associate the context to the current thread
            if (getContext() != null) {
                Context.setCurrent(getContext());
            }
        } else {
            response.setStatus(Status.SERVER_ERROR_INTERNAL);
            getLogger()
                    .warning(
                            "The filter "
                                    + getName()
                                    + " was executed without a next Restlet attached to it.");
        }

        return result;
    }
}
