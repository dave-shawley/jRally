package tests;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Test;

import standup.connector.UnexpectedResponseException;
import standup.connector.ServerConnection.IterationStatus;
import standup.connector.rally.ServerConnection;


/**
 * Test the {@link ServerConnection} class.
 */
public class RallyServerConnectionTest {
	
	private static final String PROJECT_NAME = "A Project";
	private static final String SERVER_NAME = "rally1.rallydev.com";
	private static final String ITERATION_URL = "https://server/slm/webservice/1.17/iteration/42";
	private static final String ITERATION_NAME = "An Iteration";
	private static final String PASSWORD = "<P@s5w0rD";
	private static final String USER_NAME = "dave.shawley@schange.com";
	private ServerConnection conn;
	private StubClientFactory factory;

	@Before
	public void createConnectionObject() {
		factory = new StubClientFactory();
		conn = new ServerConnection(SERVER_NAME, factory);
	}

	@Test
	public void testCredentials() {
		Credentials expected = new UsernamePasswordCredentials("", "");
		Credentials creds = conn.getCredentials(null);
		assertEquals(expected, creds);

		expected = new UsernamePasswordCredentials(USER_NAME, PASSWORD);
		conn.setUsername(USER_NAME);
		conn.setPassword(PASSWORD);
		creds = conn.getCredentials(null);
		assertEquals(expected, creds);

		AuthScope scope = new AuthScope("localhost", 88);
		conn.setCredentials(scope, expected);
		creds = conn.getCredentials(scope);
		assertEquals(expected, creds);
		
		// The implementation ignores AuthScope, so we should get the
		// same result with a different scope.
		creds = conn.getCredentials(AuthScope.ANY);
		assertEquals(expected, creds);

		conn.clear();
		expected = new UsernamePasswordCredentials("", "");
		creds = conn.getCredentials(null);
		assertEquals(expected, creds);
	}

	@Test
	public void testRetreiveIterations() throws Exception {
		Integer expectedResultCount = 2;
		factory.setNextResponse(200,
				"<?xml version='1.0'?>" +
				"<QueryResult rallyAPIMajor=\"1\" rallyAPIMinor=\"17\">" +
				"<Errors/><Warnings/><TotalResultCount>"+expectedResultCount.toString()+"</TotalResultCount>" +
				"<StartIndex>1</StartIndex><PageSize>20</PageSize><Results>" +
				"<Object ref=\""+ITERATION_URL+"\" " +
					"rallyAPIMajor='1' rallyAPIMinor='17' type='Iteration' " +
					"refObjectName=\""+ITERATION_NAME+"\"/>" +
				"<Object ref=\"an invalid URL\" " +
					"rallyAPIMajor='1' rallyAPIMinor='17' type='Iteration' " +
					"refObjectName=\""+ITERATION_NAME+"\"/>" +
				"</Results></QueryResult>");
		conn.setUsername(USER_NAME);
		conn.setPassword(PASSWORD);
		List<IterationStatus> iterations = conn.listIterationsForProject(PROJECT_NAME);
		assertEquals(expectedResultCount.intValue(), iterations.size());
		assertEquals(ITERATION_NAME, iterations.get(0).iterationName);
		assertEquals(new URI(ITERATION_URL), iterations.get(0).iterationURI);
		assertEquals(ITERATION_NAME, iterations.get(1).iterationName);
		assertEquals(null, iterations.get(1).iterationURI);
	}

	@Test(expected=ClientProtocolException.class)
	public void useInvalidCredentials() throws Exception {
		factory.setNextResponse(401);
		conn.setUsername("INVALID");
		conn.setPassword("");
		conn.listIterationsForProject(PROJECT_NAME);
	}

	@Test(expected=UnexpectedResponseException.class)
	public void stubUnexpectedObject() throws Exception {
		factory.setNextResponse(200,
				"<?xml version='1.0'?>" +
				"<Task objectVersion='1' rallyAPIMajor='1' rallyAPIMinor='17' " +
					"refObjectName='A Task' ref='http://here/'>" +
				"</Task>");
		conn.setUsername("INVALID");
		conn.setPassword("");
		conn.listIterationsForProject(PROJECT_NAME);
	}

	@Test(expected=MalformedURLException.class)
	public void useInvalidHostname() throws Exception {
		conn = new ServerConnection("H0pefuly an INVALID hostn@Me", factory);
		conn.listIterationsForProject(PROJECT_NAME);
	}

	@Test(expected=UnexpectedResponseException.class)
	public void stubNonXmlResponse() throws Exception {
		factory.setNextResponse(200, "EHLO smtp.verizon.net\r\n");
		conn.listIterationsForProject(PROJECT_NAME);
	}

	@Test(expected=UnexpectedResponseException.class)
	public void stubUnexpectedXml() throws Exception {
		factory.setNextResponse(200, "<?xml version='1.0'?><some-document/>");
		conn.listIterationsForProject(PROJECT_NAME);
	}
}

