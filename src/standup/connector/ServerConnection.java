package standup.connector;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.http.client.ClientProtocolException;

import standup.xml.StoryList;
import standup.xml.TaskList;

/**
 * A connection to an Agile Project Management tool.
 * Implementations of this interface expose different Agile tools
 * to the application.  It serves as the connection wrapper for
 * the client-server connection.  It also acts as the data connector
 * for object instances specific to the backend tool.
 */
public interface ServerConnection {

	/**
	 * A simple POD type that is used to describe the list
	 * of iterations owned by the server.
	 */
	public class IterationStatus {
		public String iterationName;
		public URI iterationURI;
	}

 	/**
 	 * Retrieves the list of iterations for a specific project.
 	 * @param projectName the project to retrieve the information for.
 	 * @return The list of known iterations.
 	 */
 	public List<IterationStatus> listIterationsForProject(String projectName)
 		throws IOException, JAXBException, ClientProtocolException, ConnectorException;
 	
 	public StoryList retrieveStoriesForIteration(String iterationName)
 		throws IOException, ClientProtocolException, ConnectorException;

 	public StoryList retrieveStories(String[] stories)
 		throws IOException, ClientProtocolException, ConnectorException;

 	public TaskList retrieveTasks(StoryList stories)
 		throws IOException, ClientProtocolException, ConnectorException;

	/**
	 * Retrieve a resource and unmarshal it as a specified type.
	 * 
	 * This method retrieves the resource located at the specified URI.  The
	 * response is handed off to JAXB and the result is coerced into the
	 * requested type.
	 * 
	 * @param klass the class to coerce the response into
	 * @param uri the URI to retrieve the resource from
	 * @return the coerced instance
	 * @throws ClientProtocolException
	 *         thrown if retrieving the resource results in an HTTP error
	 * @throws IOException
	 *         thrown if a network error occurs while retrieving the resource
	 * @throws UnexpectedResponseException
	 *         thrown if the response cannot be unmarshalled as <tt>klass</tt>
	 */
	public <T> T retrieveURI(Class<T> klass, URI uri)
		throws ClientProtocolException, IOException, UnexpectedResponseException;

}
