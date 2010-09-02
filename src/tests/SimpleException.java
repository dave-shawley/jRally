package tests;

public class SimpleException extends Exception {
	public enum Flags { THROW_ERROR; }
	public SimpleException(String msg) {
		super(msg);
		if (msg.equals(Flags.THROW_ERROR.toString())) {
			throw new Error(msg);
		}
	}
}
