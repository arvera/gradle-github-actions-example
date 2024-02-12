package ca.skipatrol.cnswap.exception;

public class CNSWAPException extends RuntimeException {

	public CNSWAPException(String message) {
		super(message);
	}

	public CNSWAPException(String message, Throwable cause) {
		super(message, cause);
	}
}