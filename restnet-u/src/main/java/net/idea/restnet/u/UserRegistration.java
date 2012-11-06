package net.idea.restnet.u;

import java.io.Serializable;

public class UserRegistration implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2291105142249204432L;
	long timestamp_created;
	long timestamp_confirmed;
	String confirmationCode;
	RegistrationStatus status;
	
	public UserRegistration(String code) {
		super();
		this.confirmationCode = code;
	}
	
	public UserRegistration() {
		this(null);
	}
	public long getTimestamp_created() {
		return timestamp_created;
	}
	public void setTimestamp_created(long timestamp_created) {
		this.timestamp_created = timestamp_created;
	}
	public long getTimestamp_confirmed() {
		return timestamp_confirmed;
	}
	public void setTimestamp_confirmed(long timestamp_confirmed) {
		this.timestamp_confirmed = timestamp_confirmed;
	}
	public String getConfirmationCode() {
		return confirmationCode;
	}
	public void setConfirmationCode(String confirmationCode) {
		this.confirmationCode = confirmationCode;
	}
	public RegistrationStatus getStatus() {
		return status;
	}
	public void setStatus(RegistrationStatus status) {
		this.status = status;
	}
}
