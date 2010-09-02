package standup.application;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

import standup.connector.ServerConnection;
import standup.xml.StoryList;


public class RetrieveStoriesByID extends RetrieveStories {
	final static Logger logger = Logger.getLogger(RetrieveStoriesByID.class);
	private String[] storyIdList = null;
	
	@Override
	protected boolean processOptions(CommandLine parsedCmdLine) throws Exception {
		if (!super.processOptions(parsedCmdLine)) {
			return false;
		}
		this.storyIdList = parsedCmdLine.getArgs();
		return true;
	}

	@Override
	protected StoryList fetchStories(ServerConnection server) throws Exception {
		return server.retrieveStories(this.storyIdList);
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
