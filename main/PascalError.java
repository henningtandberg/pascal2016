package main;

public class PascalError extends RuntimeException {
    // Since RuntimeException extends Exception and
    // Exception implements Serializable, it should define this constant
    // (but the actual value can be anything):
    private static final long serialVersionUID = 20160426L;

    PascalError(String message) {
    	super(message);
    }
}
