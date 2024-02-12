package ca.skipatrol.cnswap.exception;

public class CNSWAPExceptionItemError extends RuntimeException {

	public CNSWAPExceptionItemError(String message) {
		super(message);
	}

	public CNSWAPExceptionItemError(String message, Throwable cause) {
		super(message, cause);
	}
}