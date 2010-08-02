package tests;

import java.io.IOException;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestExecutor;

import standup.connector.HttpClientFactory;


class StubHttpRequestExecutor extends HttpRequestExecutor {

	private final StatusLine statusLine;
	private final String content;

	public StubHttpRequestExecutor(int code, String content) {
		this.statusLine = new BasicStatusLine(HttpVersion.HTTP_1_1, code, null);
		this.content = content;
	}

	/* (non-Javadoc)
	 * @see org.apache.http.protocol.HttpRequestExecutor#execute(org.apache.http.HttpRequest, org.apache.http.HttpClientConnection, org.apache.http.protocol.HttpContext)
	 */
	@Override
	public HttpResponse execute(HttpRequest request, HttpClientConnection conn, HttpContext context)
		throws IOException, HttpException
	{
		HttpResponse response = new BasicHttpResponse(this.statusLine);
		response.setEntity(new StringEntity(this.content));
		return response;
	}

}


public class StubClientFactory implements HttpClientFactory {

	private int nextResponseCode;
	private String nextResponseData;

	public void setNextResponse(int code) {
		this.nextResponseCode = code;
		this.nextResponseData = "";
	}

	public void setNextResponse(int code, String data) {
		this.nextResponseCode = code;
		this.nextResponseData = data;
	}

	public AbstractHttpClient getHttpClient() {
		AbstractHttpClient client = new DefaultHttpClient() {
			@Override
			protected HttpRequestExecutor createRequestExecutor() {
				return new StubHttpRequestExecutor(nextResponseCode, nextResponseData);
			}
		};
		return client;
	}

	public AbstractHttpClient getHttpClient(CredentialsProvider credentials) {
		AbstractHttpClient client = getHttpClient();
		client.setCredentialsProvider(credentials);
		return client;
	}

}
