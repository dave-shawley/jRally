package standup.utility;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;

public class TransformErrorListener implements ErrorListener {

	private final Logger logger;

	public TransformErrorListener(Logger logger) {
		this.logger = logger;
	}

	public void error(TransformerException exception) throws TransformerException {
		this.logger.error(exception.getClass().getCanonicalName(), exception);
		throw exception;
	}

	public void fatalError(TransformerException exception) throws TransformerException {
		this.logger.fatal(exception.getClass().getCanonicalName(), exception);
		throw exception;
	}

	public void warning(TransformerException exception) throws TransformerException {
		this.logger.warn(exception.getClass().getCanonicalName(), exception);
		throw exception;
	}

}
