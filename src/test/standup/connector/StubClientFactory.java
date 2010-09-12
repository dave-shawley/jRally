package standup.connector;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestExecutor;


class StubHttpRequestExecutor extends HttpRequestExecutor {

	private final HttpResponse cannedResponse;

	public StubHttpRequestExecutor(HttpResponse rsp) {
		this.cannedResponse = rsp;
	}

	/* (non-Javadoc)
	 * @see org.apache.http.protocol.HttpRequestExecutor#execute(org.apache.http.HttpRequest, org.apache.http.HttpClientConnection, org.apache.http.protocol.HttpContext)
	 */
	@Override
	public HttpResponse execute(HttpRequest request, HttpClientConnection conn, HttpContext context)
	{
		return this.cannedResponse;
	}

}


public class StubClientFactory implements HttpClientFactory {

	private final Queue<HttpResponse> responses = new LinkedList<HttpResponse>();

	private StatusLine buildStatusLine(int code) {
		return new BasicStatusLine(HttpVersion.HTTP_1_1, code, null);
	}

	public void setNextResponse(int code) {
		responses.add(new BasicHttpResponse(buildStatusLine(code)));
	}

	public void setNextResponse(int code, String data) {
		HttpResponse rsp = new BasicHttpResponse(buildStatusLine(code));
		try {
			rsp.setEntity(new StringEntity(data));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		responses.add(rsp);
	}

	public int numberOfResponsesLeft() {
		return responses.size();
	}

	@Override
	public AbstractHttpClient getHttpClient() {
		AbstractHttpClient client = new DefaultHttpClient() {
			@Override
			protected HttpRequestExecutor createRequestExecutor() {
				return new StubHttpRequestExecutor(responses.poll());
			}
		};
		return client;
	}

	@Override
	public AbstractHttpClient getHttpClient(CredentialsProvider credentials) {
		AbstractHttpClient client = getHttpClient();
		client.setCredentialsProvider(credentials);
		return client;
	}

	@Override
	public HttpUriRequest getRequestObject(String httpMethod, URI uri) {
		if (httpMethod.equalsIgnoreCase(HttpGet.METHOD_NAME)) {
			return new HttpGet(uri);
		} else if (httpMethod.equalsIgnoreCase(HttpPost.METHOD_NAME)) {
			return new HttpPost(uri);
		} else if (httpMethod.equalsIgnoreCase(HttpDelete.METHOD_NAME)) {
			return new HttpDelete(uri);
		}
		return null;
	}

}
