package standup.connector;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.http.client.ClientProtocolException;

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

	public <T> T retrieveURI(Class<T> klass, URI uri)
		throws ClientProtocolException, IOException, UnexpectedResponseException;

}
