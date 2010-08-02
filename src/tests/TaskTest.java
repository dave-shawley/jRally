/**
 * 
 */
package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import standup.model.Task;

/**
 * Test the Task class.
 */
public class TaskTest {

	private static final double ESTIMATE_ADJUSTMENT = 1.5;
	private static final double EFFORT_ADJUSTMENT = 1.0;
	private static final double TODO_ADJUSTMENT = 5.0;
	private static final String TASK_NAME = "A Task";
	private static final double DETAILED_ESTIMATE = 10.0;
	private Task aTask = null;

	@Before
	public void setUp() {
		aTask = new Task(TASK_NAME, DETAILED_ESTIMATE);
	}

	/**
	 * Test method for {@link standup.model.Task#getTaskName()}.
	 */
	@Test
	public void testGetTaskName() {
		assertEquals(TASK_NAME, aTask.getTaskName());
	}

	/**
	 * Test method for {@link standup.model.Task#getDetailedEstimate()}.
	 */
	@Test
	public void testGetDetailedEstimate() {
		assertEquals(DETAILED_ESTIMATE, aTask.getDetailedEstimate(), Double.MIN_VALUE);
	}

	/**
	 * Test method for {@link standup.model.Task#setTaskName(java.lang.String)}.
	 */
	@Test
	public void testSetTaskName() {
		aTask.setTaskName("Another Task");
		assertEquals("Another Task", aTask.getTaskName());
	}

	/**
	 * Test method for {@link standup.model.Task#getToDo()}.
	 */
	@Test
	public void testGetToDo() {
		double startingToDo = aTask.getToDo();
		assertEquals(aTask.getDetailedEstimate(), aTask.getToDo(), Double.MIN_VALUE);
		aTask.setDetailedEstimate(aTask.getDetailedEstimate() + ESTIMATE_ADJUSTMENT);
		// To do is independent of detailed estimate.
		assertEquals(startingToDo, aTask.getToDo(), Double.MIN_VALUE);
		
		aTask.increaseToDo(TODO_ADJUSTMENT);
		assertEquals(startingToDo + TODO_ADJUSTMENT, aTask.getToDo(), Double.MIN_VALUE);
		aTask.decreaseToDo(aTask.getToDo());
		assertEquals(0.0, aTask.getToDo(), Double.MIN_VALUE);
		
		// Detailed estimate is independent of to do.
		assertEquals(DETAILED_ESTIMATE + ESTIMATE_ADJUSTMENT, aTask.getDetailedEstimate(),
				     Double.MIN_VALUE);
	}

	@Test
	public void testStateChanges() {
		TestObserver<Task,Task.State> tester = new TestObserver<Task,Task.State>(
				Task.State.NOT_STARTED, // initial state
				Task.State.IN_PROGRESS, Task.State.WAITING, Task.State.BLOCKED,
				Task.State.IN_PROGRESS, Task.State.FINISHED, Task.State.IN_PROGRESS,
				Task.State.FINISHED
				);
		aTask.addObserver(tester);
		// the first few are simple state change tests
		aTask.setState(Task.State.IN_PROGRESS);
		aTask.setState(Task.State.WAITING);
		aTask.setState(Task.State.BLOCKED);
		aTask.setState(Task.State.IN_PROGRESS);
		aTask.setState(Task.State.FINISHED);
		// once a task is finished, it's To Do becomes zero.
		assertEquals(0.0, aTask.getToDo(), Double.MIN_VALUE);
		// increasing it's To Do moves the task back into progress.
		aTask.increaseToDo(TODO_ADJUSTMENT);
		assertEquals(Task.State.IN_PROGRESS, aTask.getState());
		// decreasing it's To Do to zero does not affect it's state
		aTask.decreaseToDo(aTask.getToDo());
		assertEquals(Task.State.IN_PROGRESS, aTask.getState());
		// complete the cycle
		aTask.setState(Task.State.FINISHED);
		assertTrue(tester.isFinished());
		// this will cause an assertion if we are notified even
		// though the state hasn't changed.
		aTask.setState(Task.State.FINISHED);
		// make sure that `removeObserver' code is executed somewhere
		aTask.removeObserver(tester);
	}

	@Test
	public void testEffortApplied() {
		double todo = aTask.getToDo();
		double initialEffort = aTask.getEffortApplied();
		assertEquals(Task.State.NOT_STARTED, aTask.getState());
		aTask.applyEffort(EFFORT_ADJUSTMENT);
		// applying effort forces task to `in progress'
		assertEquals(Task.State.IN_PROGRESS, aTask.getState());
		// applying effort does not affect to do
		assertEquals(todo, aTask.getToDo(), Double.MIN_VALUE);
		// applying effort adjusts effort applied
		assertEquals(initialEffort+EFFORT_ADJUSTMENT, aTask.getEffortApplied(), Double.MIN_VALUE);
		// ensure that applyEffort is not overwriting the effort applied
		aTask.applyEffort(EFFORT_ADJUSTMENT);
		assertEquals(initialEffort+2.0, aTask.getEffortApplied(), Double.MIN_VALUE);
	}

	//
	// The remaining tests will be marked as "not covered" by Emma
	// and other code coverage tools.  This is expected since an
	// exception is thrown immediately.
	//
	@Test(expected=IllegalArgumentException.class)
	public void increaseToDoByNegativeNumber() {
		aTask.increaseToDo(-1.0);
	}

	@Test(expected=IllegalArgumentException.class)
	public void decreaseToDoByNegativeNumber() {
		aTask.decreaseToDo(-1.0);
	}

}
