package net.idea.restnet.c.task;

import org.restlet.data.Form;

public class TaskCreatorForm<USERID, T> extends TaskCreator<USERID, T, Form> {

    /**
	 * 
	 */
    private static final long serialVersionUID = -1341551608684088669L;

    public TaskCreatorForm(Form input, boolean async) throws Exception {
	super(input, async);
    }

}
