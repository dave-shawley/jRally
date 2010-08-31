package standup.application;

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

public abstract class RetrieveStories {
	private final static Logger logger = Logger.getLogger(RetrieveStories.class);

	@SuppressWarnings("static-access")
	protected Options buildOptions() {
		Options options = new Options();
		options.addOption(
			OptionBuilder
				.withLongOpt("help")
				.withDescription("show this help summary")
				.create("h"));
		options.addOption(
			OptionBuilder
				.withLongOpt("verbose")
				.withDescription("show debug diagnostics")
				.create("v"));
		options.addOption(
			OptionBuilder
				.withLongOpt("user")
				.hasArg().withArgName("USER").isRequired()
				.withDescription("connect to Rally with the user name USER")
				.create("u"));
		options.addOption(
			OptionBuilder
				.withLongOpt("password")
				.hasArg().withArgName("PASSWORD").isRequired()
				.withDescription("use this password when connecting to Rally")
				.create("p"));
		return options;
	}

	abstract protected void processResults(CommandLine parsed, StoryList stories) throws Exception;

	protected void run(String[] args) throws Exception {
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

		ServerConnection rallyServer = new ServerConnection(Constants.RALLY_SERVER_NAME,
				new DefaultHttpClientFactory());
		rallyServer.setUsername(parsed.getOptionValue("user"));
		rallyServer.setPassword(parsed.getOptionValue("password"));
		processResults(parsed, rallyServer.retrieveStories(parsed.getArgs()));
	}

}
