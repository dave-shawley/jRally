package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import standup.model.Task;
import standup.model.UserStory;

public class UserStoryTest {

	private static final String STORY_NAME = "Story Name";
	private UserStory aStory = null;

	@Before
	public void setUp() {
		aStory = new UserStory(STORY_NAME);
	}

	@Test
	public void basicTest() {
		assertEquals(STORY_NAME, aStory.getStoryName());
		assertEquals(0, aStory.getNumberOfTasks());
		assertEquals(0.0, aStory.getDetailedEstimate(), 0.0);
		assertEquals(0, aStory.getStoryPoints());
		assertEquals(UserStory.State.NOT_STARTED, aStory.getState());
	}

	@Test
	public void testDetailedEstimate() {
		aStory.addTask(new Task("Task 1", 4.0));
		aStory.addTask(new Task("Task 2", 5.0));
		assertEquals(9.0, aStory.getDetailedEstimate(), Double.MIN_VALUE);
		assertEquals(2, aStory.getNumberOfTasks());
		assertEquals(9.0, aStory.getToDo(), Double.MIN_VALUE);

		aStory.getTask(0).setDetailedEstimate(5.0);
		assertEquals(10.0, aStory.getDetailedEstimate(), Double.MIN_VALUE);
		// To Do is unaffected by changes in the detailed estimate
		assertEquals(9.0, aStory.getToDo(), Double.MIN_VALUE);
	}

	@Test
	public void testStoryPoints() {
		aStory.setStoryPoints(10);
		assertEquals(10, aStory.getStoryPoints());
	}

	@Test
	public void testStateChanges() {
		TestObserver<UserStory,UserStory.State> tester = new TestObserver<UserStory,UserStory.State>(
				UserStory.State.NOT_STARTED, // initial state
				UserStory.State.IN_PROGRESS,
				UserStory.State.COMPLETED,
				UserStory.State.IN_PROGRESS,
				UserStory.State.ACCEPTED
				);
		aStory.addObserver(tester);
		aStory.setState(UserStory.State.IN_PROGRESS);
		aStory.setState(UserStory.State.COMPLETED);
		// adding a new task moves the story back into IN_PROGRESS
		aStory.addTask(new Task("A Task", 1.0));
		aStory.setState(UserStory.State.ACCEPTED);
		assertTrue(tester.isFinished());
		aStory.removeObserver(tester);
	}
}
