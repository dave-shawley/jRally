package standup.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;

import org.apache.http.HttpHost;
import org.apache.log4j.Logger;
import org.junit.Test;


public class UtilitiesTest
{

	@Test
	public void testGenerateExceptionWithObjectArray()
	{
		boolean exceptionCaught = false;
		try {
			throw Utilities.generateException(SimpleException.class, "message body", "1", 2, 3.0);
		} catch (SimpleException e) {
			exceptionCaught = true;
			assertEquals("message body: 1 2 3.0", e.getMessage());
			assertNull(e.getCause());
		}
		if (!exceptionCaught) {
			fail("expected exception, none was thrown");
		}
	}

	@Test(expected=Error.class)
	public void testGenerateExceptionWithEvilExceptionClass() throws SimpleException
	{
		throw Utilities.generateException(SimpleException.class,
			SimpleException.Flags.THROW_ERROR.toString());
	}

	@Test
	public void testGenerateExceptionWithObjectArrayAndCause()
	{
		Throwable cause = new NumberFormatException();
		boolean exceptionCaught = false;
		try {
			throw Utilities.generateException(SimpleException.class, cause, "message body", "1", 2, 3.0);
		} catch (SimpleException e) {
			exceptionCaught = true;
			assertEquals("message body: 1 2 3.0", e.getMessage());
			assertSame(cause, e.getCause());
		}
		if (!exceptionCaught) {
			fail("expected exception, none was thrown");
		}
	}

	@Test
	public void testJoin()
	{
		String result = Utilities.join("");
		assertEquals("", result);
		result = Utilities.join(":", 1, 2, 3, new Object() {
			@Override public String toString() { return "FOO"; }
		}, "hello ", " world");
		assertEquals("1:2:3:FOO:hello : world", result);
	}

	@Test
	public void testCreateSimpleURI() throws URISyntaxException
	{
		URI expected = new URI("http://www.google.com/?q=foo");
		URI created = Utilities.createURI(new HttpHost("www.google.com"), null, "q=foo");
		assertEquals(expected, created);
	}

	@Test
	public void testCreateHttpsURI() throws URISyntaxException {
		URI expected = new URI("https://rally1.rallydev.com/slm/doc/webservice/index.jsp?version=1.17");
		URI created = Utilities.createURI(new HttpHost("rally1.rallydev.com", -1, "https"), "/slm/doc/webservice/index.jsp", "version=1.17");
		assertEquals(expected, created);
	}

	@Test
	public void testCreateHttpsUriWithPort() throws URISyntaxException {
		URI expected = new URI("https://rally1.rallydev.com:443/slm/doc/webservice/index.jsp?version=1.17");
		URI created = Utilities.createURI(new HttpHost("rally1.rallydev.com", 443, "https"), "slm/doc/webservice/index.jsp", "version=1.17");
		assertEquals(expected, created);
	}
	
	@Test(expected=TransformerException.class)
	public void verifyRunXsltThrowsWithInvalidResource() throws TransformerException
	{
		SAXResult result = null;
		JAXBSource source = null;
		TransformerFactory factory = null;
		Utilities.runXSLT(result, "non-existant/no-file", Logger.getLogger(Utilities.class), source, factory);
	}

}
