package com.rajames.forth.pi.helpers;

public class InstanceAlreadyRunningException extends RuntimeException {
	public InstanceAlreadyRunningException(String message) {
		super(message);
	}

	public InstanceAlreadyRunningException(String message, Throwable cause) {
		super(message, cause);
	}
}
