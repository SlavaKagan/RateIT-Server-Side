package playground.logic;

public class ConfirmationException extends Exception implements NotFoundExceptions {
	private static final long serialVersionUID = 734874520333059751L;

	public ConfirmationException() {
		super();
	}

	public ConfirmationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ConfirmationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfirmationException(String message) {
		super(message);
	}

	public ConfirmationException(Throwable cause) {
		super(cause);
	}
}