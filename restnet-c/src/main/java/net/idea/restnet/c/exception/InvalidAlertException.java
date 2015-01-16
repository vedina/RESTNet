package net.idea.restnet.c.exception;

import net.idea.modbcum.i.exceptions.AmbitException;

public class InvalidAlertException extends AmbitException {

    /**
	 * 
	 */
    private static final long serialVersionUID = 9054812229642318416L;

    public InvalidAlertException() {
	super("No alert id!");
    }
}
