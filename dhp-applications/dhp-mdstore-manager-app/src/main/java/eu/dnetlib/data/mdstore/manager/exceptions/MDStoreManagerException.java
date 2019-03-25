package eu.dnetlib.data.mdstore.manager.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class MDStoreManagerException extends Exception{

	/**
	 *
	 */
	private static final long serialVersionUID = -7503316126409002675L;

	public MDStoreManagerException() {
		super();
	}

	public MDStoreManagerException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MDStoreManagerException(String message, Throwable cause) {
		super(message, cause);
	}

	public MDStoreManagerException(String message) {
		super(message);
	}

	public MDStoreManagerException(Throwable cause) {
		super(cause);
	}
}
