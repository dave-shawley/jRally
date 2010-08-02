/**
 * 
 */
package standup.connector;

/**
 * TODO Describe UnexpectedResponseException.
 */
public class UnexpectedResponseException extends ConnectorException {

	private static final long serialVersionUID = 1L;

	public UnexpectedResponseException(String message) {
		super(message);
	}

	public UnexpectedResponseException(String message, Throwable cause) {
		super(message, cause);
	}

}
