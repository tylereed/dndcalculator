package dndcalculator.exceptions;

public class RollParseException extends RuntimeException {

	private static final long serialVersionUID = 279118282836422662L;

	public RollParseException(String message) {
		super(message);
	}

	public RollParseException(String message, Throwable cause) {
		super(message, cause);
	}

}
