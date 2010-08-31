package standup.application;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import standup.xml.StoryList;


public class Formatter {
	private final FopFactory fopFactory;
	private final TransformerFactory xformerFactory;
	private final JAXBContext jaxb;

	public Formatter() throws JAXBException {
		fopFactory = FopFactory.newInstance();
		xformerFactory = TransformerFactory.newInstance();
		jaxb = JAXBContext.newInstance("standup.xml");
	}

	public void writeToPDF(StoryList stories, FileOutputStream pdfFile) {
		OutputStream outStream = new BufferedOutputStream(pdfFile);
		Fop fop;
		try {
			fop = fopFactory.newFop(MimeConstants.MIME_PDF, outStream);
			StreamSource xsltStream = new StreamSource(ClassLoader.getSystemResourceAsStream("xslt/story-cards.xsl"));
			Transformer xformer = xformerFactory.newTransformer(xsltStream);
			JAXBSource sourceDoc = new JAXBSource(this.jaxb, stories);
			SAXResult resultDoc = new SAXResult(fop.getDefaultHandler());
			xformer.transform(sourceDoc, resultDoc);
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
