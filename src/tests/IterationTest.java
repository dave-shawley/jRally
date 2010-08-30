package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

import standup.model.Iteration;
import standup.model.UserStory;

public class IterationTest {

	private static final String ITERATION_NAME = "An Iteration";
	private Date ITERATION_START;
	private Date ITERATION_END;
	private UserStory[] STORIES;

	private Iteration anIteration;

	@Before
	public void setUp() throws Exception {
		Calendar cal = new GregorianCalendar();

		// clear out the time related fields, this will roll back
		// to midnight of the current day
		cal.clear(Calendar.HOUR_OF_DAY);
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);
		
		// roll back by one day and use that as the start date
		cal.add(Calendar.DAY_OF_MONTH, -1);
		ITERATION_START = cal.getTime();
		// now roll forward by two days and use that as the end date
		cal.add(Calendar.DAY_OF_MONTH, 2);
		ITERATION_END = cal.getTime();

		STORIES = new UserStory[] {
				new UserStory("story 1"),
				new UserStory("story 2"),
				new UserStory("story 3")
		};

		anIteration = new Iteration(ITERATION_NAME, ITERATION_START, ITERATION_END);
	}

	private void addStories() {
		for (UserStory u : STORIES) {
			anIteration.addStory(u);
		}
	}

	@Test
	public void testAddStory() {
		addStories();
		assertEquals(STORIES.length, anIteration.getStoryCount());
	}

	@Test
	public void testGetStory() {
		addStories();
		int index = 0;
		for (UserStory u : STORIES) {
			UserStory v = anIteration.getStory(index);
			assertEquals(u, v);
			++index;
		}
	}

	@Test
	public void testIterator() {
		addStories();
		int index = 0;
		for (UserStory u : anIteration) {
			assertEquals(STORIES[index], u);
			++index;
		}
	}

	@Test
	public void testSetName() {
		String newName = anIteration.getName();
		newName.toUpperCase();
		anIteration.setName(newName);
		assertEquals(newName, anIteration.getName());
	}

	@Test
	public void testGetName() {
		assertEquals(ITERATION_NAME, anIteration.getName());
	}

	@Test
	public void testSetStartDate() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(anIteration.getStartDate());
		cal.add(Calendar.MONTH, -1);
		anIteration.setStartDate(cal.getTime());
		assertEquals(cal.getTime(), anIteration.getStartDate());
	}

	@Test
	public void testGetStartDate() {
		assertEquals(ITERATION_START, anIteration.getStartDate());
	}

	@Test
	public void testSetEndDate() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(anIteration.getEndDate());
		cal.add(Calendar.MONTH, 1);
		anIteration.setEndDate(cal.getTime());
		assertEquals(cal.getTime(), anIteration.getEndDate());
	}

	@Test
	public void testGetEndDate() {
		assertEquals(ITERATION_END, anIteration.getEndDate());
	}

	@Test
	public void testGetState() {
		// The initial state is "active" since the start date is
		// yesterday and the end data is the day after tomorrow.
		assertEquals(Iteration.State.ACTIVE, anIteration.getState());
	}

	@Test
	public void testIterationDates() {
		TestObserver<Iteration,Iteration.State> tester = new TestObserver<Iteration,Iteration.State>(
				Iteration.State.ACTIVE,
				Iteration.State.PLANNED,
				Iteration.State.ACTIVE,
				Iteration.State.COMPLETED
				);
		anIteration.addObserver(tester);

		// Roll the dates forward so that the iteration will
		// become active in a few days.
		Calendar cal = new GregorianCalendar();
		cal.clear(Calendar.HOUR);
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);
		Calendar saved = (Calendar) cal.clone();

		// Roll forward by a few days and use this as the end
		cal.add(Calendar.DATE, 4);
		anIteration.setEndDate(cal.getTime());

		// Now roll it back by two days and use this as the start.
		// This will set the iteration's state to PLANNED since
		// it is officially in the future when the start is moved.
		cal.add(Calendar.DATE, -2);
		anIteration.setStartDate(cal.getTime());

		// Start with the cleared version of today and roll it
		// back a few days.  Then set it as the start.  This will
		// reset the state to ACTIVE again.
		cal = saved;
		cal.add(Calendar.DATE, -3);
		anIteration.setStartDate(cal.getTime());

		// Now set the end date to a few days ago.  This will reset
		// the state to COMPLETED.
		cal.add(Calendar.DATE, 2);
		anIteration.setEndDate(cal.getTime());

		assertTrue(tester.isFinished());
		anIteration.removeObserver(tester);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testInvalidStartDate() {
		Calendar laterThanEnd = new GregorianCalendar();
		laterThanEnd.setTime(anIteration.getEndDate());
		laterThanEnd.add(Calendar.DATE, 1);
		anIteration.setStartDate(laterThanEnd.getTime());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testInvalidEndDate() {
		Calendar earlierThanStart = new GregorianCalendar();
		earlierThanStart.setTime(anIteration.getStartDate());
		earlierThanStart.add(Calendar.DATE, -1);
		anIteration.setEndDate(earlierThanStart.getTime());
	}

}
