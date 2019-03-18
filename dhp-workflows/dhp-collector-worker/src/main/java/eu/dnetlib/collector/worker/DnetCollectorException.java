package eu.dnetlib.collector.worker;

public class DnetCollectorException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = -290723075076039757L;

	public DnetCollectorException() {
		super();
	}

	public DnetCollectorException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DnetCollectorException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public DnetCollectorException(final String message) {
		super(message);
	}

	public DnetCollectorException(final Throwable cause) {
		super(cause);
	}

}
