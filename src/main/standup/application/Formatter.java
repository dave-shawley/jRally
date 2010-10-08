package standup.application;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.log4j.Logger;

import standup.utility.Utilities;
import standup.xml.StoryList;
import standup.xml.TaskList;


public class Formatter {
	private static final Logger logger = Logger.getLogger(Formatter.class);
	private final FopFactory fopFactory;
	private final TransformerFactory xformerFactory;
	private final JAXBContext jaxb;

	public Formatter() throws JAXBException {
		fopFactory = FopFactory.newInstance();
		xformerFactory = TransformerFactory.newInstance();
		jaxb = JAXBContext.newInstance("standup.xml");
	}

	private void dump(Object obj, String dumpFilePrefix) {
		String fileName = String.format("%s-%d.xml", dumpFilePrefix, obj.hashCode());
		try {
			Marshaller m = this.jaxb.createMarshaller();
			m.marshal(obj, new File(fileName));
		} catch (Exception e) {
			logger.error("failed to write intermediate file "+fileName, e);
		}
	}

	public void writeToPDF(StoryList stories, FileOutputStream pdfFile, String dumpFilePrefix) {
		OutputStream outStream = new BufferedOutputStream(pdfFile);
		Fop fop;
		try {
			if (dumpFilePrefix != null) {
				dump(stories, dumpFilePrefix);
			}
			fop = fopFactory.newFop(MimeConstants.MIME_PDF, outStream);
			JAXBSource sourceDoc = new JAXBSource(this.jaxb, stories);
			Utilities.runXSLT(new SAXResult(fop.getDefaultHandler()),
					"xslt/story-cards.xsl", logger, sourceDoc, xformerFactory);
			outStream.close();
		} catch (Exception e) {
			logger.error("failed to generate PDF from StoryList", e);
		}
	}

	public void writeToPDF(TaskList tasks, FileOutputStream pdfFile, String dumpFilePrefix) {
		OutputStream outStream = new BufferedOutputStream(pdfFile);
		Fop fop;
		try {
			if (dumpFilePrefix != null) {
				dump(tasks, dumpFilePrefix);
			}
			fop = fopFactory.newFop(MimeConstants.MIME_PDF, outStream);
			JAXBSource sourceDoc = new JAXBSource(this.jaxb, tasks);
			Utilities.runXSLT(new SAXResult(fop.getDefaultHandler()),
					"xslt/story-cards.xsl", logger, sourceDoc, xformerFactory);
			outStream.close();
		} catch (Exception e) {
			logger.error("failed to generate PDF from StoryList", e);
		}
	}

}
