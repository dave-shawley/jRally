package standup.connector;

import java.lang.reflect.Constructor;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

/**
 * Implements a vanilla HTTP client factory.
 * 
 * This class provides the most basic factory implementation.  I had plans on
 * creating peer classes to implement pooled connections but have yet to need
 * such a factory. 
 */
public class DefaultHttpClientFactory implements HttpClientFactory {
	private static final Logger logger = Logger.getLogger(DefaultHttpClientFactory.class);
	private final Map<String, Class<? extends HttpUriRequest>> methodMap;

	public DefaultHttpClientFactory() {
		this.methodMap = getMethodMap();
	}

	/**
	 * Override this method to provide a custom map from method name to
	 * implementation class.
	 * <p> 
	 * This is called during construction to create the map that is used
	 * by {@link #getRequestObject(String)} to instantiate the appropriate
	 * object for a given HTTP method.  In most cases, the defaults are
	 * appropriate - they include the standard HTTP message types (sans
	 * <code>PATCH</code>) and map to the classes defined in the
	 * {@link org.apache.http.client.methods} package.
	 * <p>
	 * Subclasses should call this method and modify the resulting map
	 * object to meet whatever requirements you have.  Feel free to 
	 * append new methods or replace the implementation classes in the
	 * default map.
	 * 
	 * @return a map from HTTP method name to implementation class
	 */
	protected Map<String,Class<? extends HttpUriRequest>> getMethodMap() {
		Map<String,Class<? extends HttpUriRequest>> m = 
			new HashMap<String,Class<? extends HttpUriRequest>>();
		m.put(HttpDelete.METHOD_NAME, HttpDelete.class);
		m.put(HttpGet.METHOD_NAME, HttpGet.class);
		m.put(HttpHead.METHOD_NAME, HttpHead.class);
		m.put(HttpOptions.METHOD_NAME, HttpOptions.class);
		m.put(HttpPut.METHOD_NAME, HttpPut.class);
		m.put(HttpTrace.METHOD_NAME, HttpTrace.class);
		return m;
	}

	/**
	 * Override this method to provide custom configuration per request.
	 * <p>
	 * This method is executed after a request is created by before it is
	 * returned to the client.  This is the perfect extension point if you
	 * need to set custom HTTP headers or otherwise modify the outgoing request
	 * before the client has access to it.
	 * <p>
	 * The default implementation does nothing so there is no harm in calling
	 * it from derived classes.
	 * 
	 * @param request the newly minted request object
	 */
	protected void configureRequest(HttpUriRequest request) {	
	}

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

	/* (non-Javadoc)
	 * @see standup.connector.HttpClientFactory#getRequestObject(java.lang.String, java.net.URI)
	 */
	@Override
	public HttpUriRequest getRequestObject(String httpMethod, URI uri) {
		HttpUriRequest req = null;
		if (methodMap.containsKey(httpMethod)) {
			try {
				Constructor<? extends HttpUriRequest> cons = 
					methodMap.get(httpMethod).getConstructor(URI.class);
				req = cons.newInstance(uri);
				configureRequest(req);
			} catch (Exception e) {
				logger.error("failed to create new instance for method "
						+ httpMethod, e);
			}
		} else {
			logger.warn("failed to locate message factory for method "
					+ httpMethod);
		}
		return req;
	}

}
