/**
 * 
 */
package standup.connector;

import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * TODO Describe HttpClientFactory.
 */
public class DefaultHttpClientFactory implements HttpClientFactory {

	/* (non-Javadoc)
	 * @see standup.connector.HttpClientFactory#getHttpClient()
	 */
	public AbstractHttpClient getHttpClient() {
		// TODO figure out how to make this work
		//HttpParams params = new BasicHttpParams();
		//params.setParameter(ClientPNames.CONNECTION_MANAGER_FACTORY_CLASS_NAME,
		//		"org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager");
		DefaultHttpClient httpClient = new DefaultHttpClient();
		//httpClient.setParams(new DefaultedHttpParams(params, httpClient.getParams()));
		return httpClient;
	}

	/* (non-Javadoc)
	 * @see standup.connector.HttpClientFactory#getHttpClient(org.apache.http.client.CredentialsProvider)
	 */
	public AbstractHttpClient getHttpClient(CredentialsProvider credentials) {
		AbstractHttpClient httpClient = getHttpClient();
		httpClient.setCredentialsProvider(credentials);
		return httpClient;
	}

}
