package standup.connector.rally;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

import javax.xml.bind.UnmarshalException;

import org.apache.commons.io.IOUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Test;

import standup.connector.ServerConnection.IterationStatus;
import standup.connector.StubClientFactory;
import standup.connector.UnexpectedResponseException;
import standup.xml.StoryList;
import standup.xml.StoryType;


/**
 * Test the {@link ServerConnection} class.
 */
public class RallyServerConnectionTest {
	
	private static final String PROJECT_NAME = "Weather on Mobile";
	private static final String SERVER_NAME = "rally1.rallydev.com";
	private static final String ITERATION_URL = "https://server/slm/webservice/1.17/iteration/";
	private static final String[] ITERATION_NAMES = {
		"Make Sample Data Go from Server to Device", "Minimal Working Version",
		"Plug in the Real Weather"
	};
	private static final String ACTIVE_ITERATION_NAME = ITERATION_NAMES[2];
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
		factory.setNextResponse(200, getResourceAsString("test-data/iterations.xml"));
		conn.setUsername(USER_NAME);
		conn.setPassword(PASSWORD);
		List<IterationStatus> iterations = conn.listIterationsForProject(PROJECT_NAME);
		assertEquals(ITERATION_NAMES.length, iterations.size());
		int index = 0;
		for (IterationStatus iteration: iterations) {
			assertEquals(ITERATION_NAMES[index], iteration.iterationName);
			assertEquals(new URI(ITERATION_URL+Integer.toString(index+1)), iteration.iterationURI);
			index++;
		}
		assertEquals(0, factory.numberOfResponsesLeft());
	}

	@Test
	public void testRetrieveStoriesForIteration() throws Exception {
		factory.setNextResponse(200, getResourceAsString("test-data/iteration-3.xml"));
		factory.setNextResponse(200, getResourceAsString("test-data/story-10.xml"));
		factory.setNextResponse(200, getResourceAsString("test-data/story-11.xml"));
		factory.setNextResponse(200, getResourceAsString("test-data/story-12.xml"));
		factory.setNextResponse(200, getResourceAsString("test-data/story-13.xml"));
		factory.setNextResponse(200, getResourceAsString("test-data/empty-response.xml"));
		conn.setUsername(USER_NAME);
		conn.setPassword(PASSWORD);
		StoryList storyList = conn.retrieveStoriesForIteration(ACTIVE_ITERATION_NAME);
		List<StoryType> stories = storyList.getStory();
		assertEquals(4, stories.size());
		assertEquals(0, factory.numberOfResponsesLeft());
	}

	@Test
	public void testRetrieveStoriesByID() throws Exception {
		factory.setNextResponse(200, getResourceAsString("test-data/stories-by-id.xml"));
		factory.setNextResponse(200, getResourceAsString("test-data/story-11.xml"));
		factory.setNextResponse(200, getResourceAsString("test-data/story-12.xml"));
		conn.setUsername(USER_NAME);
		conn.setPassword(PASSWORD);
		StoryList storyList = conn.retrieveStories(new String[]{"US11", "US12"});
		List<StoryType> stories = storyList.getStory();
		assertEquals(2, stories.size());
		assertEquals(0, factory.numberOfResponsesLeft());
	}

	@Test
	public void testRetrieveStoriesByIdWithTypes() throws Exception {
		factory.setNextResponse(200, getResourceAsString("test-data/stories-by-id typed.xml"));
		conn.setUsername(USER_NAME);
		conn.setPassword(PASSWORD);
		StoryList storyList = conn.retrieveStories(new String[]{"US11", "US12"});
		List<StoryType> stories = storyList.getStory();
		assertEquals(2, stories.size());
		assertEquals(0, factory.numberOfResponsesLeft());
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
		conn.retrieveStoriesForIteration(ACTIVE_ITERATION_NAME);
	}

	@Test
	public void testRetrieveIterationWithInvalidURL() throws Exception {
		try {
			factory.setNextResponse(200, getResourceAsString("test-data/iteration-with-invalid-url.xml"));
			conn.listIterationsForProject(PROJECT_NAME);
		} catch (UnexpectedResponseException exc) {
			Throwable cause = exc.getCause();
			assertTrue(cause instanceof UnmarshalException);
		}
	}

	//
	// Utility functions
	//
	protected String getResourceAsString(String resourceName) throws IOException {
		return IOUtils.toString(ClassLoader.getSystemResourceAsStream(resourceName));
	}

}

