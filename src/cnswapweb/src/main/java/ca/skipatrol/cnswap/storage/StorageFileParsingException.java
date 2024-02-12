package ca.skipatrol.cnswap.storage;

public class StorageFileParsingException extends StorageException {

	private static final long serialVersionUID = 239011091095873453L;

	public StorageFileParsingException(String message) {
		super(message);
	}

	public StorageFileParsingException(String message, Throwable cause) {
		super(message, cause);
	}
}