package standup.application;

import java.io.BufferedReader;
import java.io.Console;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import standup.connector.rally.Constants;
import standup.connector.rally.RallyClientFactory;
import standup.connector.rally.ServerConnection;
import standup.xml.StoryList;
import standup.xml.StoryType;
import standup.xml.TaskList;
import standup.xml.TaskType;


public abstract class RetrieveStories {
	protected static final String DEBUG_PREFIX_KEY = "debug-prefix";
	protected static final String PASSWORD_KEY = "password";
	protected static final String USER_KEY = "user";
	protected static final String VERBOSE_KEY = "verbose";
	protected static final String HELP_KEY = "help";
	protected static final String TASK_FILE_KEY = "task-file";
	protected static final String STORY_FILE_KEY = "story-file";

	private final static Logger logger = Logger.getLogger(RetrieveStories.class);
	private String userName = null;
	private String password = null;
	private String storyFilename = null;
	private String taskFilename = null;
	private String debugPrefix = null;

	@SuppressWarnings("static-access")
	protected Options buildOptions() {
		// Note that the various .isRequired() specifiers are commented
		// out.  We want to allow the --help option by itself.  If we
		// let the cli package handle it, you get a rather non-informative
		// error message back that only includes the short options... ick.
		Options options = new Options();
		options.addOption(
			OptionBuilder
				.withLongOpt(HELP_KEY)
				.withDescription("show this help summary")
				.create('h'));
		options.addOption(
			OptionBuilder
				.withLongOpt(VERBOSE_KEY)
				.withDescription("show debug diagnostics")
				.create('v'));
		options.addOption(
			OptionBuilder
				.withLongOpt(USER_KEY)
				.hasArg().withArgName("USER") //.isRequired()
				.withDescription("connect to Rally with the user name USER")
				.create('u'));
		options.addOption(
			OptionBuilder
				.withLongOpt(PASSWORD_KEY)
				.hasArg().withArgName("PASSWORD") //.isRequired()
				.withDescription("use this password when connecting to Rally")
				.create('p'));
		options.addOption(
				OptionBuilder
					.withLongOpt(STORY_FILE_KEY)
					.hasArg().withArgName("FILE")
					.withDescription("use this name for the story cards PDF")
					.create('s'));
		options.addOption(
				OptionBuilder
					.withLongOpt(TASK_FILE_KEY)
					.hasArg().withArgName("FILE")
					.withDescription("use this name for the task cards PDF")
					.create('t'));
		options.addOption(
				OptionBuilder
					.withLongOpt(DEBUG_PREFIX_KEY)
					.hasArg().withArgName("FILE")
					.withDescription("use this as a name prefix for intermediate XML files")
					.create('d'));
		return options;
	}

	protected void run(String[] args) throws Exception {
		Options opts = buildOptions();
		CommandLine parsedCmdLine = null;
		try {
			Parser cmdLineParser = new GnuParser();
			parsedCmdLine = cmdLineParser.parse(opts, args, true);
			if (!processOptions(parsedCmdLine)) {
				showHelp(opts);
				return;
			}
		} catch (MissingOptionException e) {
			e.printStackTrace();
			return;
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}

		ServerConnection rallyServer = new ServerConnection(
				Constants.RALLY_SERVER_NAME, new RallyClientFactory());
		rallyServer.setUsername(this.userName);
		rallyServer.setPassword(this.password);
		
		StoryList stories = fetchStories(rallyServer);
		processStories(stories);
		if (stories.getStory().size() > 0 && getTaskFilename() != null) {
			TaskList tasks = fetchTasks(rallyServer, stories);
			processTasks(tasks);
		}
	}

	/**
	 * @param opts
	 */
	protected void showHelp(Options opts) {
		HelpFormatter formatter = new HelpFormatter();
		String myName = this.getClass().getCanonicalName();
		formatter.printHelp(myName + " [options] story-id...", opts);
	}
	
	protected String retrieveOption(CommandLine parsedCmdLine, String optionKey,
			boolean hideInput) throws MissingOptionException
	{
		String result = null;
		if (parsedCmdLine.hasOption(optionKey)) {
			result = parsedCmdLine.getOptionValue(optionKey);
		} else if (System.console() != null) {
			Console cons = System.console();
			if (hideInput) {
				char[] buf = cons.readPassword("%s: ", optionKey);
				result = new String(buf);
			} else {
				result = cons.readLine("%s: ", optionKey);
			}
		} else {
			logger.warn("failed to acquire system console, using System.in/out instead.");
			System.out.print(optionKey+": ");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			try {
				result = reader.readLine();
			} catch (IOException e) {
				logger.error("failed to read input", e);
				throw new MissingOptionException(String.format("--%s is a required option", optionKey));
			}
		}
		return result.trim();
	}

	protected boolean processOptions(CommandLine parsedCmdLine) throws Exception {
		if (parsedCmdLine.hasOption(HELP_KEY)) {
			return false;
		}
		if (parsedCmdLine.hasOption(VERBOSE_KEY)) {
			Logger.getRootLogger().setLevel(Level.DEBUG);
		}

		this.userName = retrieveOption(parsedCmdLine, USER_KEY, false);
		this.password = retrieveOption(parsedCmdLine, PASSWORD_KEY, true);
		this.storyFilename = retrieveOption(parsedCmdLine, STORY_FILE_KEY, false);
		this.taskFilename = retrieveOption(parsedCmdLine, TASK_FILE_KEY, false);
		if (this.userName.isEmpty() || this.password.isEmpty()) {
			throw new MissingOptionException(String.format(
					"both %s and %s are required", USER_KEY, PASSWORD_KEY));
		}
		if (this.storyFilename.isEmpty() && this.taskFilename.isEmpty()) {
			throw new MissingOptionException(String.format(
					"one of %s or %s is required", STORY_FILE_KEY, TASK_FILE_KEY));
		}

		this.debugPrefix = parsedCmdLine.getOptionValue(DEBUG_PREFIX_KEY, null);

		return true;
	}

	abstract protected StoryList fetchStories(standup.connector.ServerConnection server) throws Exception;

	protected TaskList fetchTasks(standup.connector.ServerConnection server, StoryList stories) throws Exception {
		return server.retrieveTasks(stories);
	}

	protected void processStories(StoryList stories) throws Exception {
		List<StoryType> storyList = stories.getStory();
		String pdfFilename = getStoryFilename();
		if (storyList.size() == 0) {
			logger.info("No stories found");
		} else if (pdfFilename != null) {
			logger.info(String.format("Found %d stories, writing output to '%s'",
					storyList.size(), pdfFilename));
			FileOutputStream fos;
			fos = new FileOutputStream(pdfFilename);
			Formatter formatter = new Formatter();
			formatter.writeToPDF(stories, fos, debugPrefix);
			fos.close();
		} else {
			logger.info(String.format("Found %d stories, no story file specified",
					storyList.size()));
		}
	}

	protected void processTasks(TaskList tasks) throws Exception {
		List<TaskType> taskList = tasks.getTask();
		String pdfFilename = getTaskFilename();
		if (taskList.size() == 0) {
			logger.info("No tasks found");
		} else if (pdfFilename != null) {
			logger.info(String.format("Found %d tasks, writing output to '%s'",
					taskList.size(), pdfFilename));
			FileOutputStream fos = new FileOutputStream(pdfFilename);
			Formatter formatter = new Formatter();
			formatter.writeToPDF(tasks, fos, debugPrefix);
			fos.close();
		} else {
			logger.info(String.format("found %d tasks, no task file specified",
					taskList.size()));
		}
	}

	/**
	 * @return the storyFilename
	 */
	protected String getStoryFilename() {
		if (storyFilename.isEmpty()) {
			return null;
		}
		return storyFilename;
	}

	/**
	 * @return the taskFilename
	 */
	protected String getTaskFilename() {
		if (taskFilename.isEmpty()) {
			return null;
		}
		return taskFilename;
	}

	/**
	 * @return the debugPrefix
	 */
	protected String getDebugPrefix() {
		return debugPrefix;
	}

}
