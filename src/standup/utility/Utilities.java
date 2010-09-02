package standup.utility;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.utils.URIUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

public class Utilities {

	/**
	 * Generate an exception with a message.
	 * 
	 * This helper simply generates an exception from a series of strings.  The
	 * variable-length argument list is a list of objects.  The message is formed
	 * by starting with <code>msg</code> and appending a colon and the result of
	 * each object converted to a string (using {@link Object#toString()}).  
	 *  
	 * @param <T> the type of exception to generate.
	 * @param exceptionClass the class used to construct the exception instance.
	 * @param msg the basic message.
	 * @param objects zero or more objects that are appended to the message.
	 * @return An instance of the exception ready to be thrown.
	 */
	static public <T extends Throwable> T generateException(Class<T> exceptionClass,
			String msg, Object... objects)
	{
		T exc = null;
		StringBuilder message = new StringBuilder(msg);
		if (objects.length > 0) {
			message.append(": ");
			message.append(Utilities.join(" ", objects));
		}
		try {
			Constructor<T> cons;
			cons = exceptionClass.getConstructor(String.class);
			exc = cons.newInstance(message.toString());
		} catch (Throwable t) {
			// Yuck... this really shouldn't happen unless you, the person
			// reading this, has done something horribly wrong...
			throw new Error(String.format(
				"exception class %s constructor threw an exception",
				exceptionClass.getCanonicalName()), t);
		}
		return exc;
	}

	/**
	 * Generate an exception with a message.
	 * 
	 * This helper simply generates an exception from a series of strings.  The
	 * variable-length argument list is a list of objects.  The message is formed
	 * by starting with <code>msg</code> and appending a colon and the result of
	 * each object converted to a string (using {@link Object#toString()}).  
	 *  
	 * @param <T> the type of exception to generate.
	 * @param exceptionClass the class used to construct the exception instance.
	 * @param cause the Throwable that caused this exception. 
	 * @param msg the basic message.
	 * @param objects zero or more objects that are appended to the message.
	 * @return An instance of the exception ready to be thrown.
	 */
	static public <T extends Throwable> T generateException(Class<T> exceptionClass,
			Throwable cause, String msg, Object... objects)
	{
		T exc = Utilities.generateException(exceptionClass, msg, objects);
		exc.initCause(cause);
		return exc;
	}

	/**
	 * Join an arbitrary number of objects with a seperator string.
	 * 
	 * Given all of the options offered by {@link StringUtils}, I was surprised
	 * to see that it lacked this simple method.
	 * 
	 * @param separator the separator used as glue between the objects.
	 * @param objects a variable-length list of objects to join.
	 * @return a string containing each object in order joined using the separator.
	 * @see StringUtils#join(Object[], String)
	 */
	static public String join(String separator, Object... objects) {
		return StringUtils.join(objects, separator);
	}

	/**
	 * Helpful wrapper around {@link URIUtils#createURI(String, String, int, String, String, String)}.
	 * 
	 * @param host this is used for the scheme, host name, and port.
	 * @param path the resource path to append.
	 * @param query the query to append.
	 * @return a URI constructed from the parameters.
	 * @throws URISyntaxException
	 * @see URIUtils#createURI(String, String, int, String, String, String)
	 */
	static public URI createURI(HttpHost host, String path, String query) throws URISyntaxException {
		return URIUtils.createURI(host.getSchemeName(), host.getHostName(), host.getPort(), path, query, null);
	}

	static public <T extends Result> T runXSLT(T resultDoc, String xsltFilename,
			Logger logger, JAXBContext jaxb, JAXBSource sourceDocument,
			TransformerFactory xformFactory)
		throws JAXBException, TransformerException
	{
		NDC.push("processing "+xsltFilename);
		try {
			InputStream xsltFile = ClassLoader.getSystemResourceAsStream(xsltFilename);
			if (xsltFile == null) {
				throw new TransformerException("getSystemResourceAsStream("+xsltFilename+")");
			}
			Transformer t = xformFactory.newTransformer(new StreamSource(xsltFile));
			t.setErrorListener(new TransformErrorListener(logger));
			t.transform(sourceDocument, resultDoc);
			return resultDoc;
		} finally {
			NDC.pop();
		}
	}

}
