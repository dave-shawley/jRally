package standup.application;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;

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
					.withLongOpt("user")
					.hasArg().withArgName("USER").isRequired()
					.withDescription("connect to Rally with the user name USER")
					.create("u"));
		opts.addOption(
				OptionBuilder
					.withLongOpt("password")
					.hasArg().withArgName("password").isRequired()
					.withDescription("use this password when connecting to Rally")
					.create("p"));
		return opts;
	}

	private void run(String[] args) throws Exception {
		CommandLine parsed = null;
		Options opts = buildOptions();
		try {
			Parser cmdLineParser = new PosixParser();
			parsed = cmdLineParser.parse(opts, args, true);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}

		ServerConnection rallyServer = new ServerConnection(Constants.RALLY_SERVER_NAME, new DefaultHttpClientFactory());
		rallyServer.setUsername(parsed.getOptionValue("user"));
		rallyServer.setPassword(parsed.getOptionValue("password"));

		StoryList stories = rallyServer.retrieveStories(new String[]{"US4270", "US4182", "US4188", "US2888"});
		System.out.format("Found %d stories", stories.getStory().size());
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
