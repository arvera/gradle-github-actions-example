package ca.skipatrol.cnswap.storage;

public class StorageFileNotFoundException extends StorageException {

	private static final long serialVersionUID = 239011091095873453L;

	public StorageFileNotFoundException(String message) {
		super(message);
	}

	public StorageFileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}