package standup.application;

import java.io.FileOutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import standup.xml.StoryList;

public class RetrieveStoriesByID extends RetrieveStories {
	private final static Logger logger = Logger.getLogger(RetrieveStoriesByID.class);

	@Override
	@SuppressWarnings("static-access")
	protected Options buildOptions() {
		Options options = super.buildOptions();
		options.addOption(
				OptionBuilder
					.withLongOpt("output-file")
					.hasArg().withArgName("FILE")
					.withDescription("name the PDF file this")
					.create("o"));
		return options;
	}

	@Override
	protected void processResults(CommandLine parsed, StoryList stories) throws Exception {
		String pdfFilename = parsed.getOptionValue("output-file", "stories.pdf");
		logger.info(String.format("Found %d stories, writing output to '%s'",
				                  stories.getStory().size(), pdfFilename));
		FileOutputStream fos;
		fos = new FileOutputStream(pdfFilename);
		Formatter formatter = new Formatter();
		formatter.writeToPDF(stories, fos);
		fos.close();
	}

	public static void main(String[] args) {
		try {
			RetrieveStories app = new RetrieveStoriesByID();
			app.run(args);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

}
