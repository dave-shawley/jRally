package standup.application;

import java.io.FileOutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import standup.connector.DefaultHttpClientFactory;
import standup.connector.rally.Constants;
import standup.connector.rally.ServerConnection;
import standup.xml.StoryList;

public class RetrieveStories {

	@SuppressWarnings("static-access")
	private Options buildOptions() {
		Options opts = new Options();
		opts.addOption(
			OptionBuilder
				.withLongOpt("help")
				.withDescription("show this help summary")
				.create("h"));
		opts.addOption(
			OptionBuilder
				.withLongOpt("verbose")
				.withDescription("show debug diagnostics")
				.create("v"));
		opts.addOption(
			OptionBuilder
				.withLongOpt("user")
				.hasArg().withArgName("USER").isRequired()
				.withDescription("connect to Rally with the user name USER")
				.create("u"));
		opts.addOption(
			OptionBuilder
				.withLongOpt("password")
				.hasArg().withArgName("PASSWORD").isRequired()
				.withDescription("use this password when connecting to Rally")
				.create("p"));
		return opts;
	}

	private void run(String[] args) throws Exception {
		CommandLine parsed = null;
		Options opts = buildOptions();
		try {
			Parser cmdLineParser = new GnuParser();
			parsed = cmdLineParser.parse(opts, args, true);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}

		if (parsed.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			String myName = this.getClass().getCanonicalName();
			formatter.printHelp(myName + " [options] story-id...", opts);
			return;
		}
		if (parsed.hasOption("verbose")) {
			Logger.getRootLogger().setLevel(Level.DEBUG);
		}

		ServerConnection rallyServer = new ServerConnection(Constants.RALLY_SERVER_NAME, new DefaultHttpClientFactory());
		rallyServer.setUsername(parsed.getOptionValue("user"));
		rallyServer.setPassword(parsed.getOptionValue("password"));

		StoryList stories = rallyServer.retrieveStories(parsed.getArgs());
		System.out.format("Found %d stories", stories.getStory().size());
		
		FileOutputStream fos = new FileOutputStream("stories.pdf");
		Formatter formatter = new Formatter();
		formatter.writeToPDF(stories, fos);
		fos.close();
	}

	public static void main(String[] args) {
		try {
			RetrieveStories app = new RetrieveStories();
			app.run(args);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

}
