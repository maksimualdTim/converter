package exceptions;

public class CurrencyAlreadyExistsException extends RuntimeException{
    private static final long serialVersionUID = 1L;

	public CurrencyAlreadyExistsException(String message) {
        super(message);
    }
}
