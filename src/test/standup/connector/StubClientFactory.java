package standup.connector;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.RequestDirector;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;


class StubHttpRequestDirector implements RequestDirector {

	private final HttpResponse cannedResponse;

	public StubHttpRequestDirector(HttpResponse rsp) {
		this.cannedResponse = rsp;
	}

	@Override
	public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context)
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
			protected RequestDirector createClientRequestDirector(
					HttpRequestExecutor requestExec,
					ClientConnectionManager conman,
					ConnectionReuseStrategy reustrat,
					ConnectionKeepAliveStrategy kastrat,
					HttpRoutePlanner rouplan, HttpProcessor httpProcessor,
					HttpRequestRetryHandler retryHandler,
					RedirectHandler redirectHandler,
					AuthenticationHandler targetAuthHandler,
					AuthenticationHandler proxyAuthHandler,
					UserTokenHandler stateHandler, HttpParams params)
			{
				return new StubHttpRequestDirector(responses.poll());
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
