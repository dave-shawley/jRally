package standup.connector;

import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Implements a vanilla HTTP client factory.
 * 
 * This class provides the most basic factory implementation.  I had plans on
 * creating peer classes to implement pooled connections but have yet to need
 * such a factory. 
 */
public class DefaultHttpClientFactory implements HttpClientFactory {

	/* (non-Javadoc)
	 * @see standup.connector.HttpClientFactory#getHttpClient()
	 */
	@Override
	public AbstractHttpClient getHttpClient() {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		return httpClient;
	}

	/* (non-Javadoc)
	 * @see standup.connector.HttpClientFactory#getHttpClient(org.apache.http.client.CredentialsProvider)
	 */
	@Override
	public AbstractHttpClient getHttpClient(CredentialsProvider credentials) {
		AbstractHttpClient httpClient = getHttpClient();
		httpClient.setCredentialsProvider(credentials);
		return httpClient;
	}

}
