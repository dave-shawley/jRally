package standup.application;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
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

	public void writeToPDF(StoryList stories, FileOutputStream pdfFile) {
		OutputStream outStream = new BufferedOutputStream(pdfFile);
		Fop fop;
		try {
			fop = fopFactory.newFop(MimeConstants.MIME_PDF, outStream);
			JAXBSource sourceDoc = new JAXBSource(this.jaxb, stories);
			Utilities.runXSLT(new SAXResult(fop.getDefaultHandler()),
					"xslt/story-cards.xsl", logger, this.jaxb, sourceDoc,
					xformerFactory);
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeToPDF(TaskList tasks, FileOutputStream pdfFile) {
		OutputStream outStream = new BufferedOutputStream(pdfFile);
		Fop fop;
		try {
			fop = fopFactory.newFop(MimeConstants.MIME_PDF, outStream);
			JAXBSource sourceDoc = new JAXBSource(this.jaxb, tasks);
			Utilities.runXSLT(new SAXResult(fop.getDefaultHandler()),
					"xslt/story-cards.xsl", logger, this.jaxb, sourceDoc,
					xformerFactory);
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
