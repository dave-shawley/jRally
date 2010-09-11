package standup.connector;

/**
 * An unexpected response was received.
 * 
 * This exception is thrown when a particular response type is expected
 * and the backend service returns something else.  This may not indicate
 * an error or failure within the backend since an incompatibility in the
 * connector implementation can easily result in this type of a failure.
 */
public class UnexpectedResponseException extends ConnectorException {

	private static final long serialVersionUID = 1L;

	/**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message a detailed message. The detail message is saved for 
     *                later retrieval by the {@link #getMessage()} method.
	 */
	public UnexpectedResponseException(String message) {
		super(message);
	}

	/**
     * Constructs a new exception with the specified detail message and
     * cause.
     * 
     * Note that the detail message associated with <code>cause</code> is
     * <i>not</i> automatically incorporated in this exception's detail
     * message.
     *
     * @param message a detailed message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the underlying cause (which is saved for later retrieval
     *                by the {@link #getCause()} method).  (A <code>null</code>
     *                value is permitted, and indicates that the cause is
     *                nonexistent or unknown.)
	 */
	public UnexpectedResponseException(String message, Throwable cause) {
		super(message, cause);
	}

}
