package standup.connector;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

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
 	 * 
 	 * @throws IOException when a low-level IO operation fails
 	 * @throws ClientProtocolException when an error occurs in the protocol
 	 *         layer - e.g., a non-successful HTTP result code is returned 
 	 * @throws ConnectorException when an error occurs in the connector
 	 *         layer other than either a transport or IO layer failure
 	 */
 	public List<IterationStatus> listIterationsForProject(String projectName)
 		throws IOException, JAXBException, ClientProtocolException,
 		       ConnectorException;

 	/**
 	 * Retrieves the list of iterations that a user is involved in.
 	 * @param userName the user to search for.
 	 * @return The list of iterations that the user is associated with.
 	 *
 	 * @throws IOException when a low-level IO operation fails
 	 * @throws ClientProtocolException when an error occurs in the protocol
 	 *         layer - e.g., a non-successful HTTP result code is returned 
 	 * @throws ConnectorException when an error occurs in the connector
 	 *         layer other than either a transport or IO layer failure
 	 */
 	public List<IterationStatus> listIterationsInvolvingUser(String userName)
		throws IOException, ClientProtocolException, ConnectorException;

 	/**
 	 * Retrieve a list of stories for a named iteration.
 	 * 
 	 * This method fetches all user stories that are currently assigned to
 	 * the specified iteration.  Most Agile tools separate backlog items and
 	 * defects into separate classes.  The domain model differs -- defects and
 	 * backlog items are both considered to be stories so you might consider
 	 * the result list to be heterogeneous.
 	 * 
 	 * @param iterationName  retrieve stories associated with this iteration
 	 * 
 	 * @return A list of user stories associated with the iteration.
 	 * 
 	 * @throws IOException when a low-level IO operation fails
 	 * @throws ClientProtocolException when an error occurs in the protocol
 	 *         layer - e.g., a non-successful HTTP result code is returned 
 	 * @throws ConnectorException when an error occurs in the connector
 	 *         layer other than either a transport or IO layer failure
 	 * @throws TransformerException when an XSLT exception is thrown while
 	 *         transforming the backend result into the model.
 	 */
 	public StoryList retrieveStoriesForIteration(String iterationName)
 		throws IOException, ClientProtocolException, ConnectorException,
 		       TransformerException;

 	/**
 	 * Retrieve a list of stories by their backend identifier.
 	 * 
 	 * This method fetches user stories based solely on the identifiers that
 	 * the backend has assigned.  For example, Rally assigns IDs starting with
 	 * 'US' for user stories and 'DE' for defects.  To retrieve a Rally story,
 	 * you would use identifiers like 'US123' or 'DE42'.
 	 * 
 	 * Note that the order of stories in the result set is not necessarily
 	 * tied to the order of the {@code stories} parameter.
 	 * 
 	 * @param stories the identifiers to retrieve from the backend
 	 * 
 	 * @return A list of story domain objects that match the identifiers
 	 *         passed in as parameters.
 	 *         
 	 * @throws IOException when a low-level IO operation fails
 	 * @throws ClientProtocolException when an error occurs in the protocol
 	 *         layer - e.g., a non-successful HTTP result code is returned 
 	 * @throws ConnectorException when an error occurs in the connector
 	 *         layer other than either a transport or IO layer failure
 	 * @throws TransformerException when an XSLT exception is thrown while
 	 *         transforming the backend result into the model.
 	 */
 	public StoryList retrieveStories(String[] stories)
 		throws IOException, ClientProtocolException, ConnectorException,
 		       TransformerException;

 	/**
 	 * Fetch the tasks associated with a bunch of stories.
 	 * 
 	 * This method retrieves the tasks for each of the input stories and
 	 * returns them as a single list.
 	 * 
 	 * @param stories retrieve the tasks for this set of stories
 	 * 
 	 * @return A list of all of the tasks associated with the requested
 	 *         stories in an undefined order.
 	 * 
 	 * @throws IOException when a low-level IO operation fails
 	 * @throws ClientProtocolException when an error occurs in the protocol
 	 *         layer - e.g., a non-successful HTTP result code is returned 
 	 * @throws ConnectorException when an error occurs in the connector
 	 *         layer other than either a transport or IO layer failure
 	 * @throws TransformerException when an XSLT exception is thrown while
 	 *         transforming the backend result into the model.
 	 */
 	public TaskList retrieveTasks(StoryList stories)
 		throws IOException, ClientProtocolException, ConnectorException,
 		       TransformerException;

	/**
	 * Retrieve a resource and unmarshal it as a specified type.
	 * 
	 * This method retrieves the resource located at the specified URI.  The
	 * response is handed off to JAXB and the result is coerced into the
	 * requested type.
	 * 
	 * @param klass the class to coerce the response into
	 * @param uri the URI to retrieve the resource from
	 * 
	 * @return the coerced instance
	 * @throws ClientProtocolException
	 *         thrown if retrieving the resource results in an HTTP error
	 * @throws IOException
	 *         thrown if a network error occurs while retrieving the resource
	 * @throws UnexpectedResponseException
	 *         thrown if the response cannot be unmarshalled as
	 *         <code>klass</code>
 	 * @throws TransformerException when an XSLT exception is thrown while
 	 *         transforming the backend result into the model.
	 */
	public <T> T retrieveURI(Class<T> klass, URI uri)
		throws ClientProtocolException, IOException, TransformerException,
		       UnexpectedResponseException;

	/**
	 * Attempts a simple query to verify that the user credentials are correct.
	 *  
	 * @return true if the login succeeded, false otherwise
	 * @throws ClientProtocolException
	 *         thrown if retrieving the resource results in an HTTP error
	 * @throws IOException
	 *         thrown if a network error occurs while retrieving the resource
	 */
	public boolean login() throws ClientProtocolException, IOException;

}
