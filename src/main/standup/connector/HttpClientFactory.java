package standup.connector;

import java.net.URI;

import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;


/**
 * Simple HTTP client factory.
 * 
 * This factory exists solely to create {@link AbstractHttpClient} instances.
 * It was originally created to facilitate unit testing (see
 * {@link StubClientFactory}), but has since earned its place as a useful
 * abstraction.
 */
public interface HttpClientFactory {

	/**
	 * Retrieve an HTTP request object.
	 * 
	 * @param httpMethod
	 * @param uri
	 */
	public abstract HttpUriRequest getRequestObject(String httpMethod, URI uri);

	/**
	 * Retrieve a default client.
	 * @return An HTTP client using the default parameters for this factory.
	 */
	public abstract AbstractHttpClient getHttpClient();

	/**
	 * Retrieve a client using a specific credentials provider.
	 * @param credentials provides credentials as needed
	 * @return An HTTP client using the default parameters for this factory
	 *         and the specified credential store.
	 */
	public abstract AbstractHttpClient getHttpClient(CredentialsProvider credentials);

}