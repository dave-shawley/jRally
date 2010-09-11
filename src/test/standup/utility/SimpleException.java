package standup.utility;

class SimpleException extends Exception {
	private static final long serialVersionUID = 594341759526899330L;
	public enum Flags { THROW_ERROR; }
	public SimpleException(String msg) {
		super(msg);
		if (msg.equals(Flags.THROW_ERROR.toString())) {
			throw new Error(msg);
		}
	}
}
