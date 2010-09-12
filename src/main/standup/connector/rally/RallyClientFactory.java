package standup.connector.rally;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.http.client.methods.HttpUriRequest;

import standup.connector.DefaultHttpClientFactory;


/**
 * HTTP Client Factory for Rally connections.
 * <p>
 * This client factory ensures that requests include the HTTP headers that
 * the Rally API wants on each request.
 */
public class RallyClientFactory extends DefaultHttpClientFactory {
	private static final Map<String,String> HEADER_MAP;

	static {
		Properties sp = System.getProperties();
		HEADER_MAP = new HashMap<String,String>();
		HEADER_MAP.put("X-RallyIntegrationName", "jRally");
		HEADER_MAP.put("X-RallyIntegrationVendor",
				"git://github.com/dave-shawley");
		HEADER_MAP.put("X-RallyIntegrationVersion", "0.1");
		HEADER_MAP.put("X-RallyIntegrationOS",
				"Java by " + sp.getProperty("java.vm.vendor", "Unknown Vendor"));
		HEADER_MAP.put("X-RallyIntegrationPlatform",
				String.format("%s, %s, %s",
						sp.getProperty("java.vm.name", "Java"),
						sp.getProperty("java.vm.vendor", "Unknown Vendor"),
						sp.getProperty("java.vm.version", "Unknown Version")));
		HEADER_MAP.put("X-RallyIntegrationLibrary",
				"Rally REST API v." + Constants.RALLY_API_VERSION);
	}

	@Override
	protected void configureRequest(HttpUriRequest request) {
		super.configureRequest(request);
		for (String key: HEADER_MAP.keySet()) {
			request.addHeader(key, HEADER_MAP.get(key));
		}
	}

}
