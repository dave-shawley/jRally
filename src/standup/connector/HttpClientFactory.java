package standup.connector;

import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.AbstractHttpClient;

public interface HttpClientFactory {
	public abstract AbstractHttpClient getHttpClient();
	public abstract AbstractHttpClient getHttpClient(CredentialsProvider credentials);
}